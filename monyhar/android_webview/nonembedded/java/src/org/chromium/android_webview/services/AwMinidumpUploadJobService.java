// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.android_webview.services;

import android.os.PersistableBundle;

import org.monyhar.components.minidump_uploader.MinidumpUploadJob;
import org.monyhar.components.minidump_uploader.MinidumpUploadJobImpl;
import org.monyhar.components.minidump_uploader.MinidumpUploadJobService;

/**
 * Class that interacts with the Android JobScheduler to upload Minidumps at appropriate times.
 */
// OBS: This class needs to be public to be started from android.app.ActivityThread.
public class AwMinidumpUploadJobService extends MinidumpUploadJobService {
    @Override
    protected MinidumpUploadJob createMinidumpUploadJob(PersistableBundle unusedExtras) {
        return new MinidumpUploadJobImpl(new AwMinidumpUploaderDelegate());
    }
}
