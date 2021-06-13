// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser;

import org.monyhar.chrome.browser.base.SplitCompatGcmTaskService;
import org.monyhar.chrome.browser.base.SplitCompatUtils;

/** See {@link ChromeBackgroundServiceImpl}. */
public class ChromeBackgroundService extends SplitCompatGcmTaskService {
    public ChromeBackgroundService() {
        super(SplitCompatUtils.getIdentifierName(
                "org.monyhar.chrome.browser.ChromeBackgroundServiceImpl"));
    }
}
