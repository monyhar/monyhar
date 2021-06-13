// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.download.home.empty;

import org.monyhar.ui.modelutil.PropertyKey;
import org.monyhar.ui.modelutil.PropertyModel;
import org.monyhar.ui.modelutil.PropertyModelChangeProcessor.ViewBinder;

/**
 * A helper {@link ViewBinder} responsible for gluing {@link EmptyProperties} to
 * {@link EmptyView}.
 */
class EmptyViewBinder implements ViewBinder<PropertyModel, EmptyView, PropertyKey> {
    @Override
    public void bind(PropertyModel model, EmptyView view, PropertyKey propertyKey) {
        if (propertyKey == EmptyProperties.STATE) {
            view.setState(model.get(EmptyProperties.STATE));
        } else if (propertyKey == EmptyProperties.EMPTY_TEXT_RES_ID) {
            view.setEmptyText(model.get(EmptyProperties.EMPTY_TEXT_RES_ID));
        }
    }
}