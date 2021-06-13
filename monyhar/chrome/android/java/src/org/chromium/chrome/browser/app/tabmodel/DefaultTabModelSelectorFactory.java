// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.app.tabmodel;

import android.app.Activity;

import org.monyhar.base.annotations.VerifiesOnN;
import org.monyhar.chrome.browser.flags.ActivityType;
import org.monyhar.chrome.browser.tabmodel.AsyncTabParamsManager;
import org.monyhar.chrome.browser.tabmodel.NextTabPolicy.NextTabPolicySupplier;
import org.monyhar.chrome.browser.tabmodel.TabCreatorManager;
import org.monyhar.chrome.browser.tabmodel.TabModelFilterFactory;
import org.monyhar.chrome.browser.tabmodel.TabModelSelector;
import org.monyhar.chrome.browser.tabmodel.TabModelSelectorFactory;
import org.monyhar.chrome.browser.tabmodel.TabModelSelectorImpl;

/**
 * Default {@link TabModelSelectorFactory} for Chrome.
 */
public class DefaultTabModelSelectorFactory implements TabModelSelectorFactory {
    // Do not inline since this uses some APIs only available on Android N versions, which cause
    // verification errors.
    @VerifiesOnN
    @Override
    public TabModelSelector buildSelector(Activity activity, TabCreatorManager tabCreatorManager,
            NextTabPolicySupplier nextTabPolicySupplier, int selectorIndex) {
        TabModelFilterFactory tabModelFilterFactory = new ChromeTabModelFilterFactory();
        AsyncTabParamsManager asyncTabParamsManager = AsyncTabParamsManagerSingleton.getInstance();

        return new TabModelSelectorImpl(/*windowAndroidSupplier=*/null, tabCreatorManager,
                tabModelFilterFactory, nextTabPolicySupplier, asyncTabParamsManager, true,
                ActivityType.TABBED, false);
    }
}
