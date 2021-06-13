// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.user_prefs;

import androidx.annotation.VisibleForTesting;

import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.components.embedder_support.browser_context.BrowserContextHandle;
import org.monyhar.components.prefs.PrefService;

/**
 * Helper for retrieving a {@link PrefService} from a {@link BrowserContextHandle}.
 * This class is modeled after the C++ class of the same name.
 */
@JNINamespace("user_prefs")
public class UserPrefs {
    /** Returns the {@link PrefService} associated with the given {@link BrowserContextHandle}. */
    public static PrefService get(BrowserContextHandle browserContextHandle) {
        return UserPrefsJni.get().get(browserContextHandle);
    }

    @VisibleForTesting
    @NativeMethods
    public interface Natives {
        PrefService get(BrowserContextHandle browserContextHandle);
    }
}
