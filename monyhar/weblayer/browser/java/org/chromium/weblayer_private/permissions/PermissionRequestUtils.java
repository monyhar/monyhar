// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer_private.permissions;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.components.permissions.AndroidPermissionRequester;
import org.monyhar.ui.base.WindowAndroid;

/** Util functions to request Android permissions for a content setting. */
@JNINamespace("weblayer")
public final class PermissionRequestUtils {
    @CalledByNative
    private static void requestPermission(
            WindowAndroid windowAndroid, long nativeCallback, int[] contentSettingsTypes) {
        if (!AndroidPermissionRequester.requestAndroidPermissions(windowAndroid,
                    contentSettingsTypes, new AndroidPermissionRequester.RequestDelegate() {
                        @Override
                        public void onAndroidPermissionAccepted() {
                            PermissionRequestUtilsJni.get().onResult(nativeCallback, true);
                        }

                        @Override
                        public void onAndroidPermissionCanceled() {
                            PermissionRequestUtilsJni.get().onResult(nativeCallback, false);
                        }
                    })) {
            PermissionRequestUtilsJni.get().onResult(nativeCallback, false);
        }
    }

    @NativeMethods
    interface Natives {
        void onResult(long callback, boolean result);
    }
}
