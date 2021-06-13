// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.features.start_surface;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.monyhar.chrome.browser.tasks.ReturnToChromeExperimentsUtil.TAB_SWITCHER_ON_RETURN_MS;

import android.content.Intent;

import androidx.test.filters.MediumTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.params.ParameterAnnotations;
import org.monyhar.base.test.params.ParameterAnnotations.UseRunnerDelegate;
import org.monyhar.base.test.params.ParameterSet;
import org.monyhar.base.test.params.ParameterizedRunner;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.base.test.util.Feature;
import org.monyhar.base.test.util.Restriction;
import org.monyhar.chrome.browser.flags.CachedFeatureFlags;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.tasks.ReturnToChromeExperimentsUtil;
import org.monyhar.chrome.start_surface.R;
import org.monyhar.chrome.test.ChromeActivityTestRule;
import org.monyhar.chrome.test.ChromeJUnit4RunnerDelegate;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.browser.Features.EnableFeatures;
import org.monyhar.ui.test.util.UiRestriction;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Integration tests of the {@link StartSurface} for cases where there are no tabs. See {@link
 * StartSurfaceTest} for test that have tabs.
 */
@RunWith(ParameterizedRunner.class)
@UseRunnerDelegate(ChromeJUnit4RunnerDelegate.class)
@Restriction(
        {UiRestriction.RESTRICTION_TYPE_PHONE, Restriction.RESTRICTION_TYPE_NON_LOW_END_DEVICE})
@EnableFeatures({ChromeFeatureList.START_SURFACE_ANDROID + "<Study"})
@CommandLineFlags.
Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE, "force-fieldtrials=Study/Group"})
public class StartSurfaceNoTabsTest {
    @ParameterAnnotations.ClassParameter
    private static List<ParameterSet> sClassParams =
            Arrays.asList(new ParameterSet().value(false, false).name("NoInstant_NoReturn"),
                    new ParameterSet().value(true, false).name("Instant_NoReturn"),
                    new ParameterSet().value(false, true).name("NoInstant_Return"),
                    new ParameterSet().value(true, true).name("Instant_Return"));

    private static final String BASE_PARAMS =
            "force-fieldtrial-params=Study.Group:start_surface_variation";

    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    private final boolean mImmediateReturn;

    public StartSurfaceNoTabsTest(boolean useInstantStart, boolean immediateReturn) {
        CachedFeatureFlags.setForTesting(ChromeFeatureList.INSTANT_START, useInstantStart);

        mImmediateReturn = immediateReturn;
    }

    /**
     * Only launch Chrome without waiting for a current tab.
     * This test could not use {@link ChromeActivityTestRule#startMainActivityFromLauncher()}
     * because of its {@link org.monyhar.chrome.browser.tab.Tab} dependency.
     */
    private void startMainActivityFromLauncher() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        mActivityTestRule.prepareUrlIntent(intent, null);
        mActivityTestRule.launchActivity(intent);
    }

    @Before
    public void setUp() throws IOException {
        if (mImmediateReturn) {
            TAB_SWITCHER_ON_RETURN_MS.setForTesting(0);
            assertEquals(0, ReturnToChromeExperimentsUtil.TAB_SWITCHER_ON_RETURN_MS.getValue());
            assertTrue(ReturnToChromeExperimentsUtil.shouldShowTabSwitcher(-1));
        } else {
            assertFalse(ReturnToChromeExperimentsUtil.shouldShowTabSwitcher(-1));
        }

        startMainActivityFromLauncher();
    }

    @Test
    @MediumTest
    @Feature({"StartSurface"})
    // clang-format off
    @CommandLineFlags.Add({BASE_PARAMS + "/single"})
    public void testShow_SingleAsHomepage_NoTabs() throws TimeoutException {
        // clang-format on
        CriteriaHelper.pollUiThread(
                ()
                        -> mActivityTestRule.getActivity().getLayoutManager() != null
                        && mActivityTestRule.getActivity().getLayoutManager().overviewVisible());

        onView(withId(R.id.primary_tasks_surface_view)).check(matches(isDisplayed()));
        onView(withId(R.id.search_box_text)).check(matches(isDisplayed()));
        onView(withId(org.monyhar.chrome.tab_ui.R.id.mv_tiles_container))
                .check(matches(isDisplayed()));
        onView(withId(org.monyhar.chrome.tab_ui.R.id.tab_switcher_title))
                .check(matches(withEffectiveVisibility(GONE)));
        onView(withId(org.monyhar.chrome.tab_ui.R.id.carousel_tab_switcher_container))
                .check(matches(withEffectiveVisibility(GONE)));
        onView(withId(org.monyhar.chrome.tab_ui.R.id.more_tabs))
                .check(matches(withEffectiveVisibility(GONE)));
        onView(withId(org.monyhar.chrome.tab_ui.R.id.tasks_surface_body))
                .check(matches(isDisplayed()));
        onView(withId(R.id.start_tab_switcher_button))
                .check(matches(withEffectiveVisibility(GONE)));
    }

    @Test
    @MediumTest
    @Feature({"StartSurface"})
    // clang-format off
    @CommandLineFlags.Add({BASE_PARAMS + "/single/exclude_mv_tiles/true" +
        "/show_last_active_tab_only/true/open_ntp_instead_of_start/true"})
    public void testShow_SingleAsHomepage_SingleTabSwitcher_NoTabs() {
        // clang-format on
        CriteriaHelper.pollUiThread(
                ()
                        -> mActivityTestRule.getActivity().getLayoutManager() != null
                        && mActivityTestRule.getActivity().getLayoutManager().overviewVisible());

        onView(withId(R.id.primary_tasks_surface_view)).check(matches(isDisplayed()));
        onView(withId(R.id.search_box_text)).check(matches(isDisplayed()));
        onView(withId(org.monyhar.chrome.tab_ui.R.id.mv_tiles_container))
                .check(matches(withEffectiveVisibility(GONE)));
        onView(withId(org.monyhar.chrome.tab_ui.R.id.tab_switcher_title))
                .check(matches(withEffectiveVisibility(GONE)));
        onView(withId(org.monyhar.chrome.tab_ui.R.id.carousel_tab_switcher_container))
                .check(matches(withEffectiveVisibility(GONE)));
        onView(withId(org.monyhar.chrome.tab_ui.R.id.single_tab_view))
                .check(matches(withEffectiveVisibility(GONE)));
        onView(withId(org.monyhar.chrome.tab_ui.R.id.more_tabs))
                .check(matches(withEffectiveVisibility(GONE)));
        onView(withId(org.monyhar.chrome.tab_ui.R.id.tasks_surface_body))
                .check(matches(isDisplayed()));
    }
}
