// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.site_settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;

import org.monyhar.chrome.browser.settings.SettingsActivity;
import org.monyhar.chrome.browser.settings.SettingsLauncherImpl;
import org.monyhar.components.browser_ui.settings.SettingsLauncher;
import org.monyhar.components.browser_ui.site_settings.SingleCategorySettings;
import org.monyhar.components.browser_ui.site_settings.SingleWebsiteSettings;
import org.monyhar.components.browser_ui.site_settings.SiteSettings;
import org.monyhar.components.browser_ui.site_settings.SiteSettingsCategory;
import org.monyhar.components.browser_ui.site_settings.Website;

/**
 * Util functions for testing SiteSettings functionality.
 */
public class SiteSettingsTestUtils {
    public static SettingsActivity startSiteSettingsMenu(String category) {
        Bundle fragmentArgs = new Bundle();
        fragmentArgs.putString(SingleCategorySettings.EXTRA_CATEGORY, category);
        SettingsLauncher settingsLauncher = new SettingsLauncherImpl();
        Intent intent = settingsLauncher.createSettingsActivityIntent(
                InstrumentationRegistry.getTargetContext(), SiteSettings.class.getName(),
                fragmentArgs);
        return (SettingsActivity) InstrumentationRegistry.getInstrumentation().startActivitySync(
                intent);
    }

    public static SettingsActivity startSiteSettingsCategory(@SiteSettingsCategory.Type int type) {
        Bundle fragmentArgs = new Bundle();
        fragmentArgs.putString(
                SingleCategorySettings.EXTRA_CATEGORY, SiteSettingsCategory.preferenceKey(type));
        SettingsLauncher settingsLauncher = new SettingsLauncherImpl();
        Intent intent = settingsLauncher.createSettingsActivityIntent(
                InstrumentationRegistry.getTargetContext(), SingleCategorySettings.class.getName(),
                fragmentArgs);
        return (SettingsActivity) InstrumentationRegistry.getInstrumentation().startActivitySync(
                intent);
    }

    public static SettingsActivity startSingleWebsitePreferences(Website site) {
        Bundle fragmentArgs = new Bundle();
        fragmentArgs.putSerializable(SingleWebsiteSettings.EXTRA_SITE, site);
        SettingsLauncher settingsLauncher = new SettingsLauncherImpl();
        Intent intent = settingsLauncher.createSettingsActivityIntent(
                InstrumentationRegistry.getTargetContext(), SingleWebsiteSettings.class.getName(),
                fragmentArgs);
        return (SettingsActivity) InstrumentationRegistry.getInstrumentation().startActivitySync(
                intent);
    }
}
