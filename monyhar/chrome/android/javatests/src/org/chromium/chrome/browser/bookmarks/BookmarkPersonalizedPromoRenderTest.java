// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.bookmarks;

import android.view.View;

import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import org.monyhar.base.test.params.ParameterAnnotations;
import org.monyhar.base.test.params.ParameterizedRunner;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.DisableIf;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.night_mode.ChromeNightModeTestUtils;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.test.ChromeJUnit4RunnerDelegate;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.BookmarkTestRule;
import org.monyhar.chrome.test.util.BookmarkTestUtil;
import org.monyhar.chrome.test.util.ChromeRenderTestRule;
import org.monyhar.chrome.test.util.browser.Features.DisableFeatures;
import org.monyhar.chrome.test.util.browser.Features.EnableFeatures;
import org.monyhar.chrome.test.util.browser.signin.AccountManagerTestRule;
import org.monyhar.components.signin.test.util.FakeProfileDataSource;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.ui.test.util.NightModeTestUtils;
import org.monyhar.ui.test.util.UiDisableIf;

/**
 * Tests for the personalized signin promo on the Bookmarks page.
 */
@RunWith(ParameterizedRunner.class)
@ParameterAnnotations.UseRunnerDelegate(ChromeJUnit4RunnerDelegate.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
@DisableFeatures({ChromeFeatureList.DEPRECATE_MENAGERIE_API})
@EnableFeatures({ChromeFeatureList.MOBILE_IDENTITY_CONSISTENCY_PROMOS})
@DisableIf.Device(type = {UiDisableIf.TABLET})
public class BookmarkPersonalizedPromoRenderTest {
    // FakeProfileDataSource is required to create the ProfileDataCache entry with sync_off badge
    // for Sync promo.
    private final AccountManagerTestRule mAccountManagerTestRule =
            new AccountManagerTestRule(new FakeProfileDataSource());

    private final ChromeTabbedActivityTestRule mActivityTestRule =
            new ChromeTabbedActivityTestRule();

    private final BookmarkTestRule mBookmarkTestRule = new BookmarkTestRule();

    // Bookmarks need the FakeAccountManagerFacade initialized in AccountManagerTestRule.
    @Rule
    public final RuleChain chain = RuleChain.outerRule(mAccountManagerTestRule)
                                           .around(mActivityTestRule)
                                           .around(mBookmarkTestRule);

    @Rule
    public final ChromeRenderTestRule mRenderTestRule =
            ChromeRenderTestRule.Builder.withPublicCorpus().setRevision(3).build();

    @ParameterAnnotations.UseMethodParameterBefore(NightModeTestUtils.NightModeParams.class)
    public void setupNightMode(boolean nightModeEnabled) {
        ChromeNightModeTestUtils.setUpNightModeForChromeActivity(nightModeEnabled);
        mRenderTestRule.setNightModeEnabled(nightModeEnabled);
    }

    @BeforeClass
    public static void setUpBeforeActivityLaunched() {
        ChromeNightModeTestUtils.setUpNightModeBeforeChromeActivityLaunched();
    }

    @Before
    public void setUp() {
        // Native side needs to loaded before signing in test account.
        mActivityTestRule.startMainActivityOnBlankPage();
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            BookmarkModel bookmarkModel = new BookmarkModel(Profile.getLastUsedRegularProfile());
            bookmarkModel.loadFakePartnerBookmarkShimForTesting();
        });
        BookmarkTestUtil.waitForBookmarkModelLoaded();
    }

    @After
    public void tearDown() {
        BookmarkPromoHeader.forcePromoStateForTests(null);
    }

    @AfterClass
    public static void tearDownAfterActivityDestroyed() {
        ChromeNightModeTestUtils.tearDownNightModeAfterChromeActivityDestroyed();
    }

    @Test
    @MediumTest
    @Feature("RenderTest")
    @ParameterAnnotations.UseMethodParameter(NightModeTestUtils.NightModeParams.class)
    public void testPersonalizedSigninPromoInBookmarkPage(boolean nightModeEnabled)
            throws Exception {
        mAccountManagerTestRule.addAccount(AccountManagerTestRule.TEST_ACCOUNT_EMAIL);
        BookmarkPromoHeader.forcePromoStateForTests(
                BookmarkPromoHeader.PromoState.PROMO_SIGNIN_PERSONALIZED);
        mBookmarkTestRule.showBookmarkManager(mActivityTestRule.getActivity());
        mRenderTestRule.render(getPersonalizedPromoView(), "bookmark_personalized_signin_promo");
    }

    @Test
    @MediumTest
    @Feature("RenderTest")
    @ParameterAnnotations.UseMethodParameter(NightModeTestUtils.NightModeParams.class)
    public void testPersonalizedSyncPromoInBookmarkPage(boolean nightModeEnabled) throws Exception {
        mAccountManagerTestRule.addTestAccountThenSignin();
        BookmarkPromoHeader.forcePromoStateForTests(
                BookmarkPromoHeader.PromoState.PROMO_SYNC_PERSONALIZED);
        mBookmarkTestRule.showBookmarkManager(mActivityTestRule.getActivity());
        mRenderTestRule.render(getPersonalizedPromoView(), "bookmark_personalized_sync_promo");
    }

    private View getPersonalizedPromoView() {
        BookmarkActivity bookmarkActivity = mBookmarkTestRule.getBookmarkActivity();
        Assert.assertNotNull("BookmarkActivity should not be null", bookmarkActivity);
        return bookmarkActivity.findViewById(R.id.signin_promo_view_container);
    }
}
