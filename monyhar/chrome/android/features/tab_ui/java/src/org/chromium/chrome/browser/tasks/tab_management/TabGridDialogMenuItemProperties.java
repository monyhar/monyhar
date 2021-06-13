// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.tasks.tab_management;

import org.monyhar.ui.modelutil.PropertyKey;
import org.monyhar.ui.modelutil.PropertyModel.WritableIntPropertyKey;
import org.monyhar.ui.modelutil.PropertyModel.WritableObjectPropertyKey;

/**
 * List of properties to designate information about a menu item in tab grid dialog menu.
 */
public class TabGridDialogMenuItemProperties {
    public static final WritableObjectPropertyKey<String> TITLE = new WritableObjectPropertyKey<>();
    public static final WritableIntPropertyKey MENU_ID = new WritableIntPropertyKey();

    public static final PropertyKey[] ALL_KEYS = {TITLE, MENU_ID};
}
