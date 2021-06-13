// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.mojo.system.impl;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.MainDex;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.mojo.system.Core;
import org.monyhar.mojo.system.Handle;
import org.monyhar.mojo.system.MojoResult;
import org.monyhar.mojo.system.Watcher;

@JNINamespace("mojo::android")
@MainDex
class WatcherImpl implements Watcher {
    private long mImplPtr = WatcherImplJni.get().createWatcher(WatcherImpl.this);
    private Callback mCallback;

    @Override
    public int start(Handle handle, Core.HandleSignals signals, Callback callback) {
        if (mImplPtr == 0) {
            return MojoResult.INVALID_ARGUMENT;
        }
        if (!(handle instanceof HandleBase)) {
            return MojoResult.INVALID_ARGUMENT;
        }
        int result = WatcherImplJni.get().start(WatcherImpl.this, mImplPtr,
                ((HandleBase) handle).getMojoHandle(), signals.getFlags());
        if (result == MojoResult.OK) mCallback = callback;
        return result;
    }

    @Override
    public void cancel() {
        if (mImplPtr == 0) {
            return;
        }
        mCallback = null;
        WatcherImplJni.get().cancel(WatcherImpl.this, mImplPtr);
    }

    @Override
    public void destroy() {
        if (mImplPtr == 0) {
            return;
        }
        WatcherImplJni.get().delete(WatcherImpl.this, mImplPtr);
        mImplPtr = 0;
    }

    @CalledByNative
    private void onHandleReady(int result) {
        mCallback.onResult(result);
    }

    @NativeMethods
    interface Natives {
        long createWatcher(WatcherImpl caller);
        int start(WatcherImpl caller, long implPtr, int mojoHandle, int flags);
        void cancel(WatcherImpl caller, long implPtr);
        void delete(WatcherImpl caller, long implPtr);
    }
}
