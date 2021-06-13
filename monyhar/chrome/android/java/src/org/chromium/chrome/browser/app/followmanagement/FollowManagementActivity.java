// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.app.followmanagement;

import android.os.Bundle;

import org.monyhar.base.Log;
import org.monyhar.chrome.browser.SnackbarActivity;
import org.monyhar.chrome.browser.feed.followmanagement.FollowManagementCoordinator;

/**
 * Activity for managing feed and webfeed settings on the new tab page.
 */
public class FollowManagementActivity extends SnackbarActivity {
    private static final String TAG = "FollowMActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "FollowManagementActivity.onCreate");

        FollowManagementCoordinator coordinator = new FollowManagementCoordinator(this);
        setContentView(coordinator.getView());
    }
}
