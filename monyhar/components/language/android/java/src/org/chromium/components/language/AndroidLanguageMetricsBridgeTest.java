// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.language;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.metrics.RecordHistogram;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;

import java.util.Map;

/**
 * Tests for the AndroidLanguageMetricsBridge class.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class AndroidLanguageMetricsBridgeTest {
    private static final Map<String, Integer> NAME_HASHES =
            Map.of("en", -74147910, "af", 357286655, "hmn", 1110169461, "yue-HK", 632444664, "und",
                    350748440, "it-CH", 1708437566, "", -1895779836);

    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    @Before
    public void setUp() throws Exception {
        mActivityTestRule.startMainActivityOnBlankPage();
    }

    /** Test that the correct language hashes are reported for the override language */
    @Test
    @SmallTest
    public void testReportingAppOverrideLangauge() {
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            for (Map.Entry<String, Integer> entry : NAME_HASHES.entrySet()) {
                int initialCount = RecordHistogram.getHistogramValueCountForTesting(
                        AndroidLanguageMetricsBridge.OVERRIDE_LANGUAGE_HISTOGRAM, entry.getValue());
                AndroidLanguageMetricsBridge.reportAppOverrideLanguage(entry.getKey());
                Assert.assertEquals(initialCount + 1,
                        RecordHistogram.getHistogramValueCountForTesting(
                                AndroidLanguageMetricsBridge.OVERRIDE_LANGUAGE_HISTOGRAM,
                                entry.getValue()));
            }
        });
    }
}
