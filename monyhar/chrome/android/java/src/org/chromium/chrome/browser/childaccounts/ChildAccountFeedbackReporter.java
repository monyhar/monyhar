// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.childaccounts;

import android.app.Activity;

import org.monyhar.base.ThreadUtils;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.chrome.browser.AppHooks;
import org.monyhar.chrome.browser.feedback.ChromeFeedbackCollector;
import org.monyhar.chrome.browser.feedback.FeedbackReporter;
import org.monyhar.chrome.browser.feedback.ScreenshotTask;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.ui.base.WindowAndroid;

/**
 * Java implementation of ChildAccountFeedbackReporterAndroid.
 */
public final class ChildAccountFeedbackReporter {
    private static FeedbackReporter sFeedbackReporter;

    public static void reportFeedback(
            Activity activity, String description, String url, Profile profile) {
        ThreadUtils.assertOnUiThread();
        if (sFeedbackReporter == null) {
            sFeedbackReporter = AppHooks.get().createFeedbackReporter();
        }

        new ChromeFeedbackCollector(activity, null /* categoryTag */, description,
                new ScreenshotTask(activity),
                new ChromeFeedbackCollector.InitParams(profile, url, null),
                collector -> { sFeedbackReporter.reportFeedback(collector); });
    }

    @CalledByNative
    public static void reportFeedbackWithWindow(
            WindowAndroid window, String description, String url, Profile profile) {
        reportFeedback(window.getActivity().get(), description, url, profile);
    }

    private ChildAccountFeedbackReporter() {}
}
