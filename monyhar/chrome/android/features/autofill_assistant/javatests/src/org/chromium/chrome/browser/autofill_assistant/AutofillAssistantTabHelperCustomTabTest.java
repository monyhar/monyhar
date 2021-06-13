// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.autofill_assistant;

import android.support.test.InstrumentationRegistry;

import androidx.test.filters.MediumTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.UiThreadTest;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.chrome.browser.customtabs.CustomTabActivityTestRule;
import org.monyhar.chrome.browser.customtabs.CustomTabsTestUtils;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;

/**
 * Tests for the autofill-assistant tab helper.
 */
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
@RunWith(ChromeJUnit4ClassRunner.class)
public class AutofillAssistantTabHelperCustomTabTest {
    @Rule
    public CustomTabActivityTestRule mTestRule = new CustomTabActivityTestRule();

    @Before
    public void setUp() {
        mTestRule.startCustomTabActivityWithIntent(CustomTabsTestUtils.createMinimalCustomTabIntent(
                InstrumentationRegistry.getTargetContext(), "about:blank"));
    }

    @Test
    @MediumTest
    @UiThreadTest
    public void initForCustomTab() {
        Assert.assertNotNull(
                AutofillAssistantTabHelper.get(mTestRule.getActivity().getActivityTab()));
    }
}
