// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.webapps;

import static org.junit.Assert.assertTrue;

import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import org.monyhar.base.metrics.RecordHistogram;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.browser.browserservices.ui.SharedActivityCoordinator;
import org.monyhar.chrome.browser.browserservices.ui.controller.webapps.WebappDisclosureController;
import org.monyhar.chrome.browser.customtabs.CustomTabOrientationController;
import org.monyhar.chrome.browser.dependency_injection.ChromeActivityCommonsModule;
import org.monyhar.chrome.browser.dependency_injection.ModuleOverridesRule;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.lifecycle.ActivityLifecycleDispatcher;
import org.monyhar.chrome.browser.lifecycle.LifecycleObserver;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.util.browser.webapps.WebApkIntentDataProviderBuilder;
import org.monyhar.net.test.EmbeddedTestServer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * Tests that the expected classes are constructed when a WebAPK Activity is launched.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class WebApkInitializationTest {
    /**
     * {@link ActivityLifecycleDispatcher} wrapper which tracks {@link LifecycleObserver}
     * registrations.
     */
    private static class TrackingActivityLifecycleDispatcher
            implements ActivityLifecycleDispatcher {
        private ActivityLifecycleDispatcher mRealActivityLifecycleDispatcher;
        private Set<String> mRegisteredObserverClassNames = new HashSet<>();

        public void init(ActivityLifecycleDispatcher realActivityLifecycleDispatcher) {
            mRealActivityLifecycleDispatcher = realActivityLifecycleDispatcher;
        }

        /**
         * Returns set of all the {@link LifecycleObserver} subclasses which have registered with
         * the {@link ActivityLifecycleDispatcher}.
         */
        public Set<String> getRegisteredObserverClassNames() {
            return mRegisteredObserverClassNames;
        }

        @Override
        public void register(LifecycleObserver observer) {
            mRegisteredObserverClassNames.add(observer.getClass().getName());
            mRealActivityLifecycleDispatcher.register(observer);
        }

        @Override
        public void unregister(LifecycleObserver observer) {
            mRealActivityLifecycleDispatcher.unregister(observer);
        }

        @Override
        public @ActivityState int getCurrentActivityState() {
            return mRealActivityLifecycleDispatcher.getCurrentActivityState();
        }

        @Override
        public boolean isNativeInitializationFinished() {
            return true;
        }

        @Override
        public boolean isActivityFinishingOrDestroyed() {
            return mRealActivityLifecycleDispatcher.isActivityFinishingOrDestroyed();
        }
    }

    private final TrackingActivityLifecycleDispatcher mTrackingActivityLifecycleDispatcher =
            new TrackingActivityLifecycleDispatcher();

    private final TestRule mModuleOverridesRule = new ModuleOverridesRule().setOverride(
            ChromeActivityCommonsModule.Factory.class,
            (activity, bottomSheetControllerSupplier, tabModelSelectorSupplier,
                    browserControlsManager, browserControlsVisibilityManager, browserControlsSizer,
                    fullscreenManager, layoutManagerSupplier, lifecycleDispatcher,
                    snackbarManagerSupplier, activityTabProvider, tabContentManager,
                    activityWindowAndroid, compositorViewHolderSupplier, tabCreatorManager,
                    tabCreatorSupplier, isPromotableToTabSupplier, statusBarColorController,
                    screenOrientationProvider, notificationManagerProxySupplier,
                    tabContentManagerSupplier, activityTabStartupMetricsTrackerSupplier,
                    compositorViewHolderInitializer, chromeActivityNativeDelegate,
                    modalDialogManagerSupplier, browserControlsStateProvider,
                    savedInstanceStateSupplier, autofillUiBottomInsetSupplier) -> {
                mTrackingActivityLifecycleDispatcher.init(lifecycleDispatcher);
                return new ChromeActivityCommonsModule(activity, bottomSheetControllerSupplier,
                        tabModelSelectorSupplier, browserControlsManager,
                        browserControlsVisibilityManager, browserControlsSizer, fullscreenManager,
                        layoutManagerSupplier, mTrackingActivityLifecycleDispatcher,
                        snackbarManagerSupplier, activityTabProvider, tabContentManager,
                        activityWindowAndroid, compositorViewHolderSupplier, tabCreatorManager,
                        tabCreatorSupplier, isPromotableToTabSupplier, statusBarColorController,
                        screenOrientationProvider, notificationManagerProxySupplier,
                        tabContentManagerSupplier, activityTabStartupMetricsTrackerSupplier,
                        compositorViewHolderInitializer, chromeActivityNativeDelegate,
                        modalDialogManagerSupplier, browserControlsStateProvider,
                        savedInstanceStateSupplier, autofillUiBottomInsetSupplier);
            });

    private final WebApkActivityTestRule mActivityRule = new WebApkActivityTestRule();

    @Rule
    public final TestRule mRuleChain =
            RuleChain.outerRule(mModuleOverridesRule).around(mActivityRule);

    /**
     * Test that {@link WebappActionsNotificationManager},
     * {@link WebappDisclosureSnackbarController}, {@link WebApkActivityLifecycleUmaTracker} and
     * {@link CustomTabOrientationController} are constructed when a {@link WebApkActivity} is
     * launched.
     */
    @Test
    @LargeTest
    @Feature({"WebApk"})
    public void testInitialization() throws TimeoutException {
        EmbeddedTestServer embeddedTestServer =
                mActivityRule.getEmbeddedTestServerRule().getServer();
        WebApkIntentDataProviderBuilder intentDataProviderBuilder =
                new WebApkIntentDataProviderBuilder("org.monyhar.webapk.for.testing",
                        embeddedTestServer.getURL(
                                "/chrome/test/data/banners/manifest_test_page.html"));
        mActivityRule.startWebApkActivity(intentDataProviderBuilder.build());

        Set<String> registeredObserverClassNames =
                mTrackingActivityLifecycleDispatcher.getRegisteredObserverClassNames();
        assertTrue(registeredObserverClassNames.contains(
                WebappActionsNotificationManager.class.getName()));
        assertTrue(
                registeredObserverClassNames.contains(WebappDisclosureController.class.getName()));
        assertTrue(registeredObserverClassNames.contains(
                WebApkActivityLifecycleUmaTracker.class.getName()));
        assertTrue(
                registeredObserverClassNames.contains(SharedActivityCoordinator.class.getName()));

        // Test that WebappActiveTabUmaTracker is hooked up.
        assertTrue(0 < RecordHistogram.getHistogramTotalCountForTesting(
                           WebappActiveTabUmaTracker.HISTOGRAM_NAVIGATION_STATUS));
    }
}
