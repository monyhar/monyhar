// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.android.webview.monyhar;

import android.webkit.ValueCallback;

import org.monyhar.base.Callback;

/**
 * Utility class for converting a {@link android.webkit.ValueCallback} into a
 * {@link org.monyhar.base.Callback}.
 */
public final class CallbackConverter {
    public static <T> Callback<T> fromValueCallback(final ValueCallback<T> valueCallback) {
        return valueCallback == null ? null : result -> valueCallback.onReceiveValue(result);
    }

    // Do not instantiate this class
    private CallbackConverter() {}
}
