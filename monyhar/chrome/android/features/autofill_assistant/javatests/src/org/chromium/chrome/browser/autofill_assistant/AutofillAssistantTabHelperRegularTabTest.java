// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.autofill_assistant;

import androidx.test.filters.MediumTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.UiThreadTest;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;

/**
 * Tests for the autofill-assistant tab helper.
 */
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
@RunWith(ChromeJUnit4ClassRunner.class)
public class AutofillAssistantTabHelperRegularTabTest {
    @Rule
    public ChromeTabbedActivityTestRule mTestRule = new ChromeTabbedActivityTestRule();

    @Before
    public void setUp() {
        mTestRule.startMainActivityOnBlankPage();
    }

    @Test
    @MediumTest
    @UiThreadTest
    public void initForRegularTab() {
        Assert.assertNotNull(
                AutofillAssistantTabHelper.get(mTestRule.getActivity().getActivityTab()));
    }
}
