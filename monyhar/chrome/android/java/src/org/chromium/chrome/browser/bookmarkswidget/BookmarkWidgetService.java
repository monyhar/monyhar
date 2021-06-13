// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.bookmarkswidget;

import org.monyhar.chrome.browser.base.SplitCompatRemoteViewsService;
import org.monyhar.chrome.browser.base.SplitCompatUtils;

/** See {@link BookmarkWidgetServiceImpl}. */
public class BookmarkWidgetService extends SplitCompatRemoteViewsService {
    public BookmarkWidgetService() {
        super(SplitCompatUtils.getIdentifierName(
                "org.monyhar.chrome.browser.bookmarkswidget.BookmarkWidgetServiceImpl"));
    }
}
