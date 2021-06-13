// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.webapps;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.browser.ShortcutHelper;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;

/**
 * Tests for splash screens with EXTRA_BACKGROND_COLOR specified in the Intent.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class WebappSplashScreenBackgroundColorTest {
    @Rule
    public final WebappActivityTestRule mActivityTestRule = new WebappActivityTestRule();

    @Test
    @SmallTest
    @Feature({"Webapps"})
    public void testShowBackgroundColorAndRecordUma() {
        ViewGroup splashScreen = mActivityTestRule.startWebappActivityAndWaitForSplashScreen(
                mActivityTestRule
                        .createIntent()
                        // This is setting Color.GREEN with 50% opacity.
                        .putExtra(ShortcutHelper.EXTRA_BACKGROUND_COLOR, 0x8000FF00L));

        ColorDrawable background = (ColorDrawable) splashScreen.getBackground();

        Assert.assertEquals(Color.GREEN, background.getColor());
    }
}
