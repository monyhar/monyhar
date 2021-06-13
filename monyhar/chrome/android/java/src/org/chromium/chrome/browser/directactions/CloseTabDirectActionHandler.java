// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.directactions;

import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.browser.tabmodel.TabModel;
import org.monyhar.chrome.browser.tabmodel.TabModelSelector;

/**
 * Exposes direct actions that allows closing tabs.
 *
 * <p>This should only be registered in activities where such an action makes sense.
 */
class CloseTabDirectActionHandler extends SimpleDirectActionHandler {
    private final TabModelSelector mTabModelSelector;

    CloseTabDirectActionHandler(TabModelSelector tabModelSelector) {
        super(ChromeDirectActionIds.CLOSE_TAB);

        mTabModelSelector = tabModelSelector;
    }

    @Override
    protected boolean isAvailable() {
        return mTabModelSelector.getCurrentTab() != null;
    }

    @Override
    public void run() {
        Tab tab = mTabModelSelector.getCurrentTab();
        assert tab != null; // isAvailable() guarantees tab is non-null

        TabModel model = mTabModelSelector.getCurrentModel();
        model.closeTab(tab, /* animate= */ true, /* uponExit= */ false, /* canUndo= */ true);
    }
}
