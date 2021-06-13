// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.media.ui;

import android.content.Intent;
import android.media.AudioManager;
import android.support.test.InstrumentationRegistry;

import androidx.test.filters.MediumTest;
import androidx.test.filters.SmallTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.base.test.util.FlakyTest;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.ntp.NewTabPage;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.NewTabPageTestUtils;
import org.monyhar.chrome.test.util.browser.TabLoadObserver;
import org.monyhar.components.browser_ui.media.MediaNotificationController;
import org.monyhar.components.browser_ui.media.MediaNotificationManager;
import org.monyhar.components.embedder_support.util.UrlConstants;
import org.monyhar.components.url_formatter.SchemeDisplay;
import org.monyhar.components.url_formatter.UrlFormatter;
import org.monyhar.content_public.browser.test.util.DOMUtils;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.media.MediaSwitches;
import org.monyhar.net.test.EmbeddedTestServer;

import java.util.concurrent.TimeoutException;

/**
 * Tests for checking whether the media are paused when unplugging the headset
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({MediaSwitches.AUTOPLAY_NO_GESTURE_REQUIRED_POLICY,
        ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class MediaSessionTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    private static final String TEST_PATH = "/content/test/data/media/session/media-session.html";
    private static final String VIDEO_ID = "long-video";

    private EmbeddedTestServer mTestServer;

    @Test
    @SmallTest
    @FlakyTest
    public void testPauseOnHeadsetUnplug() throws IllegalArgumentException, TimeoutException {
        mActivityTestRule.startMainActivityWithURL(mTestServer.getURL(TEST_PATH));
        Tab tab = mActivityTestRule.getActivity().getActivityTab();

        Assert.assertTrue(DOMUtils.isMediaPaused(tab.getWebContents(), VIDEO_ID));
        DOMUtils.playMedia(tab.getWebContents(), VIDEO_ID);
        DOMUtils.waitForMediaPlay(tab.getWebContents(), VIDEO_ID);
        waitForNotificationReady();

        simulateHeadsetUnplug();
        DOMUtils.waitForMediaPauseBeforeEnd(tab.getWebContents(), VIDEO_ID);
    }

    /**
     * Regression test for crbug.com/1108038.
     *
     * Makes sure the notification info is updated after a navigation from a native page to a site
     * with media.
     */
    @Test
    @MediumTest
    public void mediaSessionUrlUpdatedAfterNativePageNavigation() throws Exception {
        mActivityTestRule.startMainActivityWithURL("about:blank");

        Tab tab = mActivityTestRule.getActivity().getActivityTab();
        mActivityTestRule.loadUrl(UrlConstants.NTP_URL);
        NewTabPageTestUtils.waitForNtpLoaded(tab);

        Assert.assertTrue(tab.getNativePage() instanceof NewTabPage);

        String videoPageUrl = mTestServer.getURL(TEST_PATH);
        new TabLoadObserver(tab).fullyLoadUrl(videoPageUrl);

        DOMUtils.playMedia(tab.getWebContents(), VIDEO_ID);
        DOMUtils.waitForMediaPlay(tab.getWebContents(), VIDEO_ID);
        waitForNotificationReady();

        TestThreadUtils.runOnUiThreadBlocking(() -> {
            MediaNotificationController controller =
                    MediaNotificationManager.getController(R.id.media_playback_notification);
            Assert.assertEquals(UrlFormatter.formatUrlForSecurityDisplay(
                                        videoPageUrl, SchemeDisplay.OMIT_HTTP_AND_HTTPS),
                    controller.mMediaNotificationInfo.origin);
        });
    }

    @Before
    public void setUp() {
        mTestServer = EmbeddedTestServer.createAndStartServer(InstrumentationRegistry.getContext());
    }

    @After
    public void tearDown() {
        mTestServer.stopAndDestroyServer();
    }

    private void waitForNotificationReady() {
        CriteriaHelper.pollInstrumentationThread(() -> {
            return MediaNotificationManager.getController(R.id.media_playback_notification) != null;
        });
    }

    private void simulateHeadsetUnplug() {
        Intent i = new Intent(InstrumentationRegistry.getTargetContext(),
                ChromeMediaNotificationControllerServices.PlaybackListenerService.class);
        i.setAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

        InstrumentationRegistry.getContext().startService(i);
    }
}
