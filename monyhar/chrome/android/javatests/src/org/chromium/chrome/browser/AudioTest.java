// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser;

import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.FlakyTest;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.browser.TabTitleObserver;
import org.monyhar.content_public.browser.test.util.DOMUtils;
import org.monyhar.net.test.EmbeddedTestServer;

import java.util.concurrent.TimeoutException;

/**
 * Simple HTML5 audio tests.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class AudioTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    private EmbeddedTestServer mTestServer;

    @Before
    public void setUp() throws Exception {
        mActivityTestRule.startMainActivityOnBlankPage();
        mTestServer = EmbeddedTestServer.createAndStartServer(InstrumentationRegistry.getContext());
    }

    @After
    public void tearDown() {
        mTestServer.stopAndDestroyServer();
    }

    /**
     * Test playing a small mp3 audio file.
     * @Feature({"Media", "Media-Audio", "Main"})
     * @MediumTest
     */
    // TODO(jbudorick): Attempt to reenable this after the server switch has stabilized.
    @Test
    @FlakyTest(message = "crbug.com/331122")
    public void testPlayMp3() throws TimeoutException {
        Tab tab = mActivityTestRule.getActivity().getActivityTab();
        TabTitleObserver titleObserver = new TabTitleObserver(tab, "ready_to_play");
        mActivityTestRule.loadUrl(
                mTestServer.getURL("/chrome/test/data/android/media/audio-play.html"));
        titleObserver.waitForTitleUpdate(5);
        Assert.assertEquals("ready_to_play", tab.getTitle());

        titleObserver = new TabTitleObserver(tab, "ended");
        DOMUtils.clickNode(tab.getWebContents(), "button1");

        // Make sure that the audio playback "ended" and title is changed.
        titleObserver.waitForTitleUpdate(15);
        Assert.assertEquals("ended", tab.getTitle());
    }
}
