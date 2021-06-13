// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.permissions;

import androidx.test.filters.MediumTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.permissions.PermissionTestRule.PermissionUpdateWaiter;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;

/**
 * Test suite for MIDI permissions requests.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class MIDITest {
    @Rule
    public PermissionTestRule mPermissionRule = new PermissionTestRule(true /* useHttpsServer */);

    private static final String TEST_FILE = "/content/test/data/android/midi_permissions.html";

    @Before
    public void setUp() throws Exception {
        mPermissionRule.setUpActivity();
    }

    @Test
    @MediumTest
    @Feature({"MIDI"})
    public void testMIDIDialog() throws Exception {
        Tab tab = mPermissionRule.getActivity().getActivityTab();
        PermissionUpdateWaiter updateWaiter =
                new PermissionUpdateWaiter("pass", mPermissionRule.getActivity());
        tab.addObserver(updateWaiter);
        mPermissionRule.runAllowTest(updateWaiter, TEST_FILE, "", 0, false, true);
        tab.removeObserver(updateWaiter);
    }
}
