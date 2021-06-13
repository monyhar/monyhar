// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer.test;

import org.monyhar.base.test.util.CallbackHelper;
import org.monyhar.weblayer.Tab;
import org.monyhar.weblayer.TabListCallback;

/**
 * TabListCallback test helper. Primarily used to wait for a tab to be closed.
 */
public class OnTabRemovedTabListCallbackImpl extends TabListCallback {
    private final CallbackHelper mCallbackHelper = new CallbackHelper();

    @Override
    public void onTabRemoved(Tab tab) {
        mCallbackHelper.notifyCalled();
    }

    public void waitForCloseTab() {
        try {
            // waitForFirst() only handles a single call. If you need more convert from
            // waitForFirst().
            mCallbackHelper.waitForFirst();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasClosed() {
        return mCallbackHelper.getCallCount() > 0;
    }
}
