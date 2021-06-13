// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.services.gcm;

import android.annotation.SuppressLint;

import org.monyhar.chrome.browser.base.SplitCompatGcmListenerService;
import org.monyhar.chrome.browser.base.SplitCompatUtils;

/**
 * See {@link ChromeGcmListenerServiceImpl}.
 * Suppressing linting as onNewToken() is implemented in base class.
 */
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class ChromeGcmListenerService extends SplitCompatGcmListenerService {
    public ChromeGcmListenerService() {
        super(SplitCompatUtils.getIdentifierName(
                "org.monyhar.chrome.browser.services.gcm.ChromeGcmListenerServiceImpl"));
    }
}
