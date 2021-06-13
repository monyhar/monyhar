// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.test.smoke;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.BaseJUnit4ClassRunner;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.base.test.util.FlakyTest;
import org.monyhar.chrome.test.pagecontroller.rules.ChromeUiApplicationTestRule;
import org.monyhar.chrome.test.pagecontroller.utils.IUi2Locator;
import org.monyhar.chrome.test.pagecontroller.utils.Ui2Locators;

/**
 * Smoke Test for Chrome Android.
 */
@SmallTest
@RunWith(BaseJUnit4ClassRunner.class)
public class ChromeSmokeTest {
    private static final String DATA_URL = "data:,Hello";
    private static final String ACTIVITY_NAME = "org.monyhar.chrome.browser.ChromeTabbedActivity";

    public static final long TIMEOUT_MS = 20000L;
    public static final long UI_CHECK_INTERVAL = 1000L;
    private String mPackageName;

    @Before
    public void setUp() {
        mPackageName = InstrumentationRegistry.getArguments().getString(
                ChromeUiApplicationTestRule.PACKAGE_NAME_ARG, "org.monyhar.chrome");
    }

    @Test
    @FlakyTest(message = "crbug.com/1107896")
    public void testHello() {
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(DATA_URL));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName(mPackageName, ACTIVITY_NAME));
        context.startActivity(intent);

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // TODO (aluo): Check that the data url is loaded after pagecontroller lands.
        IUi2Locator locatorChrome = Ui2Locators.withPackageName(mPackageName);

        CriteriaHelper.pollInstrumentationThread(() -> {
            try {
                return locatorChrome.locateOne(device) != null;
            } catch (NullPointerException e) {
                return false; // Throws an NPE on older Android versions.
            }
        }, mPackageName + " should have loaded", TIMEOUT_MS, UI_CHECK_INTERVAL);
    }
}
