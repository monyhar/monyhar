// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.customtabs;

import org.monyhar.base.supplier.ObservableSupplier;
import org.monyhar.base.supplier.OneShotCallback;
import org.monyhar.base.supplier.OneshotSupplierImpl;
import org.monyhar.base.supplier.Supplier;
import org.monyhar.chrome.browser.ActivityTabProvider;
import org.monyhar.chrome.browser.app.ChromeActivity;
import org.monyhar.chrome.browser.app.reengagement.ReengagementActivity;
import org.monyhar.chrome.browser.bookmarks.BookmarkBridge;
import org.monyhar.chrome.browser.contextualsearch.ContextualSearchManager;
import org.monyhar.chrome.browser.customtabs.content.CustomTabActivityNavigationController;
import org.monyhar.chrome.browser.customtabs.features.toolbar.CustomTabToolbarCoordinator;
import org.monyhar.chrome.browser.feature_engagement.TrackerFactory;
import org.monyhar.chrome.browser.flags.ActivityType;
import org.monyhar.chrome.browser.fullscreen.BrowserControlsManager;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.reengagement.ReengagementNotificationController;
import org.monyhar.chrome.browser.share.ShareDelegate;
import org.monyhar.chrome.browser.tabmodel.TabModelSelector;
import org.monyhar.chrome.browser.ui.RootUiCoordinator;
import org.monyhar.components.feature_engagement.Tracker;
import org.monyhar.ui.base.ActivityWindowAndroid;

/**
 * A {@link RootUiCoordinator} variant that controls UI for {@link BaseCustomTabActivity}.
 */
public class BaseCustomTabRootUiCoordinator extends RootUiCoordinator {
    private final Supplier<CustomTabToolbarCoordinator> mToolbarCoordinator;
    private final Supplier<CustomTabActivityNavigationController> mNavigationController;

    public BaseCustomTabRootUiCoordinator(ChromeActivity activity,
            ObservableSupplier<ShareDelegate> shareDelegateSupplier,
            Supplier<CustomTabToolbarCoordinator> customTabToolbarCoordinator,
            Supplier<CustomTabActivityNavigationController> customTabNavigationController,
            ActivityTabProvider tabProvider, ObservableSupplier<Profile> profileSupplier,
            ObservableSupplier<BookmarkBridge> bookmarkBridgeSupplier,
            Supplier<ContextualSearchManager> contextualSearchManagerSupplier,
            ObservableSupplier<TabModelSelector> tabModelSelectorSupplier,
            BrowserControlsManager browserControlsManager, ActivityWindowAndroid windowAndroid) {
        super(activity, null, shareDelegateSupplier, tabProvider, profileSupplier,
                bookmarkBridgeSupplier, contextualSearchManagerSupplier, tabModelSelectorSupplier,
                new OneshotSupplierImpl<>(), new OneshotSupplierImpl<>(),
                new OneshotSupplierImpl<>(), () -> null, browserControlsManager, windowAndroid);
        mToolbarCoordinator = customTabToolbarCoordinator;
        mNavigationController = customTabNavigationController;
    }

    @Override
    protected void initializeToolbar() {
        super.initializeToolbar();

        mToolbarCoordinator.get().onToolbarInitialized(mToolbarManager);
        mNavigationController.get().onToolbarInitialized(mToolbarManager);
    }

    @Override
    public void onFinishNativeInitialization() {
        super.onFinishNativeInitialization();
        if (!ReengagementNotificationController.isEnabled()) return;
        new OneShotCallback<>(mProfileSupplier, mCallbackController.makeCancelable(profile -> {
            assert profile != null : "Unexpectedly null profile from TabModel.";
            if (profile == null) return;
            Tracker tracker = TrackerFactory.getTrackerForProfile(profile);
            ReengagementNotificationController controller = new ReengagementNotificationController(
                    mActivity, tracker, ReengagementActivity.class);
            controller.tryToReengageTheUser();
        }));
    }

    @Override
    protected boolean shouldAllowThemingInNightMode() {
        @ActivityType
        int activityType = mActivity.getActivityType();
        return activityType == ActivityType.TRUSTED_WEB_ACTIVITY
                || activityType == ActivityType.WEB_APK;
    }
}
