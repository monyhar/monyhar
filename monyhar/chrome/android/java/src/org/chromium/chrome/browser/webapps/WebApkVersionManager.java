// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.webapps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import org.monyhar.base.CommandLine;
import org.monyhar.base.ContextUtils;
import org.monyhar.base.FileUtils;
import org.monyhar.base.StrictModeContext;
import org.monyhar.base.ThreadUtils;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.preferences.ChromePreferenceKeys;
import org.monyhar.chrome.browser.preferences.SharedPreferencesManager;
import org.monyhar.webapk.lib.client.DexOptimizer;
import org.monyhar.webapk.lib.client.WebApkVersion;
import org.monyhar.webapk.lib.common.WebApkCommonUtils;

import java.io.File;

/**
 * Updates installed WebAPKs after a Chrome update.
 */
public class WebApkVersionManager {

    /**
     * Tries to extract the WebAPK runtime dex from the Chrome APK if it has not tried already.
     * Should not be called on UI thread.
     */
    // TODO(crbug.com/635567): Fix this properly.
    @SuppressLint("SetWorldReadable")
    public static void updateWebApksIfNeeded() {
        assert !ThreadUtils.runningOnUiThread();

        SharedPreferencesManager preferences = SharedPreferencesManager.getInstance();
        int extractedDexVersion =
                preferences.readInt(ChromePreferenceKeys.WEBAPK_EXTRACTED_DEX_VERSION, -1);
        int lastSdkVersion = preferences.readInt(ChromePreferenceKeys.WEBAPK_LAST_SDK_VERSION, -1);
        if (!CommandLine.getInstance().hasSwitch(
                    ChromeSwitches.ALWAYS_EXTRACT_WEBAPK_RUNTIME_DEX_ON_STARTUP)
                && extractedDexVersion == WebApkVersion.CURRENT_RUNTIME_DEX_VERSION
                && lastSdkVersion == Build.VERSION.SDK_INT) {
            return;
        }

        preferences.writeInt(ChromePreferenceKeys.WEBAPK_EXTRACTED_DEX_VERSION,
                WebApkVersion.CURRENT_RUNTIME_DEX_VERSION);
        preferences.writeInt(ChromePreferenceKeys.WEBAPK_LAST_SDK_VERSION, Build.VERSION.SDK_INT);

        Context context = ContextUtils.getApplicationContext();
        File dexDir = context.getDir("dex", Context.MODE_PRIVATE);
        FileUtils.recursivelyDeleteFile(dexDir, FileUtils.DELETE_ALL);

        // Recreate world-executable directory using {@link Context#getDir}.
        dexDir = context.getDir("dex", Context.MODE_PRIVATE);

        String dexName =
                WebApkCommonUtils.getRuntimeDexName(WebApkVersion.CURRENT_RUNTIME_DEX_VERSION);
        File dexFile = new File(dexDir, dexName);
        if (!FileUtils.extractAsset(context, dexName, dexFile)) {
            return;
        }

        // Disable VM detectLeakedClosableObjects due to Android SDK bug: https://crbug.com/750196
        try (StrictModeContext ignored = StrictModeContext.allowAllVmPolicies()) {
            if (!DexOptimizer.optimize(dexFile)) {
                return;
            }
        }

        // Make dex file world-readable so that WebAPK can use it.
        dexFile.setReadable(true, false);
    }
}
