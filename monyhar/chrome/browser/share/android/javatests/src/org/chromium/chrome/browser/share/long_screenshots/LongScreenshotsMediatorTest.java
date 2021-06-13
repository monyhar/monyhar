// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.share.long_screenshots;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Looper;
import android.view.View;

import androidx.test.filters.MediumTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.monyhar.base.test.BaseActivityTestRule;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.share.long_screenshots.bitmap_generation.EntryManager;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.util.browser.Features;
import org.monyhar.ui.test.util.DummyUiActivity;

/** Tests for the LongScreenshotsMediator. */
@RunWith(ChromeJUnit4ClassRunner.class)
@Features.EnableFeatures(ChromeFeatureList.CHROME_SHARE_LONG_SCREENSHOT)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class LongScreenshotsMediatorTest {
    private Activity mActivity;
    private Bitmap mBitmap;
    private LongScreenshotsMediator mMediator;

    @Rule
    public BaseActivityTestRule<DummyUiActivity> mActivityTestRule =
            new BaseActivityTestRule<>(DummyUiActivity.class);

    @Mock
    private View mView;

    @Mock
    private EntryManager mManager;

    @Before
    public void setUp() {
        Looper.prepare();

        mActivityTestRule.launchActivity(null);
        mActivity = mActivityTestRule.getActivity();

        MockitoAnnotations.initMocks(this);

        mBitmap = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888);

        // Instantiate the object under test.
        mMediator = new LongScreenshotsMediator(mActivity, mManager);
    }

    @Test
    @MediumTest
    public void testShowAreaSelectionDone() {
        mMediator.showAreaSelectionDialog(mBitmap);
        Assert.assertTrue(mMediator.getDialog().isShowing());
    }

    @Test
    @MediumTest
    public void testAreaSelectionDone() {
        mMediator.showAreaSelectionDialog(mBitmap);
        Assert.assertTrue(mMediator.getDialog().isShowing());

        mMediator.areaSelectionDone(mView);
        Assert.assertFalse(mMediator.getDialog().isShowing());
    }

    @Test
    @MediumTest
    public void testAreaSelectionClose() {
        mMediator.showAreaSelectionDialog(mBitmap);
        Assert.assertTrue(mMediator.getDialog().isShowing());

        mMediator.areaSelectionClose(mView);
        Assert.assertFalse(mMediator.getDialog().isShowing());
    }
}
