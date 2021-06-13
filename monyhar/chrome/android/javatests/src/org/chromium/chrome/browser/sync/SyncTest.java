// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.sync;

import androidx.test.filters.LargeTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.base.test.util.DisabledTest;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.signin.services.IdentityServicesProvider;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.util.browser.Features;
import org.monyhar.chrome.test.util.browser.sync.SyncTestUtil;
import org.monyhar.components.signin.base.CoreAccountInfo;

/**
 * Test suite for Sync.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class SyncTest {
    @Rule
    public SyncTestRule mSyncTestRule = new SyncTestRule();
    @Rule
    public TestRule mProcessorRule = new Features.JUnitProcessor();

    private static final String TAG = "SyncTest";

    @DisabledTest(message = "https://crbug.com/1197554")
    @Test
    @LargeTest
    @Feature({"Sync"})
    public void testSignInAndOut() {
        CoreAccountInfo accountInfo = mSyncTestRule.setUpAccountAndEnableSyncForTesting();

        // Signing out should disable sync.
        mSyncTestRule.signOut();
        Assert.assertFalse(SyncTestUtil.isSyncRequested());

        // Signing back in should re-enable sync.
        mSyncTestRule.signinAndEnableSync(accountInfo);
        Assert.assertTrue("Sync should be re-enabled.", SyncTestUtil.isSyncFeatureActive());
    }

    @Test
    @LargeTest
    @Feature({"Sync"})
    public void testStopAndClear() {
        mSyncTestRule.setUpAccountAndEnableSyncForTesting();
        CriteriaHelper.pollUiThread(
                ()
                        -> IdentityServicesProvider.get()
                                   .getIdentityManager(Profile.getLastUsedRegularProfile())
                                   .hasPrimaryAccount(),
                "Timed out checking that hasPrimaryAccount() == true", SyncTestUtil.TIMEOUT_MS,
                SyncTestUtil.INTERVAL_MS);

        mSyncTestRule.clearServerData();

        // Clearing server data should turn off sync and sign out of chrome.
        Assert.assertNull(mSyncTestRule.getCurrentSignedInAccount());
        Assert.assertFalse(SyncTestUtil.isSyncRequested());
        CriteriaHelper.pollUiThread(
                ()
                        -> !IdentityServicesProvider.get()
                                    .getIdentityManager(Profile.getLastUsedRegularProfile())
                                    .hasPrimaryAccount(),
                "Timed out checking that hasPrimaryAccount() == false", SyncTestUtil.TIMEOUT_MS,
                SyncTestUtil.INTERVAL_MS);
    }

    @Test
    @LargeTest
    @Feature({"Sync"})
    public void testStopAndStartSync() {
        CoreAccountInfo accountInfo = mSyncTestRule.setUpAccountAndEnableSyncForTesting();

        mSyncTestRule.stopSync();
        Assert.assertEquals(accountInfo, mSyncTestRule.getCurrentSignedInAccount());
        Assert.assertFalse(SyncTestUtil.isSyncRequested());

        mSyncTestRule.startSyncAndWait();
    }
}
