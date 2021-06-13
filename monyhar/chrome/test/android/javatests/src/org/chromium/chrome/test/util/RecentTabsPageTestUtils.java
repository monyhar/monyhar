// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.test.util;

import org.hamcrest.Matchers;
import org.junit.Assert;

import org.monyhar.base.test.util.Criteria;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.chrome.browser.ntp.RecentTabsPage;
import org.monyhar.chrome.browser.tab.Tab;

/**
 * Utilities for testing the RecentTabsPage.
 */
public class RecentTabsPageTestUtils {
    public static void waitForRecentTabsPageLoaded(final Tab tab) {
        CriteriaHelper.pollUiThread(() -> {
            Criteria.checkThat("RecentTabsPage never fully loaded", tab.getNativePage(),
                    Matchers.instanceOf(RecentTabsPage.class));
        });
        Assert.assertTrue(tab.getNativePage() instanceof RecentTabsPage);
    }
}
