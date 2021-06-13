// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.crash.browser;

import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.base.annotations.UsedByReflection;
import org.monyhar.build.NativeLibraries;

@JNINamespace("crashpad")
final class CrashpadMain {
    @UsedByReflection("crashpad_linux.cc")
    public static void main(String[] argv) {
        try {
            for (String library : NativeLibraries.LIBRARIES) {
                System.loadLibrary(library);
            }
        } catch (UnsatisfiedLinkError e) {
            throw new RuntimeException(e);
        }
        CrashpadMainJni.get().crashpadMain(argv);
    }

    @NativeMethods
    interface Natives {
        void crashpadMain(String[] argv);
    }
}
