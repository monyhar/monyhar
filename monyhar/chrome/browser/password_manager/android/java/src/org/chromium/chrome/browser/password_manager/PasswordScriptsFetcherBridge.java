// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.password_manager;

import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;

/**
 * Android bridge to |PasswordScriptsFetcher|.
 */
@JNINamespace("password_manager")
public class PasswordScriptsFetcherBridge {
    public static void prewarmCache() {
        PasswordScriptsFetcherBridgeJni.get().prewarmCache();
    }

    @NativeMethods
    interface Natives {
        void prewarmCache();
    }
}
