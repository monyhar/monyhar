// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.enterprise.util;

import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.chrome.browser.profiles.Profile;

/**
 * Util methods for interacting with managed browser (enterprise) state.
 */
public class ManagedBrowserUtils {
    /**
     * Wrapper around native call to determine if policies have been applied for this browser.
     */
    public static boolean hasBrowserPoliciesApplied(Profile profile) {
        return ManagedBrowserUtilsJni.get().hasBrowserPoliciesApplied(profile);
    }

    @NativeMethods
    public interface Natives {
        boolean hasBrowserPoliciesApplied(Profile profile);
    }
}
