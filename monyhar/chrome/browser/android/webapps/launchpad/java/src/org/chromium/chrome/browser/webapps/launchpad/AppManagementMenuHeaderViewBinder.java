// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
package org.monyhar.chrome.browser.webapps.launchpad;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.monyhar.ui.modelutil.PropertyKey;
import org.monyhar.ui.modelutil.PropertyModel;
import org.monyhar.ui.modelutil.PropertyModelChangeProcessor.ViewBinder;

/**
 *  A {@link ViewBinder} responsible for gluing {@link AppManagementMenuHeaderProperties} to the
 *  view.
 */
class AppManagementMenuHeaderViewBinder implements ViewBinder<PropertyModel, View, PropertyKey> {
    @Override
    public void bind(PropertyModel model, View view, PropertyKey propertyKey) {
        if (propertyKey == AppManagementMenuHeaderProperties.TITLE) {
            TextView titleText = view.findViewById(R.id.menu_header_title);
            titleText.setText(model.get(AppManagementMenuHeaderProperties.TITLE));
        } else if (propertyKey == AppManagementMenuHeaderProperties.URL) {
            TextView urlText = view.findViewById(R.id.menu_header_url);
            urlText.setText(model.get(AppManagementMenuHeaderProperties.URL));
        } else if (propertyKey == AppManagementMenuHeaderProperties.ICON) {
            Bitmap bitmap = model.get(AppManagementMenuHeaderProperties.ICON);
            if (bitmap != null) {
                ImageView imageView = view.findViewById(R.id.menu_header_image);
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
