// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.customtabs;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.test.filters.LargeTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.ActivityState;
import org.monyhar.base.ApplicationStatus;
import org.monyhar.base.test.params.ParameterAnnotations.ClassParameter;
import org.monyhar.base.test.params.ParameterAnnotations.UseRunnerDelegate;
import org.monyhar.base.test.params.ParameterSet;
import org.monyhar.base.test.params.ParameterizedRunner;
import org.monyhar.base.test.util.CallbackHelper;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.chrome.browser.DeferredStartupHandler;
import org.monyhar.chrome.browser.customtabs.content.CustomTabActivityTabProvider;
import org.monyhar.chrome.browser.customtabs.content.TabCreationMode;
import org.monyhar.chrome.browser.customtabs.dependency_injection.BaseCustomTabActivityComponent;
import org.monyhar.chrome.browser.flags.ActivityType;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.lifecycle.InflationObserver;
import org.monyhar.chrome.browser.tab.EmptyTabObserver;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.browser.tab.TabCreationState;
import org.monyhar.chrome.browser.tab.TabObserver;
import org.monyhar.chrome.browser.tabmodel.TabModelSelectorBase;
import org.monyhar.chrome.browser.tabmodel.TabModelSelectorObserver;
import org.monyhar.chrome.test.ChromeActivityTestRule;
import org.monyhar.chrome.test.ChromeJUnit4RunnerDelegate;
import org.monyhar.chrome.test.util.browser.Features.DisableFeatures;
import org.monyhar.url.GURL;

import java.util.Arrays;
import java.util.List;

/**
 * Tests that when DeferredStartupHandler#queueDeferredTasksOnIdleHandler() is run that the
 * activity's tab has finished loading.
 */
@RunWith(ParameterizedRunner.class)
@UseRunnerDelegate(ChromeJUnit4RunnerDelegate.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class CustomTabDeferredStartupTest {
    static class PageLoadFinishedTabObserver extends EmptyTabObserver {
        private boolean mIsPageLoadFinished;

        @Override
        public void onPageLoadFinished(Tab tab, GURL url) {
            mIsPageLoadFinished = true;
        }

        public boolean isPageLoadFinished() {
            return mIsPageLoadFinished;
        }
    }

    static class InitialTabCreationObserver extends CustomTabActivityTabProvider.Observer {
        private TabObserver mObserver;

        public InitialTabCreationObserver(TabObserver observer) {
            mObserver = observer;
        }

        @Override
        public void onInitialTabCreated(@NonNull Tab tab, @TabCreationMode int mode) {
            tab.addObserver(mObserver);
        }
    }

    static class NewTabObserver implements TabModelSelectorObserver,
                                           ApplicationStatus.ActivityStateListener,
                                           InflationObserver {
        private BaseCustomTabActivity mActivity;
        private TabObserver mObserver;

        public NewTabObserver(TabObserver observer) {
            mObserver = observer;
        }

        @Override
        public void onNewTabCreated(Tab tab, @TabCreationState int creationState) {
            tab.addObserver(mObserver);
        }

        @Override
        public void onActivityStateChange(Activity activity, @ActivityState int newState) {
            if (newState == ActivityState.CREATED && activity instanceof BaseCustomTabActivity
                    && mActivity == null) {
                mActivity = (BaseCustomTabActivity) activity;
                mActivity.getLifecycleDispatcher().register(this);
            }
        }

        @Override
        public void onPreInflationStartup() {
            BaseCustomTabActivityComponent baseCustomTabActivityComponent =
                    (BaseCustomTabActivityComponent) mActivity.getComponent();
            baseCustomTabActivityComponent.resolveTabProvider().addObserver(
                    new InitialTabCreationObserver(mObserver));
        }

        @Override
        public void onPostInflationStartup() {}
    }

    static class PageIsLoadedDeferredStartupHandler extends DeferredStartupHandler {
        public PageIsLoadedDeferredStartupHandler(
                PageLoadFinishedTabObserver observer, CallbackHelper helper) {
            mObserver = observer;
            mHelper = helper;
        }

        @Override
        public void queueDeferredTasksOnIdleHandler() {
            Assert.assertTrue("Page is yet to finish loading.", mObserver.isPageLoadFinished());

            mHelper.notifyCalled();

            super.queueDeferredTasksOnIdleHandler();
        }

        private CallbackHelper mHelper;
        private PageLoadFinishedTabObserver mObserver;
    }

    @ClassParameter
    public static List<ParameterSet> sClassParams = Arrays.asList(
            new ParameterSet().value(ActivityType.WEBAPP).name("Webapp"),
            new ParameterSet().value(ActivityType.CUSTOM_TAB).name("CustomTab"),
            new ParameterSet().value(ActivityType.TRUSTED_WEB_ACTIVITY).name("TrustedWebActivity"));

    private @ActivityType int mActivityType;

    @Rule
    public final ChromeActivityTestRule<?> mActivityTestRule;

    public CustomTabDeferredStartupTest(@ActivityType int activityType) {
        mActivityType = activityType;
        mActivityTestRule = CustomTabActivityTypeTestUtils.createActivityTestRule(activityType);
    }

    @Test
    @LargeTest
    @DisableFeatures(ChromeFeatureList.TRUSTED_WEB_ACTIVITY_QUALITY_ENFORCEMENT_FORCED)
    // TODO(eirage): Make this test work with quality enforcement.
    public void testPageIsLoadedOnDeferredStartup() throws Exception {
        PageLoadFinishedTabObserver tabObserver = new PageLoadFinishedTabObserver();
        NewTabObserver newTabObserver = new NewTabObserver(tabObserver);
        TabModelSelectorBase.setObserverForTests(newTabObserver);
        ApplicationStatus.registerStateListenerForAllActivities(newTabObserver);
        CallbackHelper helper = new CallbackHelper();
        PageIsLoadedDeferredStartupHandler handler =
                new PageIsLoadedDeferredStartupHandler(tabObserver, helper);
        DeferredStartupHandler.setInstanceForTests(handler);
        CustomTabActivityTypeTestUtils.launchActivity(
                mActivityType, mActivityTestRule, "about:blank");
        helper.waitForCallback(0);
    }
}
