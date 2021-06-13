// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.identity_disc;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.not;

import static org.monyhar.chrome.test.util.ViewUtils.waitForView;

import android.view.View;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.MediumTest;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.ChromeTabUtils;
import org.monyhar.chrome.test.util.NewTabPageTestUtils;
import org.monyhar.chrome.test.util.browser.signin.AccountManagerTestRule;
import org.monyhar.components.embedder_support.util.UrlConstants;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.content_public.common.ContentUrlConstants;

/**
 * Instrumentation test for Identity Disc.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class IdentityDiscControllerTest {
    private final ChromeTabbedActivityTestRule mActivityTestRule =
            new ChromeTabbedActivityTestRule();

    private final AccountManagerTestRule mAccountManagerTestRule = new AccountManagerTestRule();

    // Mock sign-in environment needs to be destroyed after ChromeTabbedActivity in case there are
    // observers registered in the AccountManagerFacade mock.
    @Rule
    public final RuleChain mRuleChain =
            RuleChain.outerRule(mAccountManagerTestRule).around(mActivityTestRule);

    private Tab mTab;

    @Before
    public void setUp() {
        mActivityTestRule.startMainActivityWithURL(UrlConstants.NTP_URL);
        mTab = mActivityTestRule.getActivity().getActivityTab();
        NewTabPageTestUtils.waitForNtpLoaded(mTab);
    }

    @Test
    @MediumTest
    public void testIdentityDiscWithNavigation() {
        // User is signed in.
        mAccountManagerTestRule.addTestAccountThenSigninAndEnableSync();
        waitForView(allOf(withId(R.id.optional_toolbar_button), isDisplayed()));

        // Identity Disc should be hidden on navigation away from NTP.
        leaveNTP();
        onView(withId(R.id.optional_toolbar_button))
                .check(matches(anyOf(withEffectiveVisibility(ViewMatchers.Visibility.GONE),
                        not(withContentDescription(
                                R.string.accessibility_toolbar_btn_identity_disc)))));
    }

    @Test
    @MediumTest
    public void testIdentityDiscWithSignin() {
        // When user is signed out, Identity Disc should not be visible on the NTP.
        onView(withId(R.id.optional_toolbar_button)).check((view, noViewException) -> {
            if (view != null) {
                ViewMatchers.assertThat("IdentityDisc view should be gone if it exists",
                        view.getVisibility(), Matchers.is(View.GONE));
            }
        });

        // Identity Disc should be shown on sign-in state change with a NTP refresh.
        mAccountManagerTestRule.addTestAccountThenSignin();
        // TODO(https://crbug.com/1132291): Remove the reload once the sign-in without sync observer
        //  is implemented.
        TestThreadUtils.runOnUiThreadBlocking(mTab::reload);
        waitForView(allOf(withId(R.id.optional_toolbar_button), isDisplayed()));

        onView(withId(R.id.optional_toolbar_button))
                .check(matches(
                        withContentDescription(R.string.accessibility_toolbar_btn_identity_disc)));

        mAccountManagerTestRule.signOut();
        waitForView(allOf(withId(R.id.optional_toolbar_button),
                withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    @MediumTest
    public void testIdentityDiscWithSigninAndEnableSync() {
        // When user is signed out, Identity Disc should not be visible on the NTP.
        onView(withId(R.id.optional_toolbar_button)).check((view, noViewException) -> {
            if (view != null) {
                ViewMatchers.assertThat("IdentityDisc view should be gone if it exists",
                        view.getVisibility(), Matchers.is(View.GONE));
            }
        });

        // Identity Disc should be shown on sign-in state change without NTP refresh.
        mAccountManagerTestRule.addTestAccountThenSigninAndEnableSync();
        waitForView(allOf(withId(R.id.optional_toolbar_button), isDisplayed()));

        onView(withId(R.id.optional_toolbar_button))
                .check(matches(
                        withContentDescription(R.string.accessibility_toolbar_btn_identity_disc)));

        mAccountManagerTestRule.signOut();
        waitForView(allOf(withId(R.id.optional_toolbar_button),
                withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    @MediumTest
    public void testIdentityDiscWithSwitchToIncognito() {
        mAccountManagerTestRule.addTestAccountThenSigninAndEnableSync();
        waitForView(allOf(withId(R.id.optional_toolbar_button), isDisplayed()));

        // Identity Disc should not be visible, when switched from sign in state to incognito NTP.
        mActivityTestRule.newIncognitoTabFromMenu();
        waitForView(allOf(withId(R.id.optional_toolbar_button),
                withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    private void leaveNTP() {
        mActivityTestRule.loadUrl(ContentUrlConstants.ABOUT_BLANK_DISPLAY_URL);
        ChromeTabUtils.waitForTabPageLoaded(mTab, ContentUrlConstants.ABOUT_BLANK_DISPLAY_URL);
    }
}
