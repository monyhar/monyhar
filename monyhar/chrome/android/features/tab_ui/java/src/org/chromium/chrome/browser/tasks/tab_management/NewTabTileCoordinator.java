// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.tasks.tab_management;

import static org.monyhar.chrome.browser.tasks.tab_management.TabListModel.CardProperties.CARD_TYPE;
import static org.monyhar.chrome.browser.tasks.tab_management.TabListModel.CardProperties.ModelType.NEW_TAB_TILE;

import org.monyhar.chrome.browser.tabmodel.TabCreatorManager;
import org.monyhar.chrome.browser.tabmodel.TabModelSelector;
import org.monyhar.ui.modelutil.PropertyModel;

/**
 * This is the coordinator for NewTabTile component.
 */
public class NewTabTileCoordinator {
    private final PropertyModel mModel;
    private final NewTabTileMediator mMediator;

    NewTabTileCoordinator(TabModelSelector tabModelSelector, TabCreatorManager tabCreatorManager) {
        mModel = new PropertyModel.Builder(NewTabTileViewProperties.ALL_KEYS)
                         .with(CARD_TYPE, NEW_TAB_TILE)
                         .build();
        mMediator = new NewTabTileMediator(mModel, tabModelSelector, tabCreatorManager);
    }

    public PropertyModel getModel() {
        return mModel;
    }

    public void destroy() {
        mMediator.destroy();
    }
}
