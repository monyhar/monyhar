// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.language;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.metrics.RecordHistogram;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.components.language.AndroidLanguageMetricsBridge;

/**
 * Tests for the GlobalAppLocaleController class.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class GlobalAppLocaleControllerTest {
    private static final int EMPTY_STRING_HASH = -1895779836;

    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    @Before
    public void setUp() throws Exception {
        mActivityTestRule.startMainActivityOnBlankPage();
    }

    @Test
    @SmallTest
    public void testStartupHistograms() {
        CriteriaHelper.pollUiThread(() -> {
            // The initial app language is the default system language recorded as the empty string.
            Assert.assertEquals(1,
                    RecordHistogram.getHistogramValueCountForTesting(
                            AndroidLanguageMetricsBridge.OVERRIDE_LANGUAGE_HISTOGRAM,
                            EMPTY_STRING_HASH));
        });
    }
}
