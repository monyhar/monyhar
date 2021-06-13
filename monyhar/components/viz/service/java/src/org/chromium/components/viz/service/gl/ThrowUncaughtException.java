// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.viz.service.gl;

import org.monyhar.base.ThreadUtils;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.MainDex;

@MainDex
abstract class ThrowUncaughtException {
    @CalledByNative
    private static void post() {
        ThreadUtils.postOnUiThread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Intentional exception not caught by JNI");
            }
        });
    }
}
