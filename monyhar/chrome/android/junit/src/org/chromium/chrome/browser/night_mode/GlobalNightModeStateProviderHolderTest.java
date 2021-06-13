// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.night_mode;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.monyhar.chrome.browser.preferences.ChromePreferenceKeys.UI_THEME_SETTING;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import org.monyhar.base.test.BaseRobolectricTestRunner;
import org.monyhar.chrome.browser.preferences.SharedPreferencesManager;

/**
 * Unit tests for {@link GlobalNightModeStateProviderHolder}.
 */
@RunWith(BaseRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class GlobalNightModeStateProviderHolderTest {
    @After
    public void tearDown() {
        GlobalNightModeStateProviderHolder.resetInstanceForTesting();
        NightModeUtils.setNightModeSupportedForTesting(null);
        SharedPreferencesManager.getInstance().removeKey(UI_THEME_SETTING);
    }

    @Test
    public void testNightModeNotAvailable() {
        NightModeUtils.setNightModeSupportedForTesting(false);

        // Verify that night mode is disabled.
        assertFalse(GlobalNightModeStateProviderHolder.getInstance().isInNightMode());

        // Verify that night mode cannot be enabled.
        SharedPreferencesManager.getInstance().writeInt(UI_THEME_SETTING, ThemeType.DARK);
        assertFalse(GlobalNightModeStateProviderHolder.getInstance().isInNightMode());
    }

    @Test
    public void testNightModeAvailable() {
        // Verify that the instance is a GlobalNightModeStateController. Other tests are covered
        // in GlobalNightModeStateControllerTest.java.
        assertTrue(GlobalNightModeStateProviderHolder.getInstance()
                           instanceof GlobalNightModeStateController);
    }
}
