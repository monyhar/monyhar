// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content.browser;

import android.view.Surface;

import org.monyhar.base.UnguessableToken;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.content.common.IGpuProcessCallback;
import org.monyhar.content.common.SurfaceWrapper;

@JNINamespace("content")
class GpuProcessCallback extends IGpuProcessCallback.Stub {
    GpuProcessCallback() {}

    @Override
    public void forwardSurfaceForSurfaceRequest(UnguessableToken requestToken, Surface surface) {
        GpuProcessCallbackJni.get().completeScopedSurfaceRequest(requestToken, surface);
    }

    @Override
    public SurfaceWrapper getViewSurface(int surfaceId) {
        return GpuProcessCallbackJni.get().getViewSurface(surfaceId);
    }

    @NativeMethods
    interface Natives {
        void completeScopedSurfaceRequest(UnguessableToken requestToken, Surface surface);
        SurfaceWrapper getViewSurface(int surfaceId);
    }
};
