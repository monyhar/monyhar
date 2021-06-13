// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.permissions;

import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.content_public.browser.WebContents;

/**
 * Utility class that interacts with native to retrieve and set permission-related settings.
 */
public class PermissionSettingsBridge {
    public static boolean shouldShowNotificationsPromo(WebContents webContents) {
        return PermissionSettingsBridgeJni.get().shouldShowNotificationsPromo(
                getProfile(), webContents);
    }

    public static void didShowNotificationsPromo() {
        PermissionSettingsBridgeJni.get().didShowNotificationsPromo(getProfile());
    }

    private static Profile getProfile() {
        return Profile.getLastUsedRegularProfile();
    }

    @NativeMethods
    public interface Natives {
        boolean shouldShowNotificationsPromo(Profile profile, WebContents webContents);
        void didShowNotificationsPromo(Profile profile);
    }
}
