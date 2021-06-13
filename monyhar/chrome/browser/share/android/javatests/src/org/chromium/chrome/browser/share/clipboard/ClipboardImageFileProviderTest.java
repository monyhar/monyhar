// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.share.clipboard;

import android.graphics.Bitmap;
import android.os.Looper;

import androidx.test.filters.SmallTest;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.ContentUriUtils;
import org.monyhar.base.task.AsyncTask;
import org.monyhar.base.test.util.CallbackHelper;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Criteria;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.chrome.browser.FileProviderHelper;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.ui.base.Clipboard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tests of {@link ClipboardImageFileProvider}.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class ClipboardImageFileProviderTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();

    private static final long WAIT_TIMEOUT_SECONDS = 30L;
    private static final String TEST_PNG_IMAGE_FILE_EXTENSION = ".png";

    private byte[] mTestImageData;

    private class AsyncTaskRunnableHelper extends CallbackHelper implements Runnable {
        @Override
        public void run() {
            notifyCalled();
        }
    }

    private void waitForAsync() throws TimeoutException {
        try {
            AsyncTaskRunnableHelper runnableHelper = new AsyncTaskRunnableHelper();
            AsyncTask.SERIAL_EXECUTOR.execute(runnableHelper);
            runnableHelper.waitForCallback(0, 1, WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
        }
    }

    @Before
    public void setUp() {
        Looper.prepare();
        // Clear the clipboard.
        Clipboard.getInstance().setText("");

        Bitmap bitmap =
                Bitmap.createBitmap(/* width = */ 10, /* height = */ 10, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, /*quality = (0-100) */ 100, baos);
        mTestImageData = baos.toByteArray();

        mActivityTestRule.startMainActivityFromLauncher();
        ContentUriUtils.setFileProviderUtil(new FileProviderHelper());
    }

    @After
    public void tearDown() throws TimeoutException {
        // Clear the clipboard.
        Clipboard.getInstance().setText("");
    }

    @Test
    @SmallTest
    public void testClipboardSetImage() throws TimeoutException, IOException {
        Clipboard.getInstance().setImageFileProvider(new ClipboardImageFileProvider());
        Clipboard.getInstance().setImage(mTestImageData, TEST_PNG_IMAGE_FILE_EXTENSION);

        CriteriaHelper.pollUiThread(() -> {
            Criteria.checkThat(Clipboard.getInstance().getImageUri(), Matchers.notNullValue());
            Criteria.checkThat(Clipboard.getInstance().getImageUriIfSharedByThisApp(),
                    Matchers.is(Clipboard.getInstance().getImageUri()));
        });

        // Make sure Clipboard::getImage is call on non UI thread.
        AsyncTask.SERIAL_EXECUTOR.execute(
                () -> { Assert.assertNotNull(Clipboard.getInstance().getImage()); });

        // Wait for the above check to complete.
        waitForAsync();
    }
}
