// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.night_mode;

import org.monyhar.chrome.browser.preferences.ChromePreferenceKeys;
import org.monyhar.chrome.browser.preferences.SharedPreferencesManager;

/**
 * Helper methods to be used in tests to specify night mode state. See also {@link
 * org.monyhar.ui.test.util.NightModeTestUtils}.
 */
public class ChromeNightModeTestUtils {
    /**
     * Sets up initial states for night mode before
     * {@link org.monyhar.chrome.browser.ChromeActivity} is launched.
     */
    public static void setUpNightModeBeforeChromeActivityLaunched() {
        NightModeUtils.setNightModeSupportedForTesting(true);
    }

    /**
     * Sets up the night mode state for {@link org.monyhar.chrome.browser.ChromeActivity}.
     * @param nightModeEnabled Whether night mode should be enabled.
     */
    public static void setUpNightModeForChromeActivity(boolean nightModeEnabled) {
        SharedPreferencesManager.getInstance().writeInt(ChromePreferenceKeys.UI_THEME_SETTING,
                nightModeEnabled ? ThemeType.DARK : ThemeType.LIGHT);
    }

    /**
     * Resets the night mode state after {@link org.monyhar.chrome.browser.ChromeActivity} is
     * destroyed.
     */
    public static void tearDownNightModeAfterChromeActivityDestroyed() {
        NightModeUtils.setNightModeSupportedForTesting(null);
        GlobalNightModeStateProviderHolder.resetInstanceForTesting();
        SharedPreferencesManager.getInstance().removeKey(ChromePreferenceKeys.UI_THEME_SETTING);
    }
}
