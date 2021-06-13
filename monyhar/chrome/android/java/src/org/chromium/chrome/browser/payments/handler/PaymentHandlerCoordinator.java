// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.payments.handler;

import android.app.Activity;

import androidx.annotation.VisibleForTesting;

import org.monyhar.chrome.browser.WebContentsFactory;
import org.monyhar.chrome.browser.incognito.IncognitoUtils;
import org.monyhar.chrome.browser.payments.handler.toolbar.PaymentHandlerToolbarCoordinator;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.browser.tabmodel.TabModelSelectorSupplier;
import org.monyhar.chrome.browser.ui.TabObscuringHandler;
import org.monyhar.chrome.browser.ui.TabObscuringHandlerSupplier;
import org.monyhar.chrome.browser.version.ChromeVersionInfo;
import org.monyhar.components.browser_ui.bottomsheet.BottomSheetController;
import org.monyhar.components.browser_ui.bottomsheet.BottomSheetControllerProvider;
import org.monyhar.components.embedder_support.view.ContentView;
import org.monyhar.components.payments.PaymentHandlerNavigationThrottle;
import org.monyhar.components.thinwebview.ThinWebView;
import org.monyhar.components.thinwebview.ThinWebViewConstraints;
import org.monyhar.components.thinwebview.ThinWebViewFactory;
import org.monyhar.content_public.browser.LoadUrlParams;
import org.monyhar.content_public.browser.SelectionClient;
import org.monyhar.content_public.browser.SelectionPopupController;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.ui.base.ViewAndroidDelegate;
import org.monyhar.ui.base.WindowAndroid;
import org.monyhar.ui.modelutil.PropertyModel;
import org.monyhar.ui.modelutil.PropertyModelChangeProcessor;
import org.monyhar.url.GURL;

/**
 * PaymentHandler coordinator, which owns the component overall, i.e., creates other objects in the
 * component and connects them. It decouples the implementation of this component from other
 * components and acts as the point of contact between them. Any code in this component that needs
 * to interact with another component does that through this coordinator.
 */
public class PaymentHandlerCoordinator {
    private Runnable mHider;
    private WebContents mPaymentHandlerWebContents;
    private PaymentHandlerToolbarCoordinator mToolbarCoordinator;

    /** Constructs the payment-handler component coordinator. */
    public PaymentHandlerCoordinator() {}

    /** Observes the state changes of the payment-handler UI. */
    public interface PaymentHandlerUiObserver {
        /** Called when Payment Handler UI is closed. */
        void onPaymentHandlerUiClosed();
        /** Called when Payment Handler UI is shown. */
        void onPaymentHandlerUiShown();
    }

