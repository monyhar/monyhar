// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer.test;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CallbackHelper;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.weblayer.FaviconCallback;
import org.monyhar.weblayer.shell.InstrumentationActivity;

/** Tests for FaviconFetcher. */
@RunWith(WebLayerJUnit4ClassRunner.class)
public class FaviconFetcherTest {
    @Rule
    public InstrumentationActivityTestRule mActivityTestRule =
            new InstrumentationActivityTestRule();

    private InstrumentationActivity mActivity;

    @Test
    @SmallTest
    public void testFaviconFetcher() throws Exception {
        mActivity = mActivityTestRule.launchShellWithUrl("about:blank");

        final CallbackHelper callbackHelper = new CallbackHelper();
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            mActivity.getTab().createFaviconFetcher(new FaviconCallback() {
                @Override
                public void onFaviconChanged(Bitmap bitmap) {
                    if (bitmap != null) {
                        Assert.assertTrue(bitmap.getWidth() > 0);
                        Assert.assertTrue(bitmap.getHeight() > 0);
                        callbackHelper.notifyCalled();
                    }
                }
            });
        });
        String url = mActivityTestRule.getTestDataURL("simple_page_with_favicon.html");
        mActivityTestRule.navigateAndWait(url);
        callbackHelper.waitForFirst();

        // Verify the favicon can get obtained from the Profile.
        final CallbackHelper downloadCallbackHelper = new CallbackHelper();
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            mActivity.getBrowser().getProfile().getCachedFaviconForPageUri(
                    Uri.parse(url), (Bitmap bitmap) -> {
                        Assert.assertTrue(bitmap != null);
                        Assert.assertTrue(bitmap.getWidth() > 0);
                        Assert.assertTrue(bitmap.getHeight() > 0);
                        downloadCallbackHelper.notifyCalled();
                    });
        });
        downloadCallbackHelper.waitForFirst();
    }
}
