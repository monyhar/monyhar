// Copyright 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.android_webview;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;

/**
 * Delegate for handling callbacks. All methods are called on the IO thread.
 *
 * You should create a separate instance for every WebContents that requires the
 * provided functionality.
 */
@JNINamespace("android_webview")
public abstract class AwContentsIoThreadClient {
    @CalledByNative
    public abstract int getCacheMode();

    @CalledByNative
    public abstract boolean shouldBlockContentUrls();

    @CalledByNative
    public abstract boolean shouldBlockFileUrls();

    @CalledByNative
    public abstract boolean shouldBlockNetworkLoads();

    @CalledByNative
    public abstract boolean shouldAcceptThirdPartyCookies();

    @CalledByNative
    public abstract boolean getSafeBrowsingEnabled();

    @CalledByNative
    public abstract AwContentsBackgroundThreadClient getBackgroundThreadClient();
}
