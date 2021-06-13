// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.download.home;

import org.monyhar.base.Callback;
import org.monyhar.chrome.browser.profiles.OTRProfileID;
import org.monyhar.components.offline_items_collection.ContentId;
import org.monyhar.components.offline_items_collection.OfflineContentProvider;
import org.monyhar.components.offline_items_collection.OfflineItem;
import org.monyhar.components.offline_items_collection.OfflineItemSchedule;
import org.monyhar.components.offline_items_collection.ShareCallback;
import org.monyhar.components.offline_items_collection.VisualsCallback;

import java.util.ArrayList;

/**
 * An interface to use to talk to the legacy download provider path.  This is meant to be used until
 * the old download path can be removed and we no longer need this bridge/conversion route for
 * download home.
 */
public interface LegacyDownloadProvider {
    /** Adds {@code observer} to get simulated OfflineContentProvider updates. */
    void addObserver(OfflineContentProvider.Observer observer);

    /** Removes {@code observer} from getting simulated OfflineContentProvider updates. */
    void removeObserver(OfflineContentProvider.Observer observer);

    /** To be called when this class can be torn down and will no longer be used. */
    void destroy();

    // OfflineContentProvider (glue) implementation.
    /** @see OfflineContentProvider#openItem(ContentId) */
    void openItem(OfflineItem item);

    /** @see OfflineContentProvider#removeItem(ContentId) */
    void removeItem(OfflineItem item);

    /** @see OfflineContentProvider#cancelDownload(ContentId) */
    void cancelDownload(OfflineItem item);

    /** @see OfflineContentProvider#pauseDownload(ContentId) */
    void pauseDownload(OfflineItem item);

    /** @see OfflineContentProvider#resumeDownload(ContentId, boolean) */
    void resumeDownload(OfflineItem item, boolean hasUserGesture);

    /** @see OfflineContentProvider#getItemById(ContentId, Callback) */
    void getItemById(ContentId id, Callback<OfflineItem> callback);

    /** @see OfflineContentProvider#getAllItems(Callback) */
    void getAllItems(Callback<ArrayList<OfflineItem>> callback, OTRProfileID otrProfileID);

    /** @see OfflineContentProvider#getVisualsForItem(ContentId, VisualsCallback) */
    void getVisualsForItem(ContentId id, VisualsCallback callback);

    /** @see OfflineContentProvider#getShareInfoForItem(ContentId, ShareCallback) */
    void getShareInfoForItem(OfflineItem item, ShareCallback callback);

    /** @see OfflineContentProvider#renameItem(ContentId, String, Callback)*/
    void renameItem(OfflineItem item, String name, Callback</*RenameResult*/ Integer> callback);

    /** @see OfflineContentProvider#changeSchedule(ContentId, OfflineItemSchedule) */
    void changeSchedule(final OfflineItem item, final OfflineItemSchedule schedule);
}
