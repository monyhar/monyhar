// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.metrics;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import org.monyhar.base.ContextUtils;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;

/**
 * Helps the native AndroidMetricsServiceClient call Android Java APIs over JNI.
 */
@JNINamespace("metrics")
public class AndroidMetricsServiceClient {
    private static final String PLAY_STORE_PACKAGE_NAME = "com.android.vending";

    @CalledByNative
    private static boolean canRecordPackageNameForAppType() {
        // Only record if it's a system app or it was installed from Play Store.
        Context ctx = ContextUtils.getApplicationContext();
        String packageName = ctx.getPackageName();
        String installerPackageName = ctx.getPackageManager().getInstallerPackageName(packageName);
        return (ctx.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM) != 0
                || (PLAY_STORE_PACKAGE_NAME.equals(installerPackageName));
    }

    @CalledByNative
    private static String getAppPackageName() {
        // Return this unconditionally; let native code enforce whether or not it's OK to include
        // this in the logs.
        Context ctx = ContextUtils.getApplicationContext();
        return ctx.getPackageName();
    }
}
