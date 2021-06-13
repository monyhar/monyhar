// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.crash;

import org.monyhar.chrome.browser.base.SplitCompatMinidumpUploadJobService;
import org.monyhar.chrome.browser.base.SplitCompatUtils;

/** See {@link ChromeMinidumpUploadJobServiceImpl}. */
public class ChromeMinidumpUploadJobService extends SplitCompatMinidumpUploadJobService {
    public ChromeMinidumpUploadJobService() {
        super(SplitCompatUtils.getIdentifierName(
                "org.monyhar.chrome.browser.crash.ChromeMinidumpUploadJobServiceImpl"));
    }
}
