// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.contextmenu;

import static org.monyhar.chrome.browser.contextmenu.ContextMenuItemProperties.TEXT;

import android.view.View;
import android.widget.TextView;

import org.monyhar.ui.modelutil.PropertyKey;
import org.monyhar.ui.modelutil.PropertyModel;

class ContextMenuItemViewBinder {
    public static void bind(PropertyModel model, View view, PropertyKey propertyKey) {
        if (propertyKey == TEXT) {
            ((TextView) view).setText(model.get(TEXT));
        }
    }
}
