// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer.test;

import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.test.filters.SmallTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CallbackHelper;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.UrlUtils;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.weblayer.Browser;
import org.monyhar.weblayer.Tab;
import org.monyhar.weblayer.WebLayer;
import org.monyhar.weblayer.shell.InstrumentationActivity;

/**
 * Test for top-controls.
 */
@RunWith(WebLayerJUnit4ClassRunner.class)
@CommandLineFlags.Add("enable-features=ImmediatelyHideBrowserControlsForTest")
public class TopControlsTest {
    @Rule
    public InstrumentationActivityTestRule mActivityTestRule =
            new InstrumentationActivityTestRule();

    private Tab mTab;
    private Browser mBrowser;

    @Test
    @SmallTest
    public void testZeroHeight() throws Exception {
        InstrumentationActivity activity = mActivityTestRule.launchShellWithUrl(null);
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            Fragment fragment = WebLayer.createBrowserFragment(null);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commitNow();
            mBrowser = Browser.fromFragment(fragment);
            mBrowser.setTopView(new FrameLayout(activity));
            mTab = mBrowser.getActiveTab();
        });

        mActivityTestRule.navigateAndWait(mTab, UrlUtils.encodeHtmlDataUri("<html></html>"), true);

        // Calling setSupportsEmbedding() makes sure onTopControlsChanged() will get called, which
        // should not crash.
        CallbackHelper helper = new CallbackHelper();
        TestThreadUtils.runOnUiThreadBlocking(
                () -> { mBrowser.setSupportsEmbedding(true, (result) -> helper.notifyCalled()); });
        helper.waitForCallback(0);
    }
}
