// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.app.tabmodel;

import org.monyhar.chrome.browser.dependency_injection.ActivityScope;
import org.monyhar.chrome.browser.tabmodel.EmptyTabModelFilter;
import org.monyhar.chrome.browser.tabmodel.TabModel;
import org.monyhar.chrome.browser.tabmodel.TabModelFilter;
import org.monyhar.chrome.browser.tabmodel.TabModelFilterFactory;
import org.monyhar.chrome.browser.tasks.tab_management.TabManagementDelegate;
import org.monyhar.chrome.browser.tasks.tab_management.TabManagementModuleProvider;
import org.monyhar.chrome.browser.tasks.tab_management.TabUiFeatureUtilities;

import javax.inject.Inject;

/**
 * Glue code that decides which concrete {@link TabModelFilterFactory} should be used.
 */
@ActivityScope
public class ChromeTabModelFilterFactory implements TabModelFilterFactory {
    @Inject
    public ChromeTabModelFilterFactory() {}

    /**
     * Return a {@link TabModelFilter} based on feature flags. This can return either:
     * - A filter that implements tab groups.
     * - A canonical {@link EmptyTabModelFilter}.
     *
     * @param model The {@link TabModel} that the {@link TabModelFilter} acts on.
     * @return a {@link TabModelFilter}.
     */
    @Override
    public TabModelFilter createTabModelFilter(TabModel model) {
        if (TabUiFeatureUtilities.isTabGroupsAndroidEnabled()) {
            TabManagementDelegate tabManagementDelegate = TabManagementModuleProvider.getDelegate();
            if (tabManagementDelegate != null) {
                return tabManagementDelegate.createTabGroupModelFilter(model);
            }
        }
        return new EmptyTabModelFilter(model);
    }
}
