// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.embedder_support.simple_factory_key;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;

/** An interface that provides access to a native SimpleFactoryKey. */
@JNINamespace("simple_factory_key")
public interface SimpleFactoryKeyHandle {
    /** @return A pointer to the native SimpleFactoryKey that this object wraps. */
    @CalledByNative
    long getNativeSimpleFactoryKeyPointer();
}