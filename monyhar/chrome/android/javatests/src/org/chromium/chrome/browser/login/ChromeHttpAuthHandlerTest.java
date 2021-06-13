// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.login;

import android.support.test.InstrumentationRegistry;

import androidx.test.filters.MediumTest;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.Callback;
import org.monyhar.base.test.util.CallbackHelper;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Criteria;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.base.test.util.Restriction;
import org.monyhar.chrome.browser.customtabs.CustomTabActivityTestRule;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.tab.SadTab;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.browser.tab.TabLaunchType;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.ChromeTabUtils;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.content_public.common.ContentUrlConstants;
import org.monyhar.net.test.EmbeddedTestServer;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests for the Android specific HTTP auth UI.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class ChromeHttpAuthHandlerTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    @Rule
    public CustomTabActivityTestRule mCustomTabActivityTestRule = new CustomTabActivityTestRule();

    private EmbeddedTestServer mTestServer;

    @Before
    public void setUp() throws Exception {
        mActivityTestRule.startMainActivityOnBlankPage();
        mTestServer = EmbeddedTestServer.createAndStartServer(InstrumentationRegistry.getContext());
    }

    @After
    public void tearDown() {
        if (mTestServer != null) mTestServer.stopAndDestroyServer();
    }

    @Test
    @MediumTest
    public void authDialogShows() throws Exception {
        ChromeHttpAuthHandler handler = triggerAuth();
        verifyAuthDialogVisibility(handler, true);
    }

    @Test
    @MediumTest
    public void authDialogDismissOnNavigation() throws Exception {
        ChromeHttpAuthHandler handler = triggerAuth();
        verifyAuthDialogVisibility(handler, true);
        ChromeTabUtils.loadUrlOnUiThread(mActivityTestRule.getActivity().getActivityTab(),
                ContentUrlConstants.ABOUT_BLANK_DISPLAY_URL);
        verifyAuthDialogVisibility(handler, false);
    }

    @Test
    @MediumTest
    public void authDialogDismissOnTabSwitched() throws Exception {
        ChromeHttpAuthHandler handler = triggerAuth();
        verifyAuthDialogVisibility(handler, true);
        TestThreadUtils.runOnUiThreadBlocking(
                ()
                        -> mActivityTestRule.getActivity().getTabCreator(false).launchUrl(
                                "about:blank", TabLaunchType.FROM_CHROME_UI));
        verifyAuthDialogVisibility(handler, false);
    }

    @Test
    @MediumTest
    public void authDialogDismissOnTabClosed() throws Exception {
        ChromeHttpAuthHandler handler = triggerAuth();
        verifyAuthDialogVisibility(handler, true);
        ChromeTabUtils.closeCurrentTab(
                InstrumentationRegistry.getInstrumentation(), mActivityTestRule.getActivity());
        verifyAuthDialogVisibility(handler, false);
    }

    @Test
    @MediumTest
    @Restriction(Restriction.RESTRICTION_TYPE_NON_LOW_END_DEVICE)
    public void authDialogSuppressedOnBackgroundTab() throws Exception {
        Tab firstTab = mActivityTestRule.getActivity().getActivityTab();
        ChromeTabUtils.newTabFromMenu(
                InstrumentationRegistry.getInstrumentation(), mActivityTestRule.getActivity());
        // If the first tab was closed due to OOM, then just exit the test.
        if (TestThreadUtils.runOnUiThreadBlocking(
                    () -> firstTab.isClosing() || SadTab.isShowing(firstTab))) {
            return;
        }
        ChromeHttpAuthHandler handler = triggerAuthForTab(firstTab);
        verifyAuthDialogVisibility(handler, false);
    }

    private ChromeHttpAuthHandler triggerAuth() throws Exception {
        return triggerAuthForTab(mActivityTestRule.getActivity().getActivityTab());
    }

    private ChromeHttpAuthHandler triggerAuthForTab(Tab tab) throws Exception {
        AtomicReference<ChromeHttpAuthHandler> handlerRef = new AtomicReference<>();
        CallbackHelper handlerCallback = new CallbackHelper();
        Callback<ChromeHttpAuthHandler> callback = (handler) -> {
            handlerRef.set(handler);
            handlerCallback.notifyCalled();
        };
        TestThreadUtils.runOnUiThreadBlocking(
                () -> { ChromeHttpAuthHandler.setTestCreationCallback(callback); });

        String url = mTestServer.getURL("/auth-basic");
        ChromeTabUtils.loadUrlOnUiThread(tab, url);
        handlerCallback.waitForFirst();
        TestThreadUtils.runOnUiThreadBlocking(
                () -> { ChromeHttpAuthHandler.setTestCreationCallback(null); });
        return handlerRef.get();
    }

    private void verifyAuthDialogVisibility(ChromeHttpAuthHandler handler, boolean isVisible) {
        CriteriaHelper.pollUiThread(
                () -> Criteria.checkThat(handler.isShowingAuthDialog(), Matchers.is(isVisible)));
    }
}
