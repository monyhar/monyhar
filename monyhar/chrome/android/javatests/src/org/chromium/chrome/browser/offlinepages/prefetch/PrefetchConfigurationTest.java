// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.offlinepages.prefetch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.filters.MediumTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.offlinepages.OfflineTestUtil;
import org.monyhar.chrome.browser.profiles.ProfileKey;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ReducedModeNativeTestRule;
import org.monyhar.chrome.test.util.browser.Features;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/** Integration tests for {@link PrefetchConfiguration}. */
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
@RunWith(ChromeJUnit4ClassRunner.class)
public class PrefetchConfigurationTest {
    @Rule
    public ReducedModeNativeTestRule mNativeTestRule = new ReducedModeNativeTestRule();

    private ProfileKey mProfileKey;

    @Before
    public void setUp() throws Exception {
        mProfileKey = ProfileKey.getLastUsedRegularProfileKey();
        Assert.assertNotNull(mProfileKey);
    }

    @Test
    @MediumTest
    @Feature("OfflinePrefetch")
    @Features.EnableFeatures(ChromeFeatureList.OFFLINE_PAGES_PREFETCHING)
    public void testWithPrefetchingFeatureEnabled() {
        AtomicBoolean isFlagEnabled = new AtomicBoolean();
        AtomicBoolean isEnabled = new AtomicBoolean();
        OfflineTestUtil.setPrefetchingEnabledByServer(true);
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            isFlagEnabled.set(PrefetchConfiguration.isPrefetchingFlagEnabled());
            isEnabled.set(PrefetchConfiguration.isPrefetchingEnabled(mProfileKey));
        });
        assertTrue(isFlagEnabled.get());
        assertTrue(isEnabled.get());

        TestThreadUtils.runOnUiThreadBlocking(() -> {
            // Disable prefetching user setting.
            PrefetchConfiguration.setPrefetchingEnabledInSettings(mProfileKey, false);
            isFlagEnabled.set(PrefetchConfiguration.isPrefetchingFlagEnabled());
            isEnabled.set(PrefetchConfiguration.isPrefetchingEnabled(mProfileKey));
        });
        assertTrue(isFlagEnabled.get());
        assertFalse(isEnabled.get());

        TestThreadUtils.runOnUiThreadBlocking(() -> {
            // Re-enable prefetching user setting.
            PrefetchConfiguration.setPrefetchingEnabledInSettings(mProfileKey, true);
            isFlagEnabled.set(PrefetchConfiguration.isPrefetchingFlagEnabled());
            isEnabled.set(PrefetchConfiguration.isPrefetchingEnabled(mProfileKey));
        });
        assertTrue(isFlagEnabled.get());
        assertTrue(isEnabled.get());
    }

    @Test
    @MediumTest
    @Feature("OfflinePrefetch")
    @Features.DisableFeatures(ChromeFeatureList.OFFLINE_PAGES_PREFETCHING)
    public void testWithPrefetchingFeatureDisabled() {
        AtomicBoolean isFlagEnabled = new AtomicBoolean();
        AtomicBoolean isEnabled = new AtomicBoolean();
        OfflineTestUtil.setPrefetchingEnabledByServer(true);
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            isFlagEnabled.set(PrefetchConfiguration.isPrefetchingFlagEnabled());
            isEnabled.set(PrefetchConfiguration.isPrefetchingEnabled(mProfileKey));
        });
        assertFalse(isFlagEnabled.get());
        assertFalse(isEnabled.get());

        TestThreadUtils.runOnUiThreadBlocking(() -> {
            // Disable prefetching user setting.
            PrefetchConfiguration.setPrefetchingEnabledInSettings(mProfileKey, false);
            isFlagEnabled.set(PrefetchConfiguration.isPrefetchingFlagEnabled());
            isEnabled.set(PrefetchConfiguration.isPrefetchingEnabled(mProfileKey));
        });
        assertFalse(isFlagEnabled.get());
        assertFalse(isEnabled.get());

        TestThreadUtils.runOnUiThreadBlocking(() -> {
            // Re-enable prefetching user setting.
            PrefetchConfiguration.setPrefetchingEnabledInSettings(mProfileKey, true);
            isFlagEnabled.set(PrefetchConfiguration.isPrefetchingFlagEnabled());
            isEnabled.set(PrefetchConfiguration.isPrefetchingEnabled(mProfileKey));
        });
        assertFalse(isFlagEnabled.get());
        assertFalse(isEnabled.get());
    }
}
