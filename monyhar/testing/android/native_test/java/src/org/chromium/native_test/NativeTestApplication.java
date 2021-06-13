// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.native_test;

import android.app.Application;
import android.content.Context;

import org.monyhar.base.CommandLine;
import org.monyhar.base.multidex.ChromiumMultiDexInstaller;
import org.monyhar.build.BuildConfig;

/**
 * Application class to be used by native_test apks.
 */
public class NativeTestApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        assert getBaseContext() != null;
        if (BuildConfig.IS_MULTIDEX_ENABLED) {
            ChromiumMultiDexInstaller.install(this);
        }
        CommandLine.init(new String[] {});

        // This is required for Mockito to initialize mocks without running under Instrumentation.
        System.setProperty("org.mockito.android.target", getCacheDir().getPath());
    }
}
