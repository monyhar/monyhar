// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.app.tabmodel;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;

/**
 * Tests for the {@link ChromeNextTabPolicySupplier}.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class ChromeNextTabPolicySupplierTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    @Before
    public void setUp() {
        mActivityTestRule.startMainActivityFromLauncher();
    }

    @Test
    @SmallTest
    public void verifyOverviewModeBehaviorIsNotNull() {
        Assert.assertNotNull(mActivityTestRule.getActivity()
                                     .getNextTabPolicySupplier()
                                     .getOverviewModeBehavior());
    }
}