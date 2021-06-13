// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.share;

import androidx.annotation.Nullable;

import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.components.embedder_support.util.UrlConstants;
import org.monyhar.url.GURL;

/**
 *  A collection of helper functions for sharing in a non static context.
 */
public class ShareUtils {
    /*
     * Determines whether a tab is eligible to be shared.
     *
     * @param tab The tab being tested.
     */
    public boolean shouldEnableShare(@Nullable Tab tab) {
        if (tab == null) {
            return false;
        }

        GURL url = tab.getUrl();
        boolean isChromeScheme = url.getScheme().equals(UrlConstants.CHROME_SCHEME)
                || url.getScheme().equals(UrlConstants.CHROME_NATIVE_SCHEME);
        boolean isDataScheme = url.getScheme().equals(UrlConstants.DATA_SCHEME);

        return !isChromeScheme && !isDataScheme;
    }
}
