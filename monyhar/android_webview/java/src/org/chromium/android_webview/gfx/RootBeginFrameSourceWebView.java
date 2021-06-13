// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.android_webview.gfx;

import org.monyhar.base.ContextUtils;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.ui.display.DisplayAndroid;
import org.monyhar.ui.display.DisplayAndroid.DisplayAndroidObserver;

/**
 * Provides DisplayRefreshRate tracking for MainBeginFrameSourceWebView
 */
@JNINamespace("android_webview")
public class RootBeginFrameSourceWebView implements DisplayAndroidObserver {
    private long mNativeRootBeginFrameSourceWebView;
    private final DisplayAndroid mDisplayAndroid;

    @CalledByNative
    private RootBeginFrameSourceWebView(long nativeRootBeginFrameSourceWebView) {
        mNativeRootBeginFrameSourceWebView = nativeRootBeginFrameSourceWebView;

        mDisplayAndroid = DisplayAndroid.getNonMultiDisplay(ContextUtils.getApplicationContext());
        mDisplayAndroid.addObserver(this);
        onRefreshRateChanged(mDisplayAndroid.getRefreshRate());
    }

    @Override
    public void onRefreshRateChanged(float refreshRate) {
        RootBeginFrameSourceWebViewJni.get().onUpdateRefreshRate(
                mNativeRootBeginFrameSourceWebView, RootBeginFrameSourceWebView.this, refreshRate);
    }

    @NativeMethods
    interface Natives {
        void onUpdateRefreshRate(long nativeRootBeginFrameSourceWebView,
                RootBeginFrameSourceWebView caller, float refreshRate);
    }
};