// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.infobar;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.chrome.browser.instantapps.InstantAppsBannerData;
import org.monyhar.chrome.browser.instantapps.InstantAppsHandler;
import org.monyhar.content_public.browser.WebContents;

/**
 * Delegate for {@link InstantAppsInfoBar}. Use launch() method to display the infobar.
 */
public class InstantAppsInfoBarDelegate {
    private static final String TAG = "IAInfoBarDelegate";

    private InstantAppsBannerData mData;

    public static void launch(InstantAppsBannerData data) {
        InstantAppsInfoBarDelegateJni.get().launch(
                data.getWebContents(), data, data.getUrl(), data.isInstantAppDefault());
    }

    @CalledByNative
    private static InstantAppsInfoBarDelegate create() {
        return new InstantAppsInfoBarDelegate();
    }

    private InstantAppsInfoBarDelegate() {}

    @CalledByNative
    private void openInstantApp(InstantAppsBannerData data) {
        InstantAppsHandler.getInstance().launchFromBanner(data);
    }

    @NativeMethods
    interface Natives {
        void launch(WebContents webContents, InstantAppsBannerData data, String url,
                boolean instantAppIsDefault);
    }
}
