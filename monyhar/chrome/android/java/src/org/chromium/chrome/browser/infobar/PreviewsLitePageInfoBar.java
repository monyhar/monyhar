// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.infobar;

import android.os.Bundle;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.datareduction.settings.DataReductionPreferenceFragment;
import org.monyhar.chrome.browser.settings.SettingsLauncherImpl;
import org.monyhar.components.browser_ui.settings.SettingsLauncher;
import org.monyhar.components.infobars.ConfirmInfoBar;
import org.monyhar.components.infobars.InfoBar;

/**
 * An InfoBar that lets the user know that Data Saver Lite Mode now also applies to HTTPS pages.
 */
public class PreviewsLitePageInfoBar extends ConfirmInfoBar {
    public static final String FROM_INFOBAR = "FromInfoBar";

    @CalledByNative
    private static InfoBar show(int iconId, String message, String linkText) {
        return new PreviewsLitePageInfoBar(iconId, message, linkText);
    }

    private PreviewsLitePageInfoBar(int iconDrawbleId, String message, String linkText) {
        super(iconDrawbleId, R.color.infobar_icon_drawable_color, null, message, linkText, null,
                null);
    }

    @Override
    public void onLinkClicked() {
        super.onLinkClicked();

        Bundle fragmentArgs = new Bundle();
        fragmentArgs.putBoolean(FROM_INFOBAR, true);
        SettingsLauncher settingsLauncher = new SettingsLauncherImpl();
        settingsLauncher.launchSettingsActivity(
                getContext(), DataReductionPreferenceFragment.class, fragmentArgs);
    }
}
