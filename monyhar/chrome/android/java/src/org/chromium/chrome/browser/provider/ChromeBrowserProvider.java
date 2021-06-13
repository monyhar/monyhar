// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.provider;

import org.monyhar.chrome.browser.base.SplitCompatContentProvider;
import org.monyhar.chrome.browser.base.SplitCompatUtils;

/** See {@link ChromeBrowserProviderImpl}. */
public class ChromeBrowserProvider extends SplitCompatContentProvider {
    public ChromeBrowserProvider() {
        super(SplitCompatUtils.getIdentifierName(
                "org.monyhar.chrome.browser.provider.ChromeBrowserProviderImpl"));
    }
}
