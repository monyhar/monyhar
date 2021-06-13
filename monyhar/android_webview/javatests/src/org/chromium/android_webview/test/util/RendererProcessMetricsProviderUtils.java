// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.android_webview.test.util;

import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;

/**
 * Utilities for force recording renderer process metrics.
 */
@JNINamespace("android_webview")
public class RendererProcessMetricsProviderUtils {
    @NativeMethods
    public interface Natives {
        /**
         * Calls to RendererProcessMetricsProvider to force record histograms.
         */
        void forceRecordHistograms();
    }
}
