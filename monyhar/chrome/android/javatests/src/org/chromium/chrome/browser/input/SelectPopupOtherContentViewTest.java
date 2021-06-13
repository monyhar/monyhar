// Copyright 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.input;

import android.support.test.InstrumentationRegistry;

import androidx.test.filters.LargeTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.base.test.util.Feature;
import org.monyhar.base.test.util.UrlUtils;
import org.monyhar.chrome.browser.WebContentsFactory;
import org.monyhar.chrome.browser.app.ChromeActivity;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.components.embedder_support.view.ContentView;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.content_public.browser.test.util.DOMUtils;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.content_public.browser.test.util.WebContentsUtils;
import org.monyhar.ui.base.ViewAndroidDelegate;

import java.util.concurrent.ExecutionException;

/**
 * Test the select popup and how it interacts with another WebContents.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class SelectPopupOtherContentViewTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    private static final String SELECT_URL = UrlUtils.encodeHtmlDataUri(
            "<html><body>"
            + "Which animal is the strongest:<br/>"
            + "<select id=\"select\">"
            + "<option>Black bear</option>"
            + "<option>Polar bear</option>"
            + "<option>Grizzly</option>"
            + "<option>Tiger</option>"
            + "<option>Lion</option>"
            + "<option>Gorilla</option>"
            + "<option>Chipmunk</option>"
            + "</select>"
            + "</body></html>");

    private boolean isSelectPopupVisibleOnUiThread() {
        try {
            // clang-format off
            return TestThreadUtils.runOnUiThreadBlocking(() ->
                    WebContentsUtils.isSelectPopupVisible(mActivityTestRule.getWebContents()));
            // clang-format on
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tests that the showing select popup does not get closed because an unrelated ContentView
     * gets destroyed.
     *
     */
    @Test
    @LargeTest
    @Feature({"Browser"})
    public void testPopupNotClosedByOtherContentView() throws Exception, Throwable {
        // Load the test page.
        mActivityTestRule.startMainActivityWithURL(SELECT_URL);

        // Once clicked, the popup should show up.
        DOMUtils.clickNode(mActivityTestRule.getWebContents(), "select");
        CriteriaHelper.pollInstrumentationThread(
                this::isSelectPopupVisibleOnUiThread, "The select popup did not show up on click.");

        // Now create and destroy a different WebContents.
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            WebContents webContents = WebContentsFactory.createWebContents(
                    Profile.getLastUsedRegularProfile(), false);
            ChromeActivity activity = mActivityTestRule.getActivity();

            ContentView cv = ContentView.createContentView(
                    activity, null /* eventOffsetHandler */, webContents);
            webContents.initialize("", ViewAndroidDelegate.createBasicDelegate(cv), cv,
                    activity.getWindowAndroid(), WebContents.createDefaultInternalsHolder());
            webContents.destroy();
        });

        // Process some more events to give a chance to the dialog to hide if it were to.
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        // The popup should still be shown.
        Assert.assertTrue("The select popup got hidden by destroying of unrelated ContentViewCore.",
                isSelectPopupVisibleOnUiThread());
    }
}
