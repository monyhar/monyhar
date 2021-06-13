// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer_private.interfaces;

import android.os.Bundle;

import org.monyhar.weblayer_private.interfaces.IObjectWrapper;

interface ICrashReporterControllerClient {
    void onPendingCrashReports(in String[] localIds) = 0;
    void onCrashDeleted(in String localId) = 1;
    void onCrashUploadSucceeded(in String localId, in String reportId) = 2;
    void onCrashUploadFailed(in String localId, in String failureMessage) = 3;
}
