// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.tracing.settings;

import android.os.Bundle;

import androidx.annotation.VisibleForTesting;
import androidx.preference.PreferenceFragmentCompat;

import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.preferences.ChromePreferenceKeys;
import org.monyhar.chrome.browser.preferences.SharedPreferencesManager;
import org.monyhar.chrome.browser.version.ChromeVersionInfo;
import org.monyhar.components.browser_ui.settings.SettingsUtils;
import org.monyhar.components.version_info.Channel;
import org.monyhar.components.version_info.VersionConstants;

/**
 * Settings fragment containing preferences aimed at Chrome and web developers.
 */
public class DeveloperSettings extends PreferenceFragmentCompat {
    private static final String UI_PREF_BETA_STABLE_HINT = "beta_stable_hint";

    // Non-translated strings:
    private static final String MSG_DEVELOPER_OPTIONS_TITLE = "Developer options";

    private static Boolean sIsEnabledForTests;

    public static boolean shouldShowDeveloperSettings() {
        // Always enabled on canary, dev and local builds, otherwise can be enabled by tapping the
        // Chrome version in Settings>About multiple times.
        if (sIsEnabledForTests != null) return sIsEnabledForTests;

        if (VersionConstants.CHANNEL <= Channel.DEV) return true;
        return SharedPreferencesManager.getInstance().readBoolean(
                ChromePreferenceKeys.SETTINGS_DEVELOPER_ENABLED, false);
    }

    public static void setDeveloperSettingsEnabled() {
        SharedPreferencesManager.getInstance().writeBoolean(
                ChromePreferenceKeys.SETTINGS_DEVELOPER_ENABLED, true);
    }

    @VisibleForTesting
    public static void setIsEnabledForTests(Boolean isEnabled) {
        sIsEnabledForTests = isEnabled;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String s) {
        getActivity().setTitle(MSG_DEVELOPER_OPTIONS_TITLE);
        SettingsUtils.addPreferencesFromResource(this, R.xml.developer_preferences);

        if (ChromeVersionInfo.isBetaBuild() || ChromeVersionInfo.isStableBuild()) {
            getPreferenceScreen().removePreference(findPreference(UI_PREF_BETA_STABLE_HINT));
        }
    }
}
