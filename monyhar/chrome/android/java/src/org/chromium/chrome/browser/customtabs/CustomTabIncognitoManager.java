// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.customtabs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.monyhar.base.CommandLine;
import org.monyhar.base.UnownedUserData;
import org.monyhar.base.UnownedUserDataKey;
import org.monyhar.base.annotations.CheckDiscard;
import org.monyhar.chrome.browser.browserservices.intents.BrowserServicesIntentDataProvider;
import org.monyhar.chrome.browser.customtabs.content.CustomTabActivityNavigationController;
import org.monyhar.chrome.browser.customtabs.content.CustomTabActivityTabProvider;
import org.monyhar.chrome.browser.dependency_injection.ActivityScope;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.lifecycle.ActivityLifecycleDispatcher;
import org.monyhar.chrome.browser.lifecycle.DestroyObserver;
import org.monyhar.chrome.browser.lifecycle.NativeInitObserver;
import org.monyhar.chrome.browser.profiles.OTRProfileID;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.ui.base.WindowAndroid;

import javax.inject.Inject;

/**
 * Implements incognito tab host for the given instance of Custom Tab activity.
 * This class exists for every custom tab, but its only active if
 * |isEnabledIncognitoCCT| returns true.
 */
@ActivityScope
public class CustomTabIncognitoManager
        implements NativeInitObserver, DestroyObserver, UnownedUserData {
    @SuppressLint("StaticFieldLeak") // This is for test only.
    private static CustomTabIncognitoManager sCustomTabIncognitoManagerUsedForTesting;

    private static final String TAG = "CctIncognito";
    /** The key for accessing this object on an {@link org.monyhar.base.UnownedUserDataHost}. */
    private static final UnownedUserDataKey<CustomTabIncognitoManager> KEY =
            new UnownedUserDataKey<>(CustomTabIncognitoManager.class);

    private final Activity mActivity;
    private final BrowserServicesIntentDataProvider mIntentDataProvider;
    private final WindowAndroid mWindowAndroid;

    private OTRProfileID mOTRProfileID;

    @Inject
    public CustomTabIncognitoManager(Activity activity, WindowAndroid windowAndroid,
            BrowserServicesIntentDataProvider intentDataProvider,
            CustomTabActivityNavigationController navigationController,
            CustomTabActivityTabProvider tabProvider,
            ActivityLifecycleDispatcher lifecycleDispatcher) {
        mActivity = activity;
        mWindowAndroid = windowAndroid;
        mIntentDataProvider = intentDataProvider;

        lifecycleDispatcher.register(this);

        attach(mWindowAndroid, this);
    }

    @CheckDiscard("Test-only setter.")
    @VisibleForTesting
    public static void setCustomTabIncognitoManagerUsedForTesting(
            CustomTabIncognitoManager customTabIncognitoManager) {
        sCustomTabIncognitoManagerUsedForTesting = customTabIncognitoManager;
    }

    /**
     * Get the Activity's {@link CustomTabIncognitoManager} from the provided {@link
     * WindowAndroid}.
     * @param window The window to get the manager from.
     * @return The Activity's {@link CustomTabIncognitoManager}.
     */
    public static @Nullable CustomTabIncognitoManager from(WindowAndroid window) {
        if (sCustomTabIncognitoManagerUsedForTesting != null) {
            return sCustomTabIncognitoManagerUsedForTesting;
        }

        return KEY.retrieveDataFromHost(window.getUnownedUserDataHost());
    }

    /**
     * Make this instance of CustomTabIncognitoManager available through the activity's window.
     * @param window A {@link WindowAndroid} to attach to.
     * @param manager The {@link CustomTabIncognitoManager} to attach.
     */
    private static void attach(WindowAndroid window, CustomTabIncognitoManager manager) {
        KEY.attachToHost(window.getUnownedUserDataHost(), manager);
    }

    /**
     * Detach the provided CustomTabIncognitoManager from any host it is associated with.
     * @param manager The {@link CustomTabIncognitoManager} to detach.
     */
    private static void detach(CustomTabIncognitoManager manager) {
        KEY.detachFromAllHosts(manager);
    }

    public Profile getProfile() {
        if (mOTRProfileID == null) mOTRProfileID = OTRProfileID.createUnique("CCT:Incognito");
        return Profile.getLastUsedRegularProfile().getOffTheRecordProfile(
                mOTRProfileID, /*createIfNeeded=*/true);
    }

    @Override
    public void onFinishNativeInitialization() {
        if (mIntentDataProvider.isIncognito()) {
            initializeIncognito();
        }
    }

    @Override
    public void onDestroy() {
        if (mOTRProfileID != null) {
            Profile.getLastUsedRegularProfile()
                    .getOffTheRecordProfile(mOTRProfileID, /*createIfNeeded=*/true)
                    .destroyWhenAppropriate();
            mOTRProfileID = null;
        }

        detach(this);
    }

    private void initializeIncognito() {
        if (!CommandLine.getInstance().hasSwitch(
                    ChromeSwitches.ENABLE_INCOGNITO_SNAPSHOTS_IN_ANDROID_RECENTS)) {
            // Disable taking screenshots and seeing snapshots in recents.
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }
}
