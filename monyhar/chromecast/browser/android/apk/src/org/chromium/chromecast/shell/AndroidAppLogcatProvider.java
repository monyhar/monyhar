// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chromecast.shell;

import org.monyhar.base.ContextUtils;
import org.monyhar.base.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Extracts logcat out of Android devices and elide PII sensitive info from it.
 *
 * <p>Elided information includes: Emails, IP address, MAC address, URL/domains as well as
 * Javascript console messages.
 */
class AndroidAppLogcatProvider extends ElidedLogcatProvider {
    private static final String TAG = "LogcatProvider";
    private static final long HALF_SECOND = 500;

    @Override
    protected void getRawLogcat(RawLogcatCallback callback) {
        Integer exitValue = null;

        Reader reader = new StringReader("");
        try {
            File outputDir = ContextUtils.getApplicationContext().getCacheDir();
            File outputFile = File.createTempFile("temp_logcat", ".txt", outputDir);

            Process p = Runtime.getRuntime().exec("logcat -d -f " + outputFile.getAbsolutePath());

            while (exitValue == null) {
                try {
                    exitValue = p.exitValue();
                } catch (IllegalThreadStateException itse) {
                    Thread.sleep(HALF_SECOND);
                }
            }
            if (exitValue != 0) {
                String msg = "Logcat process exit value: " + exitValue;
                Log.w(TAG, msg);
            }
            reader = new FileReader(outputFile);

        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Error writing logcat", e);
        } finally {
            callback.onLogsDone(new BufferedReader(reader));
        }
    }
}
