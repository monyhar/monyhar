// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer_private;

import org.monyhar.base.LifetimeAssert;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.weblayer_private.interfaces.IPrerenderController;

/**
 *  Implementation of {@link IPrerenderController}.
 */
@JNINamespace("weblayer")
public class PrerenderControllerImpl extends IPrerenderController.Stub {
    private long mNativePrerenderController;
    private final LifetimeAssert mLifetimeAssert = LifetimeAssert.create(this);

    void destroy() {
        mNativePrerenderController = 0;

        // If mLifetimeAssert is GC'ed before this is called, it will throw an exception
        // with a stack trace showing the stack during LifetimeAssert.create().
        LifetimeAssert.setSafeToGc(mLifetimeAssert, true);
    }

    public PrerenderControllerImpl(long nativePrerenderController) {
        mNativePrerenderController = nativePrerenderController;
    }

    @Override
    public void prerender(String url) {
        PrerenderControllerImplJni.get().prerender(mNativePrerenderController, url);
    }

    @NativeMethods()
    interface Natives {
        void prerender(long nativePrerenderControllerImpl, String url);
    }
}
