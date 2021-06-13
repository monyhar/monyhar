// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.omaha.notification;

import static org.monyhar.chrome.browser.omaha.UpdateStatusProvider.UpdateState.INLINE_UPDATE_AVAILABLE;
import static org.monyhar.chrome.browser.omaha.UpdateStatusProvider.UpdateState.UPDATE_AVAILABLE;

import android.content.Context;
import android.content.Intent;

import org.monyhar.base.Callback;
import org.monyhar.chrome.browser.IntentHandler;
import org.monyhar.chrome.browser.document.ChromeLauncherActivity;
import org.monyhar.chrome.browser.omaha.UpdateStatusProvider;
import org.monyhar.chrome.browser.omaha.UpdateStatusProvider.UpdateState;

/**
 * Util functions for update notification implementation.
 */
public class UpdateUtils {
    private static final String INLINE_UPDATE_NOTIFICATION_RECEIVED_EXTRA =
            "org.monyhar.chrome.browser.omaha.inline_update_notification_received_extra";

    /**
     * Addresses different states for update notification.
     * @param context A {@link Context} Context passing into receiver.
     * @param state A {@link UpdateState} state of update notification.
     */
    public static void onUpdateAvailable(Context context, @UpdateState int state) {
        switch (state) {
            case INLINE_UPDATE_AVAILABLE:
                Intent launchInlineUpdateIntent =
                        new Intent(Intent.ACTION_VIEW)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .setClass(context, ChromeLauncherActivity.class)
                                .putExtra(INLINE_UPDATE_NOTIFICATION_RECEIVED_EXTRA, true);
                IntentHandler.startActivityForTrustedIntent(launchInlineUpdateIntent);
                break;
            case UPDATE_AVAILABLE:
                Callback<UpdateStatusProvider.UpdateStatus> intentLauncher =
                        new Callback<UpdateStatusProvider.UpdateStatus>() {
                            @Override
                            public void onResult(UpdateStatusProvider.UpdateStatus result) {
                                UpdateStatusProvider.getInstance().startIntentUpdate(context,
                                        UpdateStatusProvider.UpdateInteractionSource
                                                .FROM_NOTIFICATION,
                                        true /* newTask */);
                                UpdateStatusProvider.getInstance().removeObserver(this);
                            }
                        };
                UpdateStatusProvider.getInstance().addObserver(intentLauncher);
                break;
            default:
                break;
        }
    }
}
