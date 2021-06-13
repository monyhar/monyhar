// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.version_info;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.MainDex;

/**
 * Bridge between native and VersionConstants.java.
 */
@MainDex
public class VersionConstantsBridge {
    @CalledByNative
    public static int getChannel() {
        return VersionConstants.CHANNEL;
    }
}
