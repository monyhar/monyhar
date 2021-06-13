// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.app.reengagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.monyhar.base.StrictModeContext;
import org.monyhar.chrome.browser.IntentHandler;
import org.monyhar.chrome.browser.reengagement.ReengagementNotificationController;

/** Trampoline activity to start the NTP from the reengagement notification. */
public class ReengagementActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String action = getIntent().getAction();
        if (ReengagementNotificationController.LAUNCH_NTP_ACTION.equals(action)) {
            Intent intent =
                    IntentHandler.createTrustedOpenNewTabIntent(this, /* incognito = */ false);
            try (StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
                startActivity(intent);
            }
        }
        finish();
    }
}
