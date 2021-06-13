// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.about_settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import org.monyhar.chrome.R;
import org.monyhar.components.browser_ui.settings.SettingsUtils;

/**
 * Fragment to display legal information about Chrome.
 */
public class LegalInformationSettings extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String s) {
        SettingsUtils.addPreferencesFromResource(this, R.xml.legal_information_preferences);
        getActivity().setTitle(R.string.legal_information_title);
    }
}
