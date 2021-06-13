// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.android_webview;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.components.embedder_support.util.WebResourceResponseInfo;

/**
 * The response information that is to be returned for a particular resource fetch.
 */
@JNINamespace("android_webview")
public class AwWebResourceInterceptResponse {
    private WebResourceResponseInfo mResponse;
    private boolean mRaisedException;

    public AwWebResourceInterceptResponse(
            WebResourceResponseInfo response, boolean raisedException) {
        mResponse = response;
        mRaisedException = raisedException;
    }

    @CalledByNative
    public WebResourceResponseInfo getResponse() {
        return mResponse;
    }

    @CalledByNative
    public boolean getRaisedException() {
        return mRaisedException;
    }
}
