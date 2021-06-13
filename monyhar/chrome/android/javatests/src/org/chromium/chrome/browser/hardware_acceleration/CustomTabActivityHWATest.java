// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.hardware_acceleration;

import android.support.test.InstrumentationRegistry;

import androidx.test.filters.SmallTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.chrome.browser.customtabs.CustomTabActivityTestRule;
import org.monyhar.chrome.browser.customtabs.CustomTabsTestUtils;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;

/**
 * Tests that CustomTabActivity is hardware accelerated only high-end devices.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class CustomTabActivityHWATest {
    @Rule
    public CustomTabActivityTestRule mCustomTabActivityTestRule = new CustomTabActivityTestRule();

    @Test
    @SmallTest
    public void testHardwareAcceleration() throws Exception {
        mCustomTabActivityTestRule.startCustomTabActivityWithIntent(
                CustomTabsTestUtils.createMinimalCustomTabIntent(
                        InstrumentationRegistry.getTargetContext(), "about:blank"));
        Utils.assertHardwareAcceleration(mCustomTabActivityTestRule.getActivity());
    }
}
