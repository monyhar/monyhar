// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.read_later;

import org.monyhar.base.ContextUtils;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.bookmarks.BookmarkUtils;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.components.bookmarks.BookmarkId;
import org.monyhar.components.bookmarks.BookmarkType;

/**
 * Contains JNI methods to needed by read later feature.
 */
public final class ReadingListBridge {
    private ReadingListBridge() {}

    /**
     * Called when Chrome starts in foreground.
     */
    public static void onStartChromeForeground() {
        ReadingListBridgeJni.get().onStartChromeForeground();
    }

    @CalledByNative
    private static String getNotificationTitle() {
        return ContextUtils.getApplicationContext().getResources().getString(
                R.string.reading_list_reminder_notification_title);
    }

    @CalledByNative
    private static String getNotificationText(int unreadSize) {
        return ContextUtils.getApplicationContext().getResources().getQuantityString(
                R.plurals.reading_list_reminder_notification_subtitle, unreadSize, unreadSize);
    }

    @CalledByNative
    private static void openReadingListPage() {
        if (!ChromeFeatureList.isEnabled(ChromeFeatureList.READ_LATER)) return;
        BookmarkUtils.showBookmarkManager(
                null, new BookmarkId(0, BookmarkType.READING_LIST), /*isIncognito=*/false);
    }

    @NativeMethods
    interface Natives {
        void onStartChromeForeground();
    }
}
