// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.base.profiler;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;

/**
 * Helper to run code through JNI layer to test JNI unwinding.
 */
@JNINamespace("base")
public final class TestSupport {
    @CalledByNative
    public static void callWithJavaFunction(long context) {
        TestSupportJni.get().invokeCallbackFunction(context);
    }

    @NativeMethods
    interface Natives {
        void invokeCallbackFunction(long context);
    }
}
