// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.omaha;

import org.monyhar.chrome.browser.base.SplitCompatIntentService;
import org.monyhar.chrome.browser.base.SplitCompatUtils;

/** See {@link OmahaClientImpl}. */
public class OmahaClient extends SplitCompatIntentService {
    private static final String TAG = "omaha";

    public OmahaClient() {
        super(SplitCompatUtils.getIdentifierName(
                      "org.monyhar.chrome.browser.omaha.OmahaClientImpl"),
                TAG);
    }
}
