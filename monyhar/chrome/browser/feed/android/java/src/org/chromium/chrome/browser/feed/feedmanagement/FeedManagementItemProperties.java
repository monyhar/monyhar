// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.feed.feedmanagement;

import android.view.View.OnClickListener;

import org.monyhar.ui.modelutil.PropertyKey;
import org.monyhar.ui.modelutil.PropertyModel.WritableObjectPropertyKey;

/**
 * Items for the list view in the feed management activity.
 */
public class FeedManagementItemProperties {
    public static final int DEFAULT_ITEM_TYPE = 0;

    public static final WritableObjectPropertyKey<String> TITLE_KEY =
            new WritableObjectPropertyKey<>();
    public static final WritableObjectPropertyKey<String> DESCRIPTION_KEY =
            new WritableObjectPropertyKey<>();
    public static final WritableObjectPropertyKey<OnClickListener> ON_CLICK_KEY =
            new WritableObjectPropertyKey<>();

    public static final PropertyKey[] ALL_KEYS = {TITLE_KEY, DESCRIPTION_KEY, ON_CLICK_KEY};
}
