// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.video;

import android.support.test.InstrumentationRegistry;

import androidx.test.filters.LargeTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.DisableIf;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.ChromeTabUtils;
import org.monyhar.chrome.test.util.browser.TabTitleObserver;
import org.monyhar.content_public.browser.test.util.DOMUtils;
import org.monyhar.net.test.EmbeddedTestServer;

import java.util.concurrent.TimeoutException;

/**
 *  Simple tests of html5 video.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class VideoTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    @Test
    @DisableIf.Build(sdk_is_less_than = 19, message = "crbug.com/582067")
    @Feature({"Media", "Media-Video", "Main"})
    @LargeTest
    public void testLoadMediaUrl() throws TimeoutException {
        EmbeddedTestServer testServer =
                EmbeddedTestServer.createAndStartServer(InstrumentationRegistry.getContext());
        try {
            Tab tab = mActivityTestRule.getActivity().getActivityTab();
            TabTitleObserver titleObserver = new TabTitleObserver(tab, "ready_to_play");
            mActivityTestRule.loadUrl(
                    testServer.getURL("/chrome/test/data/android/media/video-play.html"));
            titleObserver.waitForTitleUpdate(5);
            Assert.assertEquals("ready_to_play", ChromeTabUtils.getTitleOnUiThread(tab));

            titleObserver = new TabTitleObserver(tab, "ended");
            DOMUtils.clickNode(tab.getWebContents(), "button1");
            // Now the video will play for 5 secs.
            // Makes sure that the video ends and title was changed.
            titleObserver.waitForTitleUpdate(15);
            Assert.assertEquals("ended", ChromeTabUtils.getTitleOnUiThread(tab));
        } finally {
            testServer.stopAndDestroyServer();
        }
    }

    @Before
    public void setUp() throws InterruptedException {
        mActivityTestRule.startMainActivityOnBlankPage();
    }
}
