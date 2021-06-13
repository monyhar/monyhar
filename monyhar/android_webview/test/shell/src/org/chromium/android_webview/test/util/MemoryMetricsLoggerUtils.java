// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.android_webview.test.util;

import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;

/**
 * Utilities for force recording memory metrics.
 */
@JNINamespace("android_webview")
public class MemoryMetricsLoggerUtils {
    @NativeMethods
    public interface Natives {
        /**
         * Calls to MemoryMetricsLogger to force recording histograms, returning true on success.
         * A value of false means recording failed (most likely because process metrics not
         * available.
         */
        boolean forceRecordHistograms();
    }
}
