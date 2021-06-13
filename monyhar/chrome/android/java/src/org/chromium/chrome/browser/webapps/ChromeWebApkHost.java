// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.webapps;

import static org.monyhar.chrome.browser.flags.ChromeSwitches.SKIP_WEBAPK_VERIFICATION;

import org.monyhar.base.ApplicationState;
import org.monyhar.base.ApplicationStatus;
import org.monyhar.base.CommandLine;
import org.monyhar.base.ContextUtils;
import org.monyhar.base.ThreadUtils;
import org.monyhar.base.task.PostTask;
import org.monyhar.components.webapk.lib.client.ChromeWebApkHostSignature;
import org.monyhar.components.webapk.lib.client.WebApkValidator;
import org.monyhar.content_public.browser.UiThreadTaskTraits;
import org.monyhar.webapk.lib.client.WebApkIdentityServiceClient;

/**
 * Contains functionality needed for Chrome to host WebAPKs.
 */
public class ChromeWebApkHost {
    /** Time in milliseconds to wait for {@link WebApkServiceClient} to finish. */
    private static final long WAIT_FOR_WORK_DISCONNECT_SERVICE_DELAY_MS = 1000;

    private static ApplicationStatus.ApplicationStateListener sListener;

    public static void init() {
        WebApkValidator.init(ChromeWebApkHostSignature.EXPECTED_SIGNATURE,
                ChromeWebApkHostSignature.PUBLIC_KEY);
        if (CommandLine.getInstance().hasSwitch(SKIP_WEBAPK_VERIFICATION)) {
            // Tell the WebApkValidator to work for all WebAPKs.
            WebApkValidator.setDisableValidationForTesting(true);
        }
    }

    /**
     * Checks whether Chrome is the runtime host of the WebAPK asynchronously. Accesses the
     * ApplicationStateListener needs to be called on UI thread.
     */
    public static void checkChromeBacksWebApkAsync(String webApkPackageName,
            WebApkIdentityServiceClient.CheckBrowserBacksWebApkCallback callback) {
        ThreadUtils.assertOnUiThread();

        if (sListener == null) {
            // Registers an application listener to disconnect all connections to WebAPKs
            // when Chrome is stopped.
            sListener = new ApplicationStatus.ApplicationStateListener() {
                @Override
                public void onApplicationStateChange(int newState) {
                    if (newState == ApplicationState.HAS_STOPPED_ACTIVITIES
                            || newState == ApplicationState.HAS_DESTROYED_ACTIVITIES) {
                        disconnectFromAllServices(false /* waitForPendingWork */);

                        ApplicationStatus.unregisterApplicationStateListener(sListener);
                        sListener = null;
                    }
                }
            };
            ApplicationStatus.registerApplicationStateListener(sListener);
        }

        WebApkIdentityServiceClient.getInstance(UiThreadTaskTraits.DEFAULT)
                .checkBrowserBacksWebApkAsync(
                        ContextUtils.getApplicationContext(), webApkPackageName, callback);
    }

    /** Disconnect from all of the services of all WebAPKs. */
    public static void disconnectFromAllServices(boolean waitForPendingWork) {
        if (waitForPendingWork && WebApkServiceClient.hasPendingWork()) {
            PostTask.postDelayedTask(UiThreadTaskTraits.DEFAULT,
                    ChromeWebApkHost::disconnectFromAllServicesImpl,
                    WAIT_FOR_WORK_DISCONNECT_SERVICE_DELAY_MS);
        } else {
            disconnectFromAllServicesImpl();
        }
    }

    private static void disconnectFromAllServicesImpl() {
        WebApkIdentityServiceClient.disconnectAll(ContextUtils.getApplicationContext());
        WebApkServiceClient.disconnectAll();
    }
}
