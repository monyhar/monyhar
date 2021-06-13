// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.tasks;

import org.monyhar.ui.modelutil.PropertyKey;
import org.monyhar.ui.modelutil.PropertyModel;

/** View Properties related to displaying a most visited list. */
final class MostVisitedListProperties {
    private MostVisitedListProperties() {}

    public static final PropertyModel.WritableBooleanPropertyKey IS_VISIBLE =
            new PropertyModel.WritableBooleanPropertyKey();

    public static final PropertyKey[] ALL_KEYS = new PropertyKey[] {IS_VISIBLE};
}
