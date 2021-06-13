// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.android_browsertests_apk;

import android.content.Intent;
import android.net.Uri;

import org.monyhar.build.gtest_apk.NativeTestInstrumentationTestRunner;
import org.monyhar.content_public.common.ContentUrlConstants;

/**
 * An Instrumentation for android_browsertests that includes chrome:blank in the intent.
 */
public class ChromeBrowserTestsInstrumentationTestRunner
        extends NativeTestInstrumentationTestRunner {
    @Override
    protected Intent createShardMainIntent() {
        Intent i = super.createShardMainIntent();
        i.setData(Uri.parse(ContentUrlConstants.ABOUT_BLANK_DISPLAY_URL));
        return i;
    }
}
