// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.download;

import android.net.Uri;

import org.monyhar.components.browser_ui.util.DownloadUtils;
import org.monyhar.components.download.DownloadDelegate;

/**
 * Utility class that implements DownloadDelegate.
 */
public class DownloadDelegateImpl extends DownloadDelegate {
    public DownloadDelegateImpl() {}

    @Override
    public String remapGenericMimeType(String mimeType, String url, String filename) {
        return MimeUtils.remapGenericMimeType(mimeType, url, filename);
    }

    @Override
    public Uri parseOriginalUrl(String originalUrl) {
        return DownloadUtils.parseOriginalUrl(originalUrl);
    }

    @Override
    public boolean isDownloadOnSDCard(String filePath) {
        return DownloadDirectoryProvider.isDownloadOnSDCard(filePath);
    }
}
