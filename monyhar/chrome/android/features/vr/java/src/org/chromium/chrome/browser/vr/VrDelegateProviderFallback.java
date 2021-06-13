// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.vr;

/**
 * Fallback {@link VrDelegateProvider} implementation if the VR module is not
 * available.
 */
/* package */ class VrDelegateProviderFallback implements VrDelegateProvider {
    private final VrDelegateFallback mDelegate = new VrDelegateFallback();
    private final VrIntentDelegateFallback mIntentDelegate = new VrIntentDelegateFallback();

    @Override
    public VrDelegate getDelegate() {
        return mDelegate;
    }

    @Override
    public VrIntentDelegate getIntentDelegate() {
        return mIntentDelegate;
    }
}
