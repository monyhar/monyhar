// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser;

import android.support.test.InstrumentationRegistry;
import android.widget.ImageButton;

import androidx.test.filters.MediumTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.base.test.util.Restriction;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.toolbar.TabSwitcherDrawable;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.ChromeTabUtils;
import org.monyhar.ui.test.util.UiRestriction;

/**
 * Test suite for the tab count widget on the phone toolbar.
 */

@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class TabCountLabelTest {
    /**
     * Check the tabCount string against an expected value.
     */

    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    private void tabCountLabelCheck(String stepName, int tabCountExpected) {
        ImageButton tabSwitcherBtn = (ImageButton) mActivityTestRule.getActivity().findViewById(
                R.id.tab_switcher_button);
        TabSwitcherDrawable drawable = (TabSwitcherDrawable) tabSwitcherBtn.getDrawable();
        int tabCountFromDrawable = drawable.getTabCount();
        Assert.assertTrue(stepName + ", " + tabCountExpected + " tab[s] expected, label shows "
                        + tabCountFromDrawable,
                tabCountExpected == tabCountFromDrawable);
    }

    /**
     * Verify displayed Tab Count matches the actual number of tabs.
     */
    @Test
    @MediumTest
    @Feature({"Browser", "Main"})
    @Restriction(UiRestriction.RESTRICTION_TYPE_PHONE)
    public void testTabCountLabel() {
        final int tabCount = mActivityTestRule.getActivity().getCurrentTabModel().getCount();
        tabCountLabelCheck("Initial state", tabCount);
        ChromeTabUtils.newTabFromMenu(
                InstrumentationRegistry.getInstrumentation(), mActivityTestRule.getActivity());
        // Make sure the TAB_CREATED notification went through
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        tabCountLabelCheck("After new tab", tabCount + 1);
        ChromeTabUtils.closeCurrentTab(
                InstrumentationRegistry.getInstrumentation(), mActivityTestRule.getActivity());
        // Make sure the TAB_CLOSED notification went through
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        tabCountLabelCheck("After close tab", tabCount);
    }

    @Before
    public void setUp() throws InterruptedException {
        mActivityTestRule.startMainActivityOnBlankPage();
    }
}
