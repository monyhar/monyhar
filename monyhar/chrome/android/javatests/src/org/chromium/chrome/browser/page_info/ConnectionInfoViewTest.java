// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.page_info;

import androidx.test.filters.MediumTest;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.Batch;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.browser.app.ChromeActivity;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.vr.VrModuleProvider;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.batch.BlankCTATabInitialStateRule;
import org.monyhar.components.page_info.ConnectionInfoView;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;

/**
 * Tests for ConnectionInfoView.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
@Batch(ConnectionInfoViewTest.PAGE_INFO_BATCH_NAME)
public class ConnectionInfoViewTest {
    public static final String PAGE_INFO_BATCH_NAME = "page_info";

    @ClassRule
    public static final ChromeTabbedActivityTestRule sActivityTestRule =
            new ChromeTabbedActivityTestRule();

    @Rule
    public final BlankCTATabInitialStateRule mInitialStateRule =
            new BlankCTATabInitialStateRule(sActivityTestRule, false);

    /**
     * Tests that ConnectionInfoView can be instantiated and shown.
     */
    @Test
    @MediumTest
    @Feature({"ConnectionInfoView"})
    public void testShow() throws InterruptedException {
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            ChromeActivity context = sActivityTestRule.getActivity();
            WebContents webContents = context.getActivityTab().getWebContents();
            ConnectionInfoView.show(context, webContents, context.getModalDialogManager(),
                    VrModuleProvider.getDelegate());
        });
    }
}
