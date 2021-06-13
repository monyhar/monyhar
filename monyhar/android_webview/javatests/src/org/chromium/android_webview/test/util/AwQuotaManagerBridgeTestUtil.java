// Copyright 2013 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.android_webview.test.util;

import android.support.test.InstrumentationRegistry;

import org.monyhar.android_webview.AwQuotaManagerBridge;
import org.monyhar.base.test.util.CallbackHelper;

/**
 * This class provides common methods for AwQuotaManagerBridge related tests
 */
public class AwQuotaManagerBridgeTestUtil {
    private static class GetOriginsCallbackHelper extends CallbackHelper {
        private AwQuotaManagerBridge.Origins mOrigins;

        public void notifyCalled(AwQuotaManagerBridge.Origins origins) {
            mOrigins = origins;
            notifyCalled();
        }

        public AwQuotaManagerBridge.Origins getOrigins() {
            assert getCallCount() > 0;
            return mOrigins;
        }
    }

    public static AwQuotaManagerBridge.Origins getOrigins(AwQuotaManagerBridge bridge)
            throws Exception {
        final GetOriginsCallbackHelper callbackHelper = new GetOriginsCallbackHelper();

        int callCount = callbackHelper.getCallCount();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(
                () -> bridge.getOrigins(origins -> callbackHelper.notifyCalled(origins)));
        callbackHelper.waitForCallback(callCount);

        return callbackHelper.getOrigins();
    }
}
