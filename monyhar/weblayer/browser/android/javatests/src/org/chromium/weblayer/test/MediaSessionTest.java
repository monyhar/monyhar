// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer.test;

import static org.monyhar.weblayer.R.id.weblayer_media_session_notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.service.notification.StatusBarNotification;

import androidx.test.filters.MediumTest;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Criteria;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.base.test.util.MinAndroidSdkLevel;
import org.monyhar.weblayer.shell.InstrumentationActivity;

/**
 * Tests that MediaSession works as expected.
 */
@CommandLineFlags.Add({"ignore-certificate-errors"})
@RunWith(WebLayerJUnit4ClassRunner.class)
public final class MediaSessionTest {
    @Rule
    public InstrumentationActivityTestRule mActivityTestRule =
            new InstrumentationActivityTestRule();

    private InstrumentationActivity mActivity;

    @Test
    @MediumTest
    @MinAndroidSdkLevel(Build.VERSION_CODES.M)
    public void basic() throws Throwable {
        mActivity = mActivityTestRule.launchShellWithUrl(
                mActivityTestRule.getTestDataURL("media_session.html"));
        EventUtils.simulateTouchCenterOfView(mActivity.getWindow().getDecorView());

        CriteriaHelper.pollInstrumentationThread(() -> {
            Criteria.checkThat(getMediaSessionNotification(), Matchers.notNullValue());
        });
    }

    /**
     * Retrieves the active media session notification, or null if none exists.
     * {@link NotificationManager#getActiveNotifications()} is only available from M.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private Notification getMediaSessionNotification() {
        StatusBarNotification notifications[] =
                ((NotificationManager) mActivity.getApplicationContext().getSystemService(
                         Context.NOTIFICATION_SERVICE))
                        .getActiveNotifications();
        for (StatusBarNotification statusBarNotification : notifications) {
            if (statusBarNotification.getId() == weblayer_media_session_notification) {
                return statusBarNotification.getNotification();
            }
        }
        return null;
    }
}
