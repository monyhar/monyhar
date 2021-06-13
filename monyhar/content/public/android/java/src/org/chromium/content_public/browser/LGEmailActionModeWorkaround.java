// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content_public.browser;

import org.monyhar.content.browser.selection.LGEmailActionModeWorkaroundImpl;

/**
 * This is a workaround for LG Email app: https://crbug.com/651706
 * LG Email app runs UI-thread APIs from InputConnection methods. This is not allowable with
 * the change ImeThread introduces, and LG Email app is bundled and cannot be updated without
 * a system update. However, LG Email team is committed to fixing this in the near future.
 * This is a version code limited workaround to avoid crashes in the app.
 */
public final class LGEmailActionModeWorkaround {
    private LGEmailActionModeWorkaround() {}

    public static boolean isSafeVersion(int versionCode) {
        return LGEmailActionModeWorkaroundImpl.isSafeVersion(versionCode);
    }
}
