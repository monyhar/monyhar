// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.send_tab_to_self;

import org.monyhar.chrome.browser.ChromeAccessorActivity;
import org.monyhar.chrome.browser.share.send_tab_to_self.SendTabToSelfAndroidBridge;
import org.monyhar.chrome.browser.tab.Tab;

/**
 * A simple activity that allows Chrome to expose send tab to self as an option in the share menu.
 */
public class SendTabToSelfShareActivity extends ChromeAccessorActivity {
    public static final String BROADCAST_ACTION = "SendTabToSelfShareActivityBroadcastAction";

    @Override
    protected String getBroadcastAction() {
        return BROADCAST_ACTION;
    }

    public static boolean featureIsAvailable(Tab currentTab) {
        return SendTabToSelfAndroidBridge.isFeatureAvailable(currentTab.getWebContents());
    }
}
