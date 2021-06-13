// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.util;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;

import org.monyhar.base.PackageManagerUtils;
import org.monyhar.base.ThreadUtils;

import java.util.List;

/**
 * Utilities related to voice recognition.
 */
public class VoiceRecognitionUtil {
    private static Boolean sHasRecognitionIntentHandler;

    /**
     * Determines whether or not the {@link RecognizerIntent#ACTION_RECOGNIZE_SPEECH} {@link Intent}
     * is handled by any {@link android.app.Activity}s in the system.  The result will be cached for
     * future calls.  Passing {@code false} to {@code useCachedValue} will force it to re-query any
     * {@link android.app.Activity}s that can process the {@link Intent}.
     * @param useCachedValue Whether or not to use the cached value from a previous result.
     * @return {@code true} if recognition is supported.  {@code false} otherwise.
     */
    public static boolean isRecognitionIntentPresent(boolean useCachedValue) {
        ThreadUtils.assertOnUiThread();
        if (sHasRecognitionIntentHandler == null || !useCachedValue) {
            List<ResolveInfo> activities = PackageManagerUtils.queryIntentActivities(
                    new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
            sHasRecognitionIntentHandler = !activities.isEmpty();
        }

        return sHasRecognitionIntentHandler;
    }
}
