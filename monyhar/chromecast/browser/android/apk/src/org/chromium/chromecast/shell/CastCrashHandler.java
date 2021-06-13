// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chromecast.shell;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;

/**
 * JNI wrapper class for accessing CastCrashHandler.
 */
@JNINamespace("chromecast")
public final class CastCrashHandler {
    private static final String TAG = "CastCrashHandler";

    @CalledByNative
    public static void uploadOnce(String crashDumpPath, String crashReportsPath, String uuid,
            String applicationFeedback, boolean uploadCrashToStaging) {
        CastCrashUploader uploader = CastCrashUploaderFactory.createCastCrashUploader(
                crashDumpPath, crashReportsPath, uuid, applicationFeedback, uploadCrashToStaging);
        uploader.uploadOnce();
    }

    @CalledByNative
    public static void removeCrashDumps(String crashDumpPath, String crashReportsPath, String uuid,
            String applicationFeedback, boolean uploadCrashToStaging) {
        CastCrashUploader uploader = CastCrashUploaderFactory.createCastCrashUploader(
                crashDumpPath, crashReportsPath, uuid, applicationFeedback, uploadCrashToStaging);
        uploader.removeCrashDumps();
    }
}
