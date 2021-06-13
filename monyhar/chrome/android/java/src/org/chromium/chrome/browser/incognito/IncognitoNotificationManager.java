// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.incognito;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import org.monyhar.base.ContextUtils;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.notifications.NotificationConstants;
import org.monyhar.chrome.browser.notifications.NotificationUmaTracker;
import org.monyhar.chrome.browser.notifications.NotificationWrapperBuilderFactory;
import org.monyhar.chrome.browser.notifications.channels.ChromeChannelDefinitions;
import org.monyhar.components.browser_ui.notifications.NotificationManagerProxy;
import org.monyhar.components.browser_ui.notifications.NotificationManagerProxyImpl;
import org.monyhar.components.browser_ui.notifications.NotificationMetadata;
import org.monyhar.components.browser_ui.notifications.NotificationWrapper;
import org.monyhar.components.browser_ui.notifications.NotificationWrapperBuilder;

/**
 * Manages the notification indicating that there are incognito tabs opened in Document mode.
 */
public class IncognitoNotificationManager {
    public static final String INCOGNITO_TABS_OPEN_TAG = "incognito_tabs_open";
    private static final int INCOGNITO_TABS_OPEN_ID = 100;

    /**
     * Shows the close all incognito notification.
     */
    public static void showIncognitoNotification() {
        Context context = ContextUtils.getApplicationContext();
        String actionMessage =
                context.getResources().getString(R.string.close_all_incognito_notification);

        // From Android N, notification by default has the app name and title should not be the same
        // as app name.
        String title = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? context.getResources().getString(R.string.close_all_incognito_notification_title)
                : context.getResources().getString(R.string.app_name);

        NotificationWrapperBuilder builder =
                NotificationWrapperBuilderFactory
                        .createNotificationWrapperBuilder(true /* preferCompat */,
                                ChromeChannelDefinitions.ChannelId.INCOGNITO,
                                null /* remoteAppPackageName */,
                                new NotificationMetadata(
                                        NotificationUmaTracker.SystemNotificationType
                                                .CLOSE_INCOGNITO,
                                        INCOGNITO_TABS_OPEN_TAG, INCOGNITO_TABS_OPEN_ID))
                        .setContentTitle(title)
                        .setContentIntent(
                                IncognitoNotificationServiceImpl.getRemoveAllIncognitoTabsIntent(
                                        context))
                        .setContentText(actionMessage)
                        .setOngoing(true)
                        .setVisibility(Notification.VISIBILITY_SECRET)
                        .setSmallIcon(R.drawable.incognito_simple)
                        .setShowWhen(false)
                        .setLocalOnly(true)
                        .setGroup(NotificationConstants.GROUP_INCOGNITO);
        NotificationManagerProxy nm = new NotificationManagerProxyImpl(context);
        NotificationWrapper notification = builder.buildNotificationWrapper();
        nm.notify(notification);
        NotificationUmaTracker.getInstance().onNotificationShown(
                NotificationUmaTracker.SystemNotificationType.CLOSE_INCOGNITO,
                notification.getNotification());
    }

    /**
     * Dismisses the incognito notification.
     */
    public static void dismissIncognitoNotification() {
        Context context = ContextUtils.getApplicationContext();
        NotificationManager nm =
                  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(INCOGNITO_TABS_OPEN_TAG, INCOGNITO_TABS_OPEN_ID);
    }
}
