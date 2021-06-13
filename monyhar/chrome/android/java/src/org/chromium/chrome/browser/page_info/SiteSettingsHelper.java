// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.page_info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.monyhar.base.StrictModeContext;
import org.monyhar.chrome.browser.offlinepages.OfflinePageUtils;
import org.monyhar.chrome.browser.settings.SettingsLauncherImpl;
import org.monyhar.components.browser_ui.settings.SettingsLauncher;
import org.monyhar.components.browser_ui.site_settings.ContentSettingsResources;
import org.monyhar.components.browser_ui.site_settings.SingleCategorySettings;
import org.monyhar.components.browser_ui.site_settings.SingleWebsiteSettings;
import org.monyhar.components.browser_ui.site_settings.SiteSettingsCategory;
import org.monyhar.components.dom_distiller.core.DomDistillerUrlUtils;
import org.monyhar.components.embedder_support.util.UrlUtilities;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.url.GURL;

/**
 * This class contains helper methods for determining site settings availability and showing the
 * site settings page.
 */
public class SiteSettingsHelper {
    /**
     * Whether site settings is available for a given {@link WebContents}.
     * @param webContents The WebContents for which to check the site settings.
     */
    public static boolean isSiteSettingsAvailable(WebContents webContents) {
        boolean isOfflinePage = OfflinePageUtils.getOfflinePage(webContents) != null;
        // TODO(crbug.com/1033178): dedupe the DomDistillerUrlUtils#getOriginalUrlFromDistillerUrl()
        // calls.
        GURL url = DomDistillerUrlUtils.getOriginalUrlFromDistillerUrl(webContents.getVisibleUrl());
        return !isOfflinePage && url != null && UrlUtilities.isHttpOrHttps(url);
    }

    /**
     * Shows the site settings activity for a given url.
     */
    public static void showSiteSettings(Context context, String fullUrl) {
        SettingsLauncher settingsLauncher = new SettingsLauncherImpl();
        Intent preferencesIntent = settingsLauncher.createSettingsActivityIntent(context,
                SingleWebsiteSettings.class.getName(),
                SingleWebsiteSettings.createFragmentArgsForSite(fullUrl));
        launchIntent(context, preferencesIntent);
    }

    /**
     * Show the single category settings page for given category and type.
     */
    public static void showCategorySettings(
            Context context, @SiteSettingsCategory.Type int category) {
        SettingsLauncher settingsLauncher = new SettingsLauncherImpl();
        Bundle extras = new Bundle();
        extras.putString(SingleCategorySettings.EXTRA_CATEGORY,
                SiteSettingsCategory.preferenceKey(category));
        extras.putString(SingleCategorySettings.EXTRA_TITLE,
                context.getResources().getString(ContentSettingsResources.getTitle(
                        SiteSettingsCategory.contentSettingsType(category))));
        Intent preferencesIntent = settingsLauncher.createSettingsActivityIntent(
                context, SingleCategorySettings.class.getName(), extras);
        launchIntent(context, preferencesIntent);
    }

    private static void launchIntent(Context context, Intent intent) {
        // Disabling StrictMode to avoid violations (https://crbug.com/819410).
        try (StrictModeContext ignored = StrictModeContext.allowDiskReads()) {
            context.startActivity(intent);
        }
    }
}
