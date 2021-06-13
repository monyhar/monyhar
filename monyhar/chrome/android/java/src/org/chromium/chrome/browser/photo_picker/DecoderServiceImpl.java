// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.photo_picker;

import android.content.Intent;
import android.os.IBinder;

import org.monyhar.base.CommandLine;
import org.monyhar.base.Log;
import org.monyhar.base.PathUtils;
import org.monyhar.base.annotations.MainDex;
import org.monyhar.base.library_loader.LibraryLoader;
import org.monyhar.base.task.PostTask;
import org.monyhar.chrome.browser.base.SplitCompatApplication;
import org.monyhar.components.browser_ui.photo_picker.ImageDecoder;
import org.monyhar.content_public.browser.UiThreadTaskTraits;

/**
 * A service to accept requests to take image file contents and decode them.
 */
@MainDex
public class DecoderServiceImpl extends DecoderService.Impl {
    private static final String TAG = "DecoderService";

    private final ImageDecoder mDecoder = new ImageDecoder();

    @Override
    public void onCreate() {
        Log.i(TAG, "Decoder service process started");
        // DecoderService does not require flags, but LibraryLoader.ensureInitialized() checks for
        // --enable-low-end-device-mode. Rather than forwarding the flags from the browser process,
        // just assume no flags.
        if (!CommandLine.isInitialized()) {
            CommandLine.init(null);
        }

        // The decoder service relies on PathUtils.
        PostTask.runSynchronously(UiThreadTaskTraits.DEFAULT, () -> {
            PathUtils.setPrivateDataDirectorySuffix(
                    SplitCompatApplication.PRIVATE_DATA_DIRECTORY_SUFFIX);
        });

        LibraryLoader.getInstance().ensureInitialized();
        mDecoder.initializeSandbox();
        Log.i(TAG, "Decoder service process initialized");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Decoder process binding");
        return mDecoder;
    }
}
