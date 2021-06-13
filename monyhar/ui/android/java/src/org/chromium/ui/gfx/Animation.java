// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.ui.gfx;

import android.annotation.TargetApi;
import android.os.Build;
import android.provider.Settings;

import org.monyhar.base.ContextUtils;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;

/**
 * Provides utility methods relating to system animation state on the current platform (i.e. Android
 * in this case). See ui/gfx/animation/animation_android.cc.
 */
@JNINamespace("gfx")
public class Animation {
    @CalledByNative
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static boolean prefersReducedMotion() {
        // We default to assuming that animations are enabled, to avoid impacting the experience for
        // users that don't have ANIMATOR_DURATION_SCALE defined.
        final float defaultScale = 1f;
        float durationScale =
                Settings.Global.getFloat(ContextUtils.getApplicationContext().getContentResolver(),
                        Settings.Global.ANIMATOR_DURATION_SCALE, defaultScale);
        return durationScale == 0.0;
    }
}
