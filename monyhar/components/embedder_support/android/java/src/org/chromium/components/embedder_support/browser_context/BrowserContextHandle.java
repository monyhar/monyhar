// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.embedder_support.browser_context;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;

/**
 * An interface that provides access to a native BrowserContext.
 */
@JNINamespace("browser_context")
public interface BrowserContextHandle {
    /** @return A pointer to the native BrowserContext that this object wraps. */
    @CalledByNative
    long getNativeBrowserContextPointer();
}
