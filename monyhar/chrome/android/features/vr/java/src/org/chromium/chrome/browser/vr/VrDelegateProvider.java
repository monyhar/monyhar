// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.vr;

import org.monyhar.components.module_installer.builder.ModuleInterface;

/** Provides delegate interfaces that can be used to call into VR.  */
@ModuleInterface(module = "vr", impl = "org.monyhar.chrome.browser.vr.VrDelegateProviderImpl")
public interface VrDelegateProvider {
    VrDelegate getDelegate();
    VrIntentDelegate getIntentDelegate();
}
