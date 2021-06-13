// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.ntp;

import android.support.test.InstrumentationRegistry;

import androidx.test.filters.LargeTest;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.ChromeTabUtils;
import org.monyhar.components.embedder_support.util.UrlConstants;
import org.monyhar.net.test.EmbeddedTestServer;

/**
 * Tests loading the NTP and navigating between it and other pages.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class NewTabPageNavigationTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    private EmbeddedTestServer mTestServer;

    @Before
    public void setUp() {
        mActivityTestRule.startMainActivityWithURL(null);
        mTestServer = EmbeddedTestServer.createAndStartServer(InstrumentationRegistry.getContext());
    }

    @After
    public void tearDown() {
        mTestServer.stopAndDestroyServer();
    }

    /**
     * Sanity check that we do start on the NTP by default.
     */
    @Test
    @MediumTest
    @Feature({"NewTabPage", "Main"})
    public void testNTPIsDefault() {
        Tab tab = mActivityTestRule.getActivity().getActivityTab();
        Assert.assertNotNull(tab);
        String url = ChromeTabUtils.getUrlStringOnUiThread(tab);
        Assert.assertTrue("Unexpected url: " + url,
                url.startsWith("chrome-native://newtab/")
                        || url.startsWith("chrome-native://bookmarks/")
                        || url.startsWith("chrome-native://recent-tabs/"));
    }

    /**
     * Check that navigating away from the NTP does work.
     */
    @Test
    @LargeTest
    @Feature({"NewTabPage"})
    public void testNavigatingFromNTP() {
        String url = mTestServer.getURL("/chrome/test/data/android/google.html");
        mActivityTestRule.loadUrl(url);
        Assert.assertEquals(url,
                ChromeTabUtils.getUrlStringOnUiThread(
                        mActivityTestRule.getActivity().getActivityTab()));
    }

    /**
     * Tests navigating back to the NTP after loading another page.
     */
    @Test
    @MediumTest
    @Feature({"NewTabPage"})
    public void testNavigateBackToNTPViaUrl() {
        String url = mTestServer.getURL("/chrome/test/data/android/google.html");
        mActivityTestRule.loadUrl(url);
        Assert.assertEquals(url,
                ChromeTabUtils.getUrlStringOnUiThread(
                        mActivityTestRule.getActivity().getActivityTab()));

        mActivityTestRule.loadUrl(UrlConstants.NTP_URL);
        Tab tab = mActivityTestRule.getActivity().getActivityTab();
        Assert.assertNotNull(tab);
        url = ChromeTabUtils.getUrlStringOnUiThread(tab);
        Assert.assertEquals(UrlConstants.NTP_URL, url);

        // Check that the NTP is actually displayed.
        Assert.assertNotNull(tab.getNativePage() instanceof NewTabPage);
    }
}
