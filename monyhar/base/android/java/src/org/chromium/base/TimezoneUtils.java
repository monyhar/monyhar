// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.base;

import android.os.StrictMode;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.MainDex;

import java.util.TimeZone;

@JNINamespace("base::android")
@MainDex
class TimezoneUtils {
    /**
     * Guards this class from being instantiated.
     */

    private TimezoneUtils() {}

    /**
     * @return the Olson timezone ID of the current system time zone.
     */
    @CalledByNative
    private static String getDefaultTimeZoneId() {
        // On Android N or earlier, getting the default timezone requires the disk
        // access when a device set up is skipped.
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        String timezoneID = TimeZone.getDefault().getID();
        StrictMode.setThreadPolicy(oldPolicy);
        return timezoneID;
    }
}
