// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.features.start_surface;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.monyhar.base.supplier.OneshotSupplierImpl;
import org.monyhar.base.supplier.Supplier;
import org.monyhar.chrome.browser.compositor.layouts.Layout;
import org.monyhar.chrome.browser.compositor.layouts.LayoutRenderHost;
import org.monyhar.chrome.browser.compositor.layouts.LayoutUpdateHost;
import org.monyhar.chrome.browser.compositor.layouts.content.TabContentManager;
import org.monyhar.chrome.browser.fullscreen.BrowserControlsManager;
import org.monyhar.chrome.browser.init.ChromeActivityNativeDelegate;
import org.monyhar.chrome.browser.lifecycle.ActivityLifecycleDispatcher;
import org.monyhar.chrome.browser.multiwindow.MultiWindowModeStateDispatcher;
import org.monyhar.chrome.browser.omnibox.OmniboxStub;
import org.monyhar.chrome.browser.share.ShareDelegate;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.browser.tabmodel.TabCreatorManager;
import org.monyhar.chrome.browser.tabmodel.TabModelSelector;
import org.monyhar.chrome.browser.ui.messages.snackbar.SnackbarManager;
import org.monyhar.components.browser_ui.bottomsheet.BottomSheetController;
import org.monyhar.components.browser_ui.widget.MenuOrKeyboardActionController;
import org.monyhar.components.browser_ui.widget.scrim.ScrimCoordinator;
import org.monyhar.ui.base.WindowAndroid;
import org.monyhar.ui.modaldialog.ModalDialogManager;
import org.monyhar.ui.resources.dynamics.DynamicResourceLoader;

/** StartSurfaceDelegate. */
public class StartSurfaceDelegate {
    public static Layout createStartSurfaceLayout(Context context, LayoutUpdateHost updateHost,
            LayoutRenderHost renderHost, StartSurface startSurface) {
        return new StartSurfaceLayout(context, updateHost, renderHost, startSurface);
    }

    /** {@see StartSurfaceCoordinator} */
    public static StartSurface createStartSurface(@NonNull Activity activity,
            @NonNull ScrimCoordinator scrimCoordinator,
            @NonNull BottomSheetController sheetController,
            @NonNull OneshotSupplierImpl<StartSurface> startSurfaceOneshotSupplier,
            @NonNull Supplier<Tab> parentTabSupplier, boolean hadWarmStart,
            @NonNull WindowAndroid windowAndroid, @NonNull ViewGroup containerView,
            @NonNull Supplier<DynamicResourceLoader> dynamicResourceLoaderSupplier,
            @NonNull TabModelSelector tabModelSelector,
            @NonNull BrowserControlsManager browserControlsManager,
            @NonNull SnackbarManager snackbarManager,
            @NonNull Supplier<ShareDelegate> shareDelegateSupplier,
            @NonNull Supplier<OmniboxStub> omniboxStubSupplier,
            @NonNull TabContentManager tabContentManager,
            @NonNull ModalDialogManager modalDialogManager,
            @NonNull ChromeActivityNativeDelegate chromeActivityNativeDelegate,
            @NonNull ActivityLifecycleDispatcher activityLifecycleDispatcher,
            @NonNull TabCreatorManager tabCreatorManager,
            @NonNull MenuOrKeyboardActionController menuOrKeyboardActionController,
            @NonNull MultiWindowModeStateDispatcher multiWindowModeStateDispatcher) {
        return new StartSurfaceCoordinator(activity, scrimCoordinator, sheetController,
                startSurfaceOneshotSupplier, parentTabSupplier, hadWarmStart, windowAndroid,
                containerView, dynamicResourceLoaderSupplier, tabModelSelector,
                browserControlsManager, snackbarManager, shareDelegateSupplier, omniboxStubSupplier,
                tabContentManager, modalDialogManager, chromeActivityNativeDelegate,
                activityLifecycleDispatcher, tabCreatorManager, menuOrKeyboardActionController,
                multiWindowModeStateDispatcher);
    }
}