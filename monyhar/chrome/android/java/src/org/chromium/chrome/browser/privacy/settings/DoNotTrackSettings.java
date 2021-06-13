// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.privacy.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.preferences.Pref;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.components.browser_ui.settings.ChromeSwitchPreference;
import org.monyhar.components.browser_ui.settings.SettingsUtils;
import org.monyhar.components.prefs.PrefService;
import org.monyhar.components.user_prefs.UserPrefs;

/**
 * Fragment to manage 'Do Not Track' preference and to explain to the user what it does.
 */
public class DoNotTrackSettings extends PreferenceFragmentCompat {
    // Must match key in do_not_track_preferences.xml.
    private static final String PREF_DO_NOT_TRACK_SWITCH = "do_not_track_switch";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        SettingsUtils.addPreferencesFromResource(this, R.xml.do_not_track_preferences);
        getActivity().setTitle(R.string.do_not_track_title);

        ChromeSwitchPreference doNotTrackSwitch =
                (ChromeSwitchPreference) findPreference(PREF_DO_NOT_TRACK_SWITCH);

        PrefService prefService = UserPrefs.get(Profile.getLastUsedRegularProfile());
        boolean isDoNotTrackEnabled = prefService.getBoolean(Pref.ENABLE_DO_NOT_TRACK);
        doNotTrackSwitch.setChecked(isDoNotTrackEnabled);

        doNotTrackSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            prefService.setBoolean(Pref.ENABLE_DO_NOT_TRACK, (boolean) newValue);
            return true;
        });
    }
}
