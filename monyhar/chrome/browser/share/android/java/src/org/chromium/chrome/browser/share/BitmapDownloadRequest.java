// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.share;

import android.graphics.Bitmap;

import org.monyhar.base.annotations.NativeMethods;

/**
 * A Java API for requesting bitmap download from Chrome's download manager service.
 */
public class BitmapDownloadRequest {
    public static void downloadBitmap(String fileName, Bitmap bitmap) {
        BitmapDownloadRequestJni.get().downloadBitmap(fileName, bitmap);
    }

    @NativeMethods
    interface Natives {
        void downloadBitmap(String fileName, Bitmap bitmap);
    }
}
