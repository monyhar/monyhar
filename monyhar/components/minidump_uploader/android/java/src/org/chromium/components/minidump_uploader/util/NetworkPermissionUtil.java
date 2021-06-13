// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.minidump_uploader.util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * A container for determining whether it's ok to upload crash reports over the currently active
 * network.
 */
public class NetworkPermissionUtil {
    public static boolean isNetworkUnmetered(ConnectivityManager connectivityManager) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) return false;
        return !connectivityManager.isActiveNetworkMetered();
    }
}
