// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content.browser.remoteobjects;

import android.os.Process;
import android.util.EventLog;

final class RemoteObjectAuditorImpl implements RemoteObjectImpl.Auditor {
    /**
     * Event which should be logged if getClass is invoked.
     * See frameworks/base/core/java/android/webkit/EventLogTags.logtags.
     */
    private static final int sObjectGetClassInvocationAttemptLogTag = 70151;

    @Override
    public void onObjectGetClassInvocationAttempt() {
        EventLog.writeEvent(sObjectGetClassInvocationAttemptLogTag, Process.myUid());
    }
}
