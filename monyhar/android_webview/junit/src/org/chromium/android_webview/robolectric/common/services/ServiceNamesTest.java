// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.android_webview.robolectric.common.services;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import org.monyhar.android_webview.common.services.ServiceNames;
import org.monyhar.android_webview.nonembedded.AwComponentUpdateService;
import org.monyhar.android_webview.services.AwMinidumpUploadJobService;
import org.monyhar.android_webview.services.ComponentsProviderService;
import org.monyhar.android_webview.services.CrashReceiverService;
import org.monyhar.android_webview.services.DeveloperModeContentProvider;
import org.monyhar.android_webview.services.DeveloperUiService;
import org.monyhar.android_webview.services.MetricsBridgeService;
import org.monyhar.android_webview.services.VariationsSeedServer;
import org.monyhar.testing.local.LocalRobolectricTestRunner;

/** Tests the constants in ServiceNames. */
@RunWith(LocalRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ServiceNamesTest {
    @Test
    @SmallTest
    public void testServiceNamesValid() {
        Assert.assertEquals("Incorrect class name constant",
                AwMinidumpUploadJobService.class.getName(),
                ServiceNames.AW_MINIDUMP_UPLOAD_JOB_SERVICE);
        Assert.assertEquals("Incorrect class name constant", CrashReceiverService.class.getName(),
                ServiceNames.CRASH_RECEIVER_SERVICE);
        Assert.assertEquals("Incorrect class name constant",
                DeveloperModeContentProvider.class.getName(),
                ServiceNames.DEVELOPER_MODE_CONTENT_PROVIDER);
        Assert.assertEquals("Incorrect class name constant", DeveloperUiService.class.getName(),
                ServiceNames.DEVELOPER_UI_SERVICE);
        Assert.assertEquals("Incorrect class name constant", MetricsBridgeService.class.getName(),
                ServiceNames.METRICS_BRIDGE_SERVICE);
        Assert.assertEquals("Incorrect class name constant", VariationsSeedServer.class.getName(),
                ServiceNames.VARIATIONS_SEED_SERVER);
        Assert.assertEquals("Incorrect class name constant",
                ComponentsProviderService.class.getName(),
                ServiceNames.COMPONENTS_PROVIDER_SERVICE);
        Assert.assertEquals("Incorrect class name constant",
                AwComponentUpdateService.class.getName(), ServiceNames.AW_COMPONENT_UPDATE_SERVICE);
    }
}
