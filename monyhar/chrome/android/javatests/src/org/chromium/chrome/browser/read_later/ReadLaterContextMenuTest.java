// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.read_later;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.monyhar.chrome.browser.toolbar.top.ButtonHighlightMatcher.withHighlight;
import static org.monyhar.chrome.test.util.ViewUtils.waitForView;

import android.support.test.InstrumentationRegistry;
import android.view.View;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.filters.MediumTest;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import org.monyhar.base.Callback;
import org.monyhar.base.test.util.Batch;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.base.test.util.JniMocker;
import org.monyhar.base.test.util.Restriction;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.app.ChromeActivity;
import org.monyhar.chrome.browser.feature_engagement.TrackerFactory;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.offlinepages.RequestCoordinatorBridge;
import org.monyhar.chrome.browser.offlinepages.RequestCoordinatorBridgeJni;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.browser.Features;
import org.monyhar.chrome.test.util.browser.contextmenu.ContextMenuUtils;
import org.monyhar.components.feature_engagement.FeatureConstants;
import org.monyhar.components.feature_engagement.Tracker;
import org.monyhar.net.test.EmbeddedTestServerRule;
import org.monyhar.ui.test.util.UiRestriction;

/** Integration tests for showing IPH bubbles for read later. */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
@Features.EnableFeatures(ChromeFeatureList.READ_LATER)
@Batch(Batch.PER_CLASS)
public class ReadLaterContextMenuTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();
    @Rule
    public EmbeddedTestServerRule mTestServer = new EmbeddedTestServerRule();
    @Rule
    public MockitoRule mMockitoRule = MockitoJUnit.rule();
    @Rule
    public JniMocker mocker = new JniMocker();
    @Mock
    private Tracker mTracker;
    @Mock
    RequestCoordinatorBridge.Natives mRequestCoordinatorBridgeJniMock;

    private static final String CONTEXT_MENU_TEST_URL =
            "/chrome/test/data/android/contextmenu/context_menu_test.html";
    private static final String CONTEXT_MENU_LINK_URL =
            "/chrome/test/data/android/contextmenu/test_link.html";
    private static final String CONTEXT_MENU_LINK_DOM_ID = "testLink";

    @Before
    public void setUp() {
        // Pretend the feature engagement feature is already initialized. Otherwise
        // UserEducationHelper#requestShowIPH() calls get dropped during test.
        doAnswer(invocation -> {
            invocation.<Callback<Boolean>>getArgument(0).onResult(true);
            return null;
        })
                .when(mTracker)
                .addOnInitializedCallback(any());
        TrackerFactory.setTrackerForTests(mTracker);
        mActivityTestRule.startMainActivityOnBlankPage();
        mocker.mock(RequestCoordinatorBridgeJni.TEST_HOOKS, mRequestCoordinatorBridgeJniMock);
    }

    @After
    public void tearDown() throws Exception {
        TrackerFactory.setTrackerForTests(null);
    }

    @Test
    @MediumTest
    @Restriction({UiRestriction.RESTRICTION_TYPE_PHONE})
    public void testShowIPHOnContextMenuLinkCopied() throws Throwable {
        when(mTracker.shouldTriggerHelpUI(
                     FeatureConstants.READ_LATER_APP_MENU_BOOKMARK_THIS_PAGE_FEATURE))
                .thenReturn(true);
        mActivityTestRule.loadUrlInNewTab(mTestServer.getServer().getURL(CONTEXT_MENU_TEST_URL));

        ChromeActivity activity = mActivityTestRule.getActivity();
        Tab tab = activity.getActivityTab();
        ContextMenuUtils.selectContextMenuItem(InstrumentationRegistry.getInstrumentation(),
                activity, tab, CONTEXT_MENU_LINK_DOM_ID, R.id.contextmenu_copy_link_address);

        onView(withId(R.id.menu_button_wrapper)).check(matches(withHighlight(true)));
        waitForHelpBubble(withText(R.string.reading_list_save_pages_for_later));
    }

    @Test
    @MediumTest
    @Restriction({UiRestriction.RESTRICTION_TYPE_PHONE})
    public void testContextMenuAddToOfflinePage() throws Throwable {
        String url = mTestServer.getServer().getURL(CONTEXT_MENU_TEST_URL);
        mActivityTestRule.loadUrlInNewTab(url);
        ChromeActivity activity = mActivityTestRule.getActivity();
        Tab tab = activity.getActivityTab();
        ContextMenuUtils.selectContextMenuItem(InstrumentationRegistry.getInstrumentation(),
                activity, tab, CONTEXT_MENU_LINK_DOM_ID, R.id.contextmenu_read_later);
        String linkUrl = mTestServer.getServer().getURL(CONTEXT_MENU_LINK_URL);
        verify(mRequestCoordinatorBridgeJniMock, timeout(CriteriaHelper.DEFAULT_MAX_TIME_TO_POLL))
                .savePageLater(any(), any(), eq(linkUrl), any(), any(), any(), anyBoolean());
    }

    private ViewInteraction waitForHelpBubble(Matcher<View> matcher) {
        View mainDecorView = mActivityTestRule.getActivity().getWindow().getDecorView();
        return onView(isRoot())
                .inRoot(RootMatchers.withDecorView(not(is(mainDecorView))))
                .check(waitForView(matcher));
    }
}
