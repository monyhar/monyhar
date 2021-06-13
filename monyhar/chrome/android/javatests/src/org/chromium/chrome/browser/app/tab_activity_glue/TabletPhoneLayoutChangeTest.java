// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.app.tab_activity_glue;

import android.content.res.Configuration;

import androidx.test.filters.MediumTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CallbackHelper;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.chrome.browser.ChromeTabbedActivity;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.lifecycle.RecreateObserver;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.browser.Features.EnableFeatures;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.ui.base.DeviceFormFactor;

import java.util.concurrent.TimeoutException;

/**
 * Test tablet / phone layout change.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
@EnableFeatures({ChromeFeatureList.ANDROID_LAYOUT_CHANGE_TAB_REPARENT})
public class TabletPhoneLayoutChangeTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    @Before
    public void setUp() {
        mActivityTestRule.startMainActivityOnBlankPage();
    }

    @Test
    @MediumTest
    public void testIsRecreatedOnLayoutChange() throws TimeoutException {
        ChromeTabbedActivity cta = mActivityTestRule.getActivity();
        boolean isTestOnTablet = cta.isTablet();
        CallbackHelper helper = new CallbackHelper();
        Configuration config = cta.getSavedConfigurationForTesting();

        // Pretend the device is in another mode.
        config.smallestScreenWidthDp =
                DeviceFormFactor.MINIMUM_TABLET_WIDTH_DP + (isTestOnTablet ? -1 : 1);
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            mActivityTestRule.getActivity().getLifecycleDispatcher().register(
                    (RecreateObserver) helper::notifyCalled);
            Assert.assertTrue(
                    "Activity should be ready for tablet mode change.", cta.didChangeTabletMode());
            cta.getDisplayAndroidObserverForTesting().onCurrentModeChanged(null);
        });

        helper.waitForFirst("Activity should be restart");
        Configuration newConfig = cta.getResources().getConfiguration();
        config = cta.getSavedConfigurationForTesting();
        Assert.assertEquals("Saved config should be updated after recreate.",
                newConfig.smallestScreenWidthDp, config.smallestScreenWidthDp);
    }
}
