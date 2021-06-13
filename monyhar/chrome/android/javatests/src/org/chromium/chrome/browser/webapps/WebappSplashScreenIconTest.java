// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.webapps;

import android.graphics.drawable.BitmapDrawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.ShortcutHelper;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;

/**
 * Tests for splash screens with an icon registered in WebappRegistry.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class WebappSplashScreenIconTest {
    @Rule
    public final WebappActivityTestRule mActivityTestRule = new WebappActivityTestRule();

    private ViewGroup mSplashScreen;

    @Before
    public void setUp() {
        WebappRegistry.getInstance()
                .getWebappDataStorage(WebappActivityTestRule.WEBAPP_ID)
                .updateSplashScreenImage(WebappActivityTestRule.TEST_SPLASH_ICON);
        mSplashScreen = mActivityTestRule.startWebappActivityAndWaitForSplashScreen(
                mActivityTestRule.createIntent().putExtra(
                        ShortcutHelper.EXTRA_ICON, WebappActivityTestRule.TEST_ICON));
    }

    @Test
    @SmallTest
    @Feature({"Webapps"})
    public void testShowSplashIcon() {
        ImageView splashImage =
                (ImageView) mSplashScreen.findViewById(R.id.webapp_splash_screen_icon);
        BitmapDrawable drawable = (BitmapDrawable) splashImage.getDrawable();

        Assert.assertEquals(512, drawable.getBitmap().getWidth());
        Assert.assertEquals(512, drawable.getBitmap().getHeight());
    }
}
