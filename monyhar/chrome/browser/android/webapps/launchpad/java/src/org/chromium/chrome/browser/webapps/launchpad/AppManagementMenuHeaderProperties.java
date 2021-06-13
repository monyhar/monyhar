// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.webapps.launchpad;

import android.graphics.Bitmap;

import org.monyhar.ui.modelutil.PropertyKey;
import org.monyhar.ui.modelutil.PropertyModel;
import org.monyhar.ui.modelutil.PropertyModel.WritableObjectPropertyKey;

/** Contains all the properties for app management menu header. */
class AppManagementMenuHeaderProperties {
    private AppManagementMenuHeaderProperties() {}

    public static final WritableObjectPropertyKey<String> TITLE = new WritableObjectPropertyKey<>();
    public static final WritableObjectPropertyKey<CharSequence> URL =
            new WritableObjectPropertyKey<>();
    public static final WritableObjectPropertyKey<Bitmap> ICON = new WritableObjectPropertyKey<>();

    public static final PropertyKey[] ALL_KEYS = {TITLE, URL, ICON};

    /** Create the {@link PropertyModel} for menu header. */
    public static PropertyModel buildHeader(LaunchpadItem item) {
        return new PropertyModel.Builder(ALL_KEYS)
                .with(TITLE, item.name)
                .with(URL, item.url)
                .with(ICON, item.icon)
                .build();
    }
}
