// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.crash;

import org.monyhar.base.ContextUtils;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.MainDex;
import org.monyhar.chrome.browser.base.SplitCompatUtils;
import org.monyhar.components.crash.CrashKeys;

/**
 * This UncaughtExceptionHandler will upload the stacktrace when there is an uncaught exception.
 *
 * This happens before native is loaded, and will replace by JavaExceptionReporter after native
 * finishes loading.
 */
@MainDex
public class PureJavaExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Thread.UncaughtExceptionHandler mParent;
    private boolean mHandlingException;
    private static boolean sIsDisabled;

    /** Interface to allow uploading reports. */
    public interface JavaExceptionReporter {
        void createAndUploadReport(Throwable e);
    }

    private PureJavaExceptionHandler(Thread.UncaughtExceptionHandler parent) {
        mParent = parent;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!mHandlingException && !sIsDisabled) {
            mHandlingException = true;
            reportJavaException(e);
        }
        if (mParent != null) {
            mParent.uncaughtException(t, e);
        }
    }

    public static void installHandler() {
        if (!sIsDisabled) {
            Thread.setDefaultUncaughtExceptionHandler(
                    new PureJavaExceptionHandler(Thread.getDefaultUncaughtExceptionHandler()));
        }
    }

    @CalledByNative
    private static void uninstallHandler() {
        // The current handler can be in the middle of an exception handler chain. We do not know
        // about handlers before it. If resetting the uncaught exception handler to mParent, we lost
        // all the handlers before mParent. In order to disable this handler, globally setting a
        // flag to ignore it seems to be the easiest way.
        sIsDisabled = true;
        CrashKeys.getInstance().flushToNative();
    }

    private void reportJavaException(Throwable e) {
        // PureJavaExceptionReporter may be in the chrome module, so load by reflection from there.
        JavaExceptionReporter reporter = (JavaExceptionReporter) SplitCompatUtils.newInstance(
                SplitCompatUtils.createChromeContext(ContextUtils.getApplicationContext()),
                "org.monyhar.chrome.browser.crash.PureJavaExceptionReporter");
        reporter.createAndUploadReport(e);
    }
}
