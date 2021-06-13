// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.directactions;
import static org.junit.Assert.assertThat;

import android.annotation.TargetApi;
import android.os.Build;

import androidx.test.filters.MediumTest;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.base.test.util.MinAndroidSdkLevel;
import org.monyhar.chrome.browser.app.ChromeActivity;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.webapps.WebappActivityTestRule;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.util.browser.Features.DisableFeatures;

/**
 * Tests the availability of core direct actions in different activities.
 *
 * <p>This tests both {@link DirectActionInitializer} and its integration with {@link
 * ChromeActivity} and its different subclasses.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
@DisableFeatures(ChromeFeatureList.AUTOFILL_ASSISTANT_DIRECT_ACTIONS)
@MinAndroidSdkLevel(Build.VERSION_CODES.N)
@TargetApi(24) // For java.util.function.Consumer.
public class DirectActionAvailabilityWebappTest {
    @Rule
    public WebappActivityTestRule mWebAppActivityTestRule = new WebappActivityTestRule();

    @Rule
    public DirectActionTestRule mDirectActionRule = new DirectActionTestRule();

    @Test
    @MediumTest
    @Feature({"DirectActions"})
    public void testCoreDirectActionInWebappActivity() throws Exception {
        mWebAppActivityTestRule.startWebappActivity();

        assertThat(DirectActionTestUtils.setupActivityAndGetDirectAction(mWebAppActivityTestRule),
                Matchers.containsInAnyOrder("go_back", "reload", "go_forward", "find_in_page"));
    }
}
