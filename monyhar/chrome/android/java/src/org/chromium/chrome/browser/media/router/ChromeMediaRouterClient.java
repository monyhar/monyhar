// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.media.router;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import org.monyhar.base.ApplicationStatus;
import org.monyhar.base.ContextUtils;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.IntentHandler;
import org.monyhar.chrome.browser.media.ui.ChromeMediaNotificationManager;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.browser.tab.TabUtils;
import org.monyhar.components.browser_ui.media.MediaNotificationInfo;
import org.monyhar.components.media_router.MediaRouterClient;
import org.monyhar.content_public.browser.WebContents;

/** Provides Chrome-specific behavior for Media Router. */
@JNINamespace("media_router")
public class ChromeMediaRouterClient extends MediaRouterClient {
    private ChromeMediaRouterClient() {}

    @Override
    public Context getContextForRemoting() {
        return ContextUtils.getApplicationContext();
    }

    @Override
    public int getTabId(WebContents webContents) {
        Tab tab = TabUtils.fromWebContents(webContents);
        return tab == null ? -1 : tab.getId();
    }

    @Override
    public Intent createBringTabToFrontIntent(int tabId) {
        return IntentHandler.createTrustedBringTabToFrontIntent(
                tabId, IntentHandler.BringToFrontSource.NOTIFICATION);
    }

    @Override
    public void showNotification(MediaNotificationInfo notificationInfo) {
        ChromeMediaNotificationManager.show(notificationInfo);
    }

    @Override
    public int getPresentationNotificationId() {
        return R.id.presentation_notification;
    }

    @Override
    public int getRemotingNotificationId() {
        return R.id.remote_playback_notification;
    }

    @Override
    public FragmentManager getSupportFragmentManager(WebContents initiator) {
        FragmentActivity currentActivity =
                (FragmentActivity) ApplicationStatus.getLastTrackedFocusedActivity();
        return currentActivity == null ? null : currentActivity.getSupportFragmentManager();
    }

    @CalledByNative
    public static void initialize() {
        if (MediaRouterClient.getInstance() != null) return;

        MediaRouterClient.setInstance(new ChromeMediaRouterClient());
    }
}
