// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.firstrun;

import androidx.fragment.app.Fragment;

/**
 * Represents first run page shown during the First Run. Actual page implementation is created
 * lazily by {@link #instantiateFragment()}.
 * @param <T> the type of the fragment that displays this FRE page.
 */
public interface FirstRunPage<T extends Fragment & FirstRunFragment> {
    /**
     * @return Whether this page should be skipped on the FRE creation.
     */
    default boolean shouldSkipPageOnCreate() {
        return false;
    }

    /**
     * Creates fragment that implements this FRE page.
     */
    T instantiateFragment();
}
