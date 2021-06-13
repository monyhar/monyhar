// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.module_installer.util;

import com.google.android.play.core.splitcompat.SplitCompat;

import org.monyhar.base.ContextUtils;
import org.monyhar.base.StrictModeContext;
import org.monyhar.base.ThreadUtils;

/**
 * PlayCore SplitCompat initializer for installing modules in the application context.
 */
class SplitCompatInitializer {
    private static volatile boolean sIsInitialized;

    public static void initApplication() {
        ThreadUtils.assertOnUiThread();

        if (sIsInitialized) {
            return;
        }

        // SplitCompat.install may copy modules into Chrome's internal folder or clean them up.
        try (StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SplitCompat.install(ContextUtils.getApplicationContext());
        }
        sIsInitialized = true;
    }

    public static boolean isInitialized() {
        return sIsInitialized;
    }
}
