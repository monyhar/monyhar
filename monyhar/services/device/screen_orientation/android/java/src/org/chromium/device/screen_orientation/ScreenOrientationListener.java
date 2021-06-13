// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.device.screen_orientation;

import android.provider.Settings;

import org.monyhar.base.ContextUtils;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;

/**
 * Android implementation details for device::ScreenOrientationListenerAndroid.
 */
@JNINamespace("device")
class ScreenOrientationListener {
    @CalledByNative
    static boolean isAutoRotateEnabledByUser() {
        return Settings.System.getInt(ContextUtils.getApplicationContext().getContentResolver(),
                       Settings.System.ACCELEROMETER_ROTATION, 0)
                == 1;
    }
}
