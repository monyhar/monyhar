// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.download.items;

import org.monyhar.base.ThreadUtils;
import org.monyhar.chrome.browser.download.DownloadManagerService;
import org.monyhar.chrome.browser.download.DownloadNotifier;
import org.monyhar.chrome.browser.offlinepages.downloads.OfflinePageDownloadBridge;
import org.monyhar.components.offline_items_collection.OfflineContentProvider;

/**
 * A factory meant to hold a singleton bridge between the notification UI and an
 * {@link OfflineContentProvider}.
 */
public class OfflineContentAggregatorNotificationBridgeUiFactory {
    private static OfflineContentAggregatorNotificationBridgeUi sBridgeUi;

    /**
     * @return An {@link OfflineContentAggregatorNotificationBridgeUi} instance singleton.  If one
     *         is not available this will create a new one.
     */
    public static OfflineContentAggregatorNotificationBridgeUi instance() {
        ThreadUtils.assertOnUiThread();
        if (sBridgeUi == null) {
            OfflineContentProvider provider = OfflineContentAggregatorFactory.get();
            DownloadNotifier ui =
                    DownloadManagerService.getDownloadManagerService().getDownloadNotifier();
            OfflinePageDownloadBridge.getInstance();
            sBridgeUi = new OfflineContentAggregatorNotificationBridgeUi(provider, ui);
        }

        return sBridgeUi;
    }

    /**
     * Destroys the internal singleton for {@link OfflineContentAggregatorNotificationBridgeUi} if
     * one exists.
     */
    public static void destroy() {
        ThreadUtils.assertOnUiThread();
        if (sBridgeUi == null) return;

        sBridgeUi.destroy();
        sBridgeUi = null;
    }

    private OfflineContentAggregatorNotificationBridgeUiFactory() {}
}
