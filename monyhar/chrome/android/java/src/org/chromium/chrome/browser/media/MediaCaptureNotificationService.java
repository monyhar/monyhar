// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.media;

import org.monyhar.chrome.browser.base.SplitCompatService;
import org.monyhar.chrome.browser.base.SplitCompatUtils;

/** See {@link MediaCaptureNotificationServiceImpl}. */
public class MediaCaptureNotificationService extends SplitCompatService {
    public MediaCaptureNotificationService() {
        super(SplitCompatUtils.getIdentifierName(
                "org.monyhar.chrome.browser.media.MediaCaptureNotificationServiceImpl"));
    }
}
