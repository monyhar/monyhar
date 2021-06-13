// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.BaseSwitches;
import org.monyhar.base.library_loader.LibraryLoader;
import org.monyhar.base.test.ReachedCodeProfiler;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.preferences.ChromePreferenceKeys;
import org.monyhar.chrome.browser.preferences.SharedPreferencesManager;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.browser.Features.DisableFeatures;
import org.monyhar.chrome.test.util.browser.Features.EnableFeatures;

/**
 * Tests for the reached code profiler feature setup.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public final class ReachedCodeProfilerTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    private static final String FAKE_GROUP_NAME = "FakeGroup";

    /**
     * Tests that passing a command line switch enables the reached code profiler no matter what.
     */
    @Test
    @SmallTest
    @DisableFeatures(ChromeFeatureList.REACHED_CODE_PROFILER)
    @CommandLineFlags.Add(BaseSwitches.ENABLE_REACHED_CODE_PROFILER)
    public void testExplicitlyEnableViaCommandLineSwitch() {
        mActivityTestRule.startMainActivityFromLauncher();
        assertReachedCodeProfilerIsEnabled();
    }

    /**
     * Tests that setting a shared preference enables the reached code profiler. This test imitates
     * the second launch after enabling the feature
     */
    @Test
    @SmallTest
    @CommandLineFlags.Add({"enable-features=" + ChromeFeatureList.REACHED_CODE_PROFILER + "<"
                    + ChromeFeatureList.REACHED_CODE_PROFILER,
            "force-fieldtrials=" + ChromeFeatureList.REACHED_CODE_PROFILER + "/" + FAKE_GROUP_NAME,
            "force-fieldtrial-params=" + ChromeFeatureList.REACHED_CODE_PROFILER + "."
                    + FAKE_GROUP_NAME + ":sampling_interval_us/42"})
    public void
    testEnabledViaCachedSharedPreference() {
        LibraryLoader.setReachedCodeProfilerEnabledOnNextRuns(true, 42);
        mActivityTestRule.startMainActivityFromLauncher();
        assertReachedCodeProfilerIsEnabled();
        Assert.assertEquals(42, LibraryLoader.getReachedCodeSamplingIntervalUs());
    }

    /**
     * Tests that the feature state is cached in shared preferences after native initialization.
     * This test imitates the first run when the feature is enabled.
     */
    @Test
    @SmallTest
    @EnableFeatures(ChromeFeatureList.REACHED_CODE_PROFILER)
    public void testSharedPreferenceIsCached_Enable() {
        mActivityTestRule.startMainActivityFromLauncher();

        Assert.assertEquals(10000, LibraryLoader.getReachedCodeSamplingIntervalUs());
        // Enabling takes effect only on the second startup.
        Assert.assertFalse(ReachedCodeProfiler.isEnabled());
    }

    /**
     * Tests that the feature state is cached in shared preferences after native initialization.
     * This test imitates disabling the reached code profiler after it has been enabled for some
     * time.
     */
    @Test
    @SmallTest
    @DisableFeatures(ChromeFeatureList.REACHED_CODE_PROFILER)
    public void testSharedPreferenceIsCached_Disable() {
        LibraryLoader.setReachedCodeProfilerEnabledOnNextRuns(true, 0);
        mActivityTestRule.startMainActivityFromLauncher();
        Assert.assertEquals(0, LibraryLoader.getReachedCodeSamplingIntervalUs());
        // Disabling takes effect only on the second startup.
        assertReachedCodeProfilerIsEnabled();
    }

    /**
     * Tests that the reached code profiler field trial group is saved in shared preferences for
     * being used on the next startup.
     */
    @Test
    @SmallTest
    @CommandLineFlags.
    Add("force-fieldtrials=" + ChromeFeatureList.REACHED_CODE_PROFILER + "/" + FAKE_GROUP_NAME)
    public void testSharedPreferenceTrialGroupIsCached() {
        mActivityTestRule.startMainActivityFromLauncher();
        Assert.assertEquals(FAKE_GROUP_NAME,
                SharedPreferencesManager.getInstance().readString(
                        ChromePreferenceKeys.REACHED_CODE_PROFILER_GROUP, null));
    }

    /**
     * The reached code profiler is always disabled in some configurations. This helper allows to
     * check if the profiler is enabled in supported configurations.
     */
    private void assertReachedCodeProfilerIsEnabled() {
        if (!ReachedCodeProfiler.isSupported()) {
            Assert.assertFalse(ReachedCodeProfiler.isEnabled());
            return;
        }

        Assert.assertTrue(ReachedCodeProfiler.isEnabled());
    }
}
