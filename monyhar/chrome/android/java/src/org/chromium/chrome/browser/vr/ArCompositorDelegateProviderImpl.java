// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.vr;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.components.webxr.ArCompositorDelegate;
import org.monyhar.components.webxr.ArCompositorDelegateProvider;
import org.monyhar.content_public.browser.WebContents;

/**
 * Concrete, Chrome-specific implementation of ArCompositorDelegateProvider interface.
 */
@JNINamespace("vr")
public class ArCompositorDelegateProviderImpl implements ArCompositorDelegateProvider {
    @CalledByNative
    public ArCompositorDelegateProviderImpl() {}

    @Override
    public ArCompositorDelegate create(WebContents webContents) {
        return new ArCompositorDelegateImpl(webContents);
    }
}