    /**
     * Shows the payment-handler UI.
     *
     * @param paymentRequestWebContents The WebContents of the merchant's frame.
     * @param url The url of the payment handler app, i.e., that of
     *         "PaymentRequestEvent.openWindow(url)".
     * @param isIncognito Whether the tab is in incognito mode.
     * @param uiObserver The {@link PaymentHandlerUiObserver} that observes this Payment Handler UI.
     * @return The WebContents of the payment handler that's just opened when the showing is
     *         successful; null if failed. When null is returned, caller should also call hide().
     */
    public WebContents show(WebContents paymentRequestWebContents, GURL url, boolean isIncognito,
            PaymentHandlerUiObserver uiObserver) {
        assert mHider == null : "Already showing payment-handler UI";
        assert paymentRequestWebContents != null;
        WindowAndroid windowAndroid = paymentRequestWebContents.getTopLevelNativeWindow();
        if (windowAndroid == null) return null;
        Activity activity = windowAndroid.getActivity().get();
        if (activity == null) return null;
        Profile profile = IncognitoUtils.getProfileFromWindowAndroid(windowAndroid, isIncognito);
        mPaymentHandlerWebContents =
                WebContentsFactory.createWebContents(profile, /*initiallyHidden=*/false);
        PaymentHandlerNavigationThrottle.markPaymentHandlerWebContents(mPaymentHandlerWebContents);
        ContentView webContentView = ContentView.createContentView(
                activity, null /* eventOffsetHandler */, mPaymentHandlerWebContents);
        initializeWebContents(windowAndroid, webContentView, url);

        mToolbarCoordinator = new PaymentHandlerToolbarCoordinator(
                activity, mPaymentHandlerWebContents, url, windowAndroid::getModalDialogManager);

        BottomSheetController bottomSheetController =
                BottomSheetControllerProvider.from(windowAndroid);
        Tab currentTab = TabModelSelectorSupplier.getCurrentTabFrom(windowAndroid);
        TabObscuringHandler tabObscuringHandler =
                TabObscuringHandlerSupplier.getValueOrNullFrom(windowAndroid);
        if (bottomSheetController == null || currentTab == null || tabObscuringHandler == null) {
            return null;
        }

        PropertyModel model = new PropertyModel.Builder(PaymentHandlerProperties.ALL_KEYS).build();
        PaymentHandlerMediator mediator = new PaymentHandlerMediator(model, this::hide,
                /*paymentRequestWebContents=*/paymentRequestWebContents,
                /*paymentHandlerWebContents*/ mPaymentHandlerWebContents, uiObserver,
                currentTab.getView(), mToolbarCoordinator.getToolbarHeightPx(),
                bottomSheetController, tabObscuringHandler, activity);
        activity.getWindow().getDecorView().addOnLayoutChangeListener(mediator);

        bottomSheetController.addObserver(mediator);
        mPaymentHandlerWebContents.addObserver(mediator);

        mToolbarCoordinator.setCloseButtonOnClickCallback(mediator::onToolbarCloseButtonClicked);
        ThinWebView thinWebView = ThinWebViewFactory.create(activity, new ThinWebViewConstraints());
        assert webContentView.getParent() == null;
        thinWebView.attachWebContents(mPaymentHandlerWebContents, webContentView, null);
        PaymentHandlerView view = new PaymentHandlerView(activity, mPaymentHandlerWebContents,
                mToolbarCoordinator.getView(), thinWebView.getView());
        assert mToolbarCoordinator.getToolbarHeightPx() == view.getToolbarHeightPx();
        PropertyModelChangeProcessor changeProcessor =
                PropertyModelChangeProcessor.create(model, view, PaymentHandlerViewBinder::bind);
        mHider = () -> {
            changeProcessor.destroy();
            bottomSheetController.removeObserver(mediator);
            bottomSheetController.hideContent(/*content=*/view, /*animate=*/true);
            uiObserver.onPaymentHandlerUiClosed();
            assert activity.getWindow() != null;
            assert activity.getWindow().getDecorView() != null;
            activity.getWindow().getDecorView().removeOnLayoutChangeListener(mediator);
            mediator.destroy();
            thinWebView.destroy();
            mPaymentHandlerWebContents.destroy();
        };
        boolean isShowSuccess = bottomSheetController.requestShowContent(view, /*animate=*/true);
        if (!isShowSuccess) return null;

        return mPaymentHandlerWebContents;
    }

    private void initializeWebContents(
            WindowAndroid windowAndroid, ContentView webContentView, GURL url) {
        mPaymentHandlerWebContents.initialize(ChromeVersionInfo.getProductVersion(),
                ViewAndroidDelegate.createBasicDelegate(webContentView), webContentView,
                windowAndroid, WebContents.createDefaultInternalsHolder());

        SelectionPopupController controller =
                SelectionPopupController.fromWebContents(mPaymentHandlerWebContents);
        controller.setActionModeCallback(
                new PaymentHandlerActionModeCallback(mPaymentHandlerWebContents));
        controller.setSelectionClient(
                SelectionClient.createSmartSelectionClient(mPaymentHandlerWebContents));

        mPaymentHandlerWebContents.getNavigationController().loadUrl(
                new LoadUrlParams(url.getSpec()));
    }

    /**
     * Get the WebContents of the Payment Handler for testing purpose. In other situations,
     * WebContents should not be leaked outside the Payment Handler.
     *
     * @return The WebContents of the Payment Handler.
     */
    @VisibleForTesting
    public WebContents getWebContentsForTest() {
        return mPaymentHandlerWebContents;
    }

    /** Hides the payment-handler UI. */
    public void hide() {
        if (mHider == null) return;
        mHider.run();
        mHider = null;
    }

    @VisibleForTesting
    public void clickSecurityIconForTest() {
        mToolbarCoordinator.clickSecurityIconForTest();
    }

    @VisibleForTesting
    public void clickCloseButtonForTest() {
        mToolbarCoordinator.clickCloseButtonForTest();
    }
}
