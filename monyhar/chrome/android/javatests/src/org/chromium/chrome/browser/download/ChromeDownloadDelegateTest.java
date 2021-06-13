// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.download;

import androidx.test.filters.SmallTest;

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
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.url.GURL;

import java.util.concurrent.Callable;

/**
 * Tests for ChromeDownloadDelegate class.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class ChromeDownloadDelegateTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    @Before
    public void setUp() throws InterruptedException {
        mActivityTestRule.startMainActivityOnBlankPage();
    }

    /**
     * Mock class for test.
     */
    static class MockChromeDownloadDelegate extends ChromeDownloadDelegate {
        public MockChromeDownloadDelegate(Tab tab) {
            super(tab);
        }

        @Override
        protected void onDownloadStartNoStream(DownloadInfo downloadInfo) {
        }
    }

    /**
     * Test to make sure {@link ChromeDownloadDelegate#shouldInterceptContextMenuDownload}
     * returns true only for ".dd" or ".dm" extensions with http/https scheme.
     */
    @Test
    @SmallTest
    @Feature({"Download"})
    public void testShouldInterceptContextMenuDownload() {
        final Tab tab = mActivityTestRule.getActivity().getActivityTab();
        mActivityTestRule.loadUrl("about:blank");
        ChromeDownloadDelegate delegate = TestThreadUtils.runOnUiThreadBlockingNoException(
                (Callable<ChromeDownloadDelegate>) () -> new MockChromeDownloadDelegate(tab));
        Assert.assertFalse(
                delegate.shouldInterceptContextMenuDownload(new GURL("file://test/test.html")));
        Assert.assertFalse(
                delegate.shouldInterceptContextMenuDownload(new GURL("http://test/test.html")));
        Assert.assertFalse(
                delegate.shouldInterceptContextMenuDownload(new GURL("ftp://test/test.dm")));
        Assert.assertFalse(delegate.shouldInterceptContextMenuDownload(new GURL("data://test.dd")));
        Assert.assertFalse(delegate.shouldInterceptContextMenuDownload(new GURL("http://test.dd")));
        Assert.assertFalse(
                delegate.shouldInterceptContextMenuDownload(new GURL("http://test/test.dd")));
        Assert.assertTrue(
                delegate.shouldInterceptContextMenuDownload(new GURL("https://test/test.dm")));
    }
}
