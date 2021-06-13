// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.thumbnail.generator;

import org.monyhar.base.Callback;
import org.monyhar.base.annotations.NativeMethods;

/**
 * A JNI bridge that owns a native side ThumbnailMediaParser, which parses media file safely in an
 * utility process.
 */
public class ThumbnailMediaParserBridge {
    private long mNativeThumbnailMediaParserBridge;

    /**
     * Creates a media parser to analyze media metadata and retrieve thumbnails.
     * @param mimeType The mime type of the media file.
     * @param filePath The absolute path of the media file.
     * @param totalSize Total size of the media file.
     * @param callback Callback to get the result.
     */
    public ThumbnailMediaParserBridge(
            String mimeType, String filePath, Callback<ThumbnailMediaData> callback) {
        mNativeThumbnailMediaParserBridge = ThumbnailMediaParserBridgeJni.get().init(
                ThumbnailMediaParserBridge.this, mimeType, filePath, callback);
    }

    /**
     * Destroys the native object of ThumbnailMediaParser. This will result in the utility process
     * being destroyed.
     */
    public void destroy() {
        ThumbnailMediaParserBridgeJni.get().destroy(
                mNativeThumbnailMediaParserBridge, ThumbnailMediaParserBridge.this);
        mNativeThumbnailMediaParserBridge = 0;
    }

    /**
     * Starts to parse a media file to retrieve media metadata and video thumbnail.
     */
    public void start() {
        if (mNativeThumbnailMediaParserBridge != 0) {
            ThumbnailMediaParserBridgeJni.get().start(
                    mNativeThumbnailMediaParserBridge, ThumbnailMediaParserBridge.this);
        }
    }

    @NativeMethods
    interface Natives {
        long init(ThumbnailMediaParserBridge caller, String mimeType, String filePath,
                Callback<ThumbnailMediaData> callback);
        void destroy(long nativeThumbnailMediaParserBridge, ThumbnailMediaParserBridge caller);
        void start(long nativeThumbnailMediaParserBridge, ThumbnailMediaParserBridge caller);
    }
}
