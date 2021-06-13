// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.password_manager;

import android.app.Activity;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.settings.SettingsLauncherImpl;
import org.monyhar.chrome.browser.signin.services.IdentityServicesProvider;
import org.monyhar.chrome.browser.sync.SyncService;
import org.monyhar.components.signin.identitymanager.IdentityManager;
import org.monyhar.components.sync.ModelType;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.ui.base.WindowAndroid;

import java.lang.ref.WeakReference;

/**
 * Bridge between Java and native PasswordManager code.
 */
public class PasswordManagerLauncher {
    private PasswordManagerLauncher() {}

    /**
     * Launches the password settings.
     * @param activity used to show the UI to manage passwords.
     */
    public static void showPasswordSettings(
            Activity activity, @ManagePasswordsReferrer int referrer) {
        if (isSyncingPasswordsWithoutCustomPassphrase()
                && ChromeFeatureList.isEnabled(ChromeFeatureList.PASSWORD_SCRIPTS_FETCHING)) {
            PasswordScriptsFetcherBridge.prewarmCache();
        }

        PasswordManagerHelper.showPasswordSettings(activity, referrer, new SettingsLauncherImpl());
    }

    @CalledByNative
    private static void showPasswordSettings(
            WebContents webContents, @ManagePasswordsReferrer int referrer) {
        WindowAndroid window = webContents.getTopLevelNativeWindow();
        if (window == null) return;
        WeakReference<Activity> currentActivity = window.getActivity();
        showPasswordSettings(currentActivity.get(), referrer);
    }

    public static boolean isSyncingPasswordsWithoutCustomPassphrase() {
        IdentityManager identityManager = IdentityServicesProvider.get().getIdentityManager(
                Profile.getLastUsedRegularProfile());
        if (!identityManager.hasPrimaryAccount()) return false;

        SyncService syncService = SyncService.get();
        if (syncService == null
                || !syncService.getActiveDataTypes().contains(ModelType.PASSWORDS)) {
            return false;
        }

        if (syncService.isUsingExplicitPassphrase()) return false;

        return true;
    }
}
