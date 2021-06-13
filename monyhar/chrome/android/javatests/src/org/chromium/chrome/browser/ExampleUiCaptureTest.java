// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser;

import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.base.test.util.Restriction;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.test.ScreenShooter;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.ui.test.util.UiRestriction;

/**
 * Simple test to demonstrate use of ScreenShooter rule.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
@Restriction(UiRestriction.RESTRICTION_TYPE_PHONE) // Tab switcher button only exists on phones.
public class ExampleUiCaptureTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    @Rule
    public ScreenShooter mScreenShooter = new ScreenShooter();

    @Before
    public void setUp() {
        mActivityTestRule.startMainActivityFromLauncher();
    }

    /**
     * Capture the New Tab Page and the tab switcher.
     */
    @Test
    @SmallTest
    @Feature({"UiCatalogue"})
    public void testCaptureNewTabPage() {
        mScreenShooter.shoot("NTP", ScreenShooter.TagsEnum.UiCatalogueExample);
    }
}
