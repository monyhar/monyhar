// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.history;

import org.monyhar.base.test.util.CallbackHelper;
import org.monyhar.chrome.browser.history.HistoryProvider.BrowsingHistoryObserver;

import java.util.List;

/**
 * Testing class used to store history requests for further examination.
 * For now only stores one request at a time but is easily extensible.
 */
public class TestBrowsingHistoryObserver implements BrowsingHistoryObserver {
    private List<HistoryItem> mHistory;
    private CallbackHelper mQueryCallback;

    public TestBrowsingHistoryObserver() {
        mQueryCallback = new CallbackHelper();
    }

    @Override
    public void onQueryHistoryComplete(List<HistoryItem> items, boolean hasMorePotentialMatches) {
        mHistory = items;
        mQueryCallback.notifyCalled();
    }

    @Override
    public void onHistoryDeleted() {}

    @Override
    public void hasOtherFormsOfBrowsingData(boolean hasOtherForms) {}

    /**
     * Simple accessor to the internal CallbackHelper.
     *
     * @return The {@link CallbackHelper} notified when a query is completed.
     */
    public CallbackHelper getQueryCallback() {
        return mQueryCallback;
    }

    /**
     * Used to retrieve the results for the latest history query made.
     *
     * @return A list of {@link HistoryItem} ordered from latest to oldest.
     */
    public List<HistoryItem> getHistoryQueryResults() {
        return mHistory;
    }
}
