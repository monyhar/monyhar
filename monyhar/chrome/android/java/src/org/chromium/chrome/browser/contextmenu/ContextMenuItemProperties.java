// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.contextmenu;

import org.monyhar.ui.modelutil.PropertyKey;
import org.monyhar.ui.modelutil.PropertyModel.WritableIntPropertyKey;
import org.monyhar.ui.modelutil.PropertyModel.WritableObjectPropertyKey;

class ContextMenuItemProperties {
    public static final WritableObjectPropertyKey<CharSequence> TEXT =
            new WritableObjectPropertyKey<>();
    public static final WritableIntPropertyKey MENU_ID = new WritableIntPropertyKey();

    public static final PropertyKey[] ALL_KEYS = {TEXT, MENU_ID};
}
