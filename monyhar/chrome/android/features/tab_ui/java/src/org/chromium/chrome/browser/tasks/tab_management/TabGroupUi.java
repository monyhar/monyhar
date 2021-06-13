// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.tasks.tab_management;

import org.monyhar.base.supplier.Supplier;
import org.monyhar.chrome.browser.toolbar.bottom.BottomControlsContentDelegate;

/**
 * Interface for the Tab Groups related UI.
 */
public interface TabGroupUi extends BottomControlsContentDelegate {
    /**
     * @return {@link Supplier} that provides dialog visibility.
     */
    Supplier<Boolean> getTabGridDialogVisibilitySupplier();
}
