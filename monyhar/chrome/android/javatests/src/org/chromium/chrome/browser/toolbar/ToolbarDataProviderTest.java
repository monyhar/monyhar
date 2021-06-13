// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.toolbar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.monyhar.base.test.util.Batch.PER_CLASS;

import androidx.test.filters.MediumTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.Batch;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Restriction;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.toolbar.top.ToolbarPhone;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.batch.BlankCTATabInitialStateRule;
import org.monyhar.chrome.test.util.browser.Features;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.ui.test.util.UiRestriction;

/**
 * Instrumentation tests for {@link ToolbarDataProvider}.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
@Restriction(UiRestriction.RESTRICTION_TYPE_PHONE)
@Batch(PER_CLASS)
public class ToolbarDataProviderTest {
    @Rule
    public TestRule mProcessor = new Features.InstrumentationProcessor();

    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    @Rule
    public BlankCTATabInitialStateRule mBlankCTATabInitialStateRule =
            new BlankCTATabInitialStateRule(mActivityTestRule, false);

    @Test
    @MediumTest
    public void testPrimaryOTRProfileUsedForIncognitoTabbedActivity() {
        mActivityTestRule.loadUrlInNewTab("about:blank", /*incognito=*/true);
        ToolbarPhone toolbar = mActivityTestRule.getActivity().findViewById(R.id.toolbar);
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            Profile profile = toolbar.getToolbarDataProvider().getProfile();
            assertTrue(profile.isPrimaryOTRProfile());
        });
    }

    @Test
    @MediumTest
    public void testRegularProfileUsedForRegularTabbedActivity() {
        mActivityTestRule.loadUrlInNewTab("about:blank", /*incognito=*/false);
        ToolbarPhone toolbar = mActivityTestRule.getActivity().findViewById(R.id.toolbar);
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            Profile profile = toolbar.getToolbarDataProvider().getProfile();
            assertFalse(profile.isOffTheRecord());
        });
    }
}
