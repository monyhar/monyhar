// Copyright 2013 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.android_webview;

import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;

/**
 * Exposes a subset of Chromium form database to Webview database for managing autocomplete
 * functionality.
 */
@JNINamespace("android_webview")
public class AwFormDatabase {

    public static boolean hasFormData() {
        return AwFormDatabaseJni.get().hasFormData();
    }

    public static void clearFormData() {
        AwFormDatabaseJni.get().clearFormData();
    }

    @NativeMethods
    interface Natives {
        boolean hasFormData();
        void clearFormData();
    }
}
