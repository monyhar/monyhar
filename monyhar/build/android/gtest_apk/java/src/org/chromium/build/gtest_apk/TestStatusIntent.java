// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.build.gtest_apk;

/**
 * Intent action and extras of broadcasts intercepted by TestStatusReceiver.
 */
public class TestStatusIntent {
    public static final String ACTION_TEST_RUN_STARTED =
            "org.monyhar.test.reporter.TestStatusReporter.TEST_RUN_STARTED";
    public static final String ACTION_TEST_RUN_FINISHED =
            "org.monyhar.test.reporter.TestStatusReporter.TEST_RUN_FINISHED";
    public static final String ACTION_UNCAUGHT_EXCEPTION =
            "org.monyhar.test.reporter.TestStatusReporter.UNCAUGHT_EXCEPTION";
    public static final String DATA_TYPE_RESULT = "org.monyhar.test.reporter/result";
    public static final String EXTRA_PID = "org.monyhar.test.reporter.TestStatusReporter.PID";
    public static final String EXTRA_STACK_TRACE =
            "org.monyhar.test.reporter.TestStatusReporter.STACK_TRACE";
}
