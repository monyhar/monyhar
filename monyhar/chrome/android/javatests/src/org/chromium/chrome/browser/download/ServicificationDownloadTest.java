// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.download;

import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;

import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.base.test.util.Feature;
import org.monyhar.base.test.util.TestFileUtil;
import org.monyhar.chrome.browser.download.items.OfflineContentAggregatorFactory;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.profiles.OTRProfileID;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ReducedModeNativeTestRule;
import org.monyhar.chrome.test.util.browser.Features.DisableFeatures;
import org.monyhar.chrome.test.util.browser.Features.EnableFeatures;
import org.monyhar.components.offline_items_collection.ContentId;
import org.monyhar.components.offline_items_collection.OfflineContentProvider;
import org.monyhar.components.offline_items_collection.OfflineItem;
import org.monyhar.components.offline_items_collection.OfflineItemState;
import org.monyhar.components.offline_items_collection.UpdateDelta;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.net.test.EmbeddedTestServerRule;

import java.util.List;

/**
 * Tests interrupted download can be resumed with minimal browser mode.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
public final class ServicificationDownloadTest {
    @Rule
    public EmbeddedTestServerRule mEmbeddedTestServerRule = new EmbeddedTestServerRule();
    @Rule
    public ReducedModeNativeTestRule mNativeTestRule = new ReducedModeNativeTestRule();

    private static final String TEST_DOWNLOAD_FILE = "/chrome/test/data/android/download/test.gzip";
    private static final String DOWNLOAD_GUID = "F7FB1F59-7DE1-4845-AFDB-8A688F70F583";
    private MockDownloadNotificationService mNotificationService;
    private DownloadUpdateObserver mDownloadUpdateObserver;

    private static class MockDownloadNotificationService extends DownloadNotificationService {
        private boolean mDownloadCompleted;

        @Override
        public int notifyDownloadSuccessful(ContentId id, String filePath, String fileName,
                long systemDownloadId, OTRProfileID otrProfileID, boolean isSupportedMimeType,
                boolean isOpenable, Bitmap icon, String originalUrl, boolean shouldPromoteOrigin,
                String referrer, long totalBytes) {
            mDownloadCompleted = true;
            return 0;
        }

        public void waitForDownloadCompletion() {
            CriteriaHelper.pollUiThread(
                    () -> mDownloadCompleted, "Failed waiting for the download to complete.");
        }
    }

    private static class DownloadUpdateObserver implements OfflineContentProvider.Observer {
        private boolean mDownloadCompleted;

        @Override
        public void onItemsAdded(List<OfflineItem> items) {}

        @Override
        public void onItemRemoved(ContentId id) {}

        @Override
        public void onItemUpdated(OfflineItem item, UpdateDelta updateDelta) {
            mDownloadCompleted = item.state == OfflineItemState.COMPLETE;
        }

        public void waitForDownloadCompletion() {
            CriteriaHelper.pollUiThread(
                    () -> mDownloadCompleted, "Failed waiting for the download to complete.");
        }
    }

    @Before
    public void setUp() {
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            mNotificationService = new MockDownloadNotificationService();
            mDownloadUpdateObserver = new DownloadUpdateObserver();
        });
    }

    private static boolean useDownloadOfflineContentProvider() {
        return ChromeFeatureList.isEnabled(ChromeFeatureList.DOWNLOAD_OFFLINE_CONTENT_PROVIDER);
    }

    @Test
    @LargeTest
    @Feature({"Download"})
    @DisableFeatures(ChromeFeatureList.DOWNLOAD_OFFLINE_CONTENT_PROVIDER)
    public void testResumeInterruptedDownload() {
        if (useDownloadOfflineContentProvider()) return;
        mNativeTestRule.assertMinimalBrowserStarted();

        String tempFile = InstrumentationRegistry.getInstrumentation()
                                  .getTargetContext()
                                  .getCacheDir()
                                  .getPath()
                + "/test.gzip";
        TestFileUtil.deleteFile(tempFile);
        DownloadItem item = new DownloadItem(false,
                new DownloadInfo.Builder()
                        .setDownloadGuid(DOWNLOAD_GUID)
                        .setOTRProfileId(null)
                        .build());
        final String url = mEmbeddedTestServerRule.getServer().getURL(TEST_DOWNLOAD_FILE);
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            DownloadManagerService downloadManagerService =
                    DownloadManagerService.getDownloadManagerService();
            downloadManagerService.disableAddCompletedDownloadToDownloadManager();
            ((SystemDownloadNotifier) downloadManagerService.getDownloadNotifier())
                    .setDownloadNotificationService(mNotificationService);
            downloadManagerService.createInterruptedDownloadForTest(url, DOWNLOAD_GUID, tempFile);
            downloadManagerService.resumeDownload(
                    new ContentId("download", DOWNLOAD_GUID), item, true);
        });
        mNotificationService.waitForDownloadCompletion();
    }

    @Test
    @LargeTest
    @Feature({"Download"})
    @EnableFeatures(ChromeFeatureList.DOWNLOAD_OFFLINE_CONTENT_PROVIDER)
    public void testResumeInterruptedDownloadUsingDownloadOfflineContentProvider() {
        if (!useDownloadOfflineContentProvider()) return;
        mNativeTestRule.assertMinimalBrowserStarted();

        String tempFile = InstrumentationRegistry.getInstrumentation()
                                  .getTargetContext()
                                  .getCacheDir()
                                  .getPath()
                + "/test.gzip";
        TestFileUtil.deleteFile(tempFile);
        final String url = mEmbeddedTestServerRule.getServer().getURL(TEST_DOWNLOAD_FILE);
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            DownloadManagerService downloadManagerService =
                    DownloadManagerService.getDownloadManagerService();
            downloadManagerService.disableAddCompletedDownloadToDownloadManager();
            ((SystemDownloadNotifier) downloadManagerService.getDownloadNotifier())
                    .setDownloadNotificationService(mNotificationService);
            downloadManagerService.createInterruptedDownloadForTest(url, DOWNLOAD_GUID, tempFile);
            OfflineContentAggregatorFactory.get().addObserver(mDownloadUpdateObserver);
            OfflineContentAggregatorFactory.get().resumeDownload(
                    new ContentId("LEGACY_DOWNLOAD", DOWNLOAD_GUID), true);
        });
        mDownloadUpdateObserver.waitForDownloadCompletion();
    }
}
