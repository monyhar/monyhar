// Copyright 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content.browser;

import org.monyhar.base.CommandLine;
import org.monyhar.base.StrictModeContext;
import org.monyhar.content_public.common.ContentSwitches;
import org.monyhar.ui.base.DeviceFormFactor;

/**
 * A utility class that has helper methods for device configuration.
 */
public class DeviceUtilsImpl {
    private DeviceUtilsImpl() {}

    public static void addDeviceSpecificUserAgentSwitch() {
        try (StrictModeContext ignored = StrictModeContext.allowDiskReads()) {
            if (!DeviceFormFactor.isTablet()) {
                CommandLine.getInstance().appendSwitch(ContentSwitches.USE_MOBILE_UA);
            }
        }
    }
}
