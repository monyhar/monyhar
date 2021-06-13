// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.download;

import android.support.test.InstrumentationRegistry;

import androidx.test.filters.MediumTest;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.PathUtils;
import org.monyhar.base.test.util.CloseableOnMainThread;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Criteria;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.base.test.util.Feature;
import org.monyhar.base.test.util.FlakyTest;
import org.monyhar.chrome.browser.app.ChromeActivity;
import org.monyhar.chrome.browser.download.DownloadTestRule.CustomMainActivityStart;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.permissions.PermissionTestRule;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.net.test.EmbeddedTestServer;
import org.monyhar.ui.modaldialog.ModalDialogManager;
import org.monyhar.ui.modaldialog.ModalDialogManager.ModalDialogType;

import java.util.ArrayList;

/**
 * Test suite for multiple downloads permissions requests.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class AutoDownloadsTest implements CustomMainActivityStart {
    @Rule
    public DownloadTestRule mDownloadTestRule = new DownloadTestRule(this);

    private static final String TEST_FILE =
            "/content/test/data/android/auto_downloads_permissions.html";
    private EmbeddedTestServer mTestServer;

    @Override
    public void customMainActivityStart() throws InterruptedException {
        mTestServer = EmbeddedTestServer.createAndStartServer(InstrumentationRegistry.getContext());

        mDownloadTestRule.startMainActivityOnBlankPage();
    }

    @After
    public void tearDown() {
        mDownloadTestRule.deleteFilesInDownloadDirectory(
                new String[] {"test-image0.png", "test-image1.png"});
    }

    private void waitForDownloadDialog(ModalDialogManager manager) {
        CriteriaHelper.pollUiThread(() -> {
            Criteria.checkThat(manager.isShowing(), Matchers.is(true));
            Criteria.checkThat(manager.getCurrentPresenterForTest(),
                    Matchers.is(manager.getPresenterForTest(ModalDialogType.APP)));
        });
    }

    @Test
    @MediumTest
    @Feature({"AutoDownloads"})
    @FlakyTest(message = "https://crbug.com/1108800")
    public void testAutoDownloadsDialog() throws Exception {
        try (CloseableOnMainThread ignored = CloseableOnMainThread.StrictMode.allowDiskWrites()) {
            ArrayList<DirectoryOption> dirOptions = new ArrayList<>();
            dirOptions.add(new DirectoryOption("Download", PathUtils.getExternalStorageDirectory(),
                    1024000, 1024000, DirectoryOption.DownloadLocationDirectoryType.DEFAULT));
            DownloadDirectoryProvider.getInstance().setDirectoryProviderForTesting(
                    new TestDownloadDirectoryProvider(dirOptions));
        }

        mDownloadTestRule.loadUrl(mTestServer.getURL(TEST_FILE));
        ChromeActivity activity = mDownloadTestRule.getActivity();

        // Wait for "multiple downloads" permission dialog and allow.
        PermissionTestRule.waitForDialog(activity);
        PermissionTestRule.replyToDialog(true, activity);

        int currentCallCount = mDownloadTestRule.getChromeDownloadCallCount();
        Assert.assertTrue(mDownloadTestRule.waitForChromeDownloadToFinish(currentCallCount));
        Assert.assertTrue(mDownloadTestRule.hasDownload("test-image0.png", null));
        Assert.assertTrue(mDownloadTestRule.hasDownload("test-image1.png", null));
    }
}
