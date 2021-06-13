// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.minidump_uploader.util;

/**
 * Interface for crash reporting permissions.
 */
public interface CrashReportingPermissionManager {
    /**
     * Checks whether this client is in-sample for usage metrics and crash reporting. See
     * {@link org.monyhar.chrome.browser.metrics.UmaUtils#isClientInMetricsSample} for details.
     *
     * @returns boolean Whether client is in-sample.
     */
    public boolean isClientInMetricsSample();

    /**
     * Checks whether uploading of crash dumps is permitted for the available network(s).
     *
     * @return whether uploading crash dumps is permitted.
     */
    public boolean isNetworkAvailableForCrashUploads();

    /**
     * Checks whether uploading of usage metrics and crash dumps is currently permitted, based on
     * user consent only.
     *
     * @return whether the user has consented to reporting usage metrics and crash dumps.
     */
    public boolean isUsageAndCrashReportingPermittedByUser();

    /**
     * Checks whether to ignore all consent and upload limitations for usage metrics and crash
     * reporting. Used by test devices to avoid a UI dependency.
     *
     * @return whether crash dumps should be uploaded if at all possible.
     */
    public boolean isUploadEnabledForTests();
}
