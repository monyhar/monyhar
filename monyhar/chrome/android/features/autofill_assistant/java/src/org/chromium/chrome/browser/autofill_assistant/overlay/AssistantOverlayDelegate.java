// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.autofill_assistant.overlay;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;

/** Delegate for the overlay. */
@JNINamespace("autofill_assistant")
class AssistantOverlayDelegate {
    private long mNativeAssistantOverlayDelegate;

    @CalledByNative
    private static AssistantOverlayDelegate create(long nativeAssistantOverlayDelegate) {
        return new AssistantOverlayDelegate(nativeAssistantOverlayDelegate);
    }

    private AssistantOverlayDelegate(long nativeAssistantOverlayDelegate) {
        mNativeAssistantOverlayDelegate = nativeAssistantOverlayDelegate;
    }

    /** Called after a certain number of unexpected taps. */
    void onUnexpectedTaps() {
        if (mNativeAssistantOverlayDelegate != 0) {
            AssistantOverlayDelegateJni.get().onUnexpectedTaps(
                    mNativeAssistantOverlayDelegate, AssistantOverlayDelegate.this);
        }
    }

    /**
     * Called when interaction within allowed touchable area was detected. The interaction
     * could be any gesture.
     */
    void onUserInteractionInsideTouchableArea() {
        if (mNativeAssistantOverlayDelegate != 0) {
            AssistantOverlayDelegateJni.get().onUserInteractionInsideTouchableArea(
                    mNativeAssistantOverlayDelegate, AssistantOverlayDelegate.this);
        }
    }

    @CalledByNative
    private void clearNativePtr() {
        mNativeAssistantOverlayDelegate = 0;
    }

    @NativeMethods
    interface Natives {
        void onUnexpectedTaps(long nativeAssistantOverlayDelegate, AssistantOverlayDelegate caller);
        void onUserInteractionInsideTouchableArea(
                long nativeAssistantOverlayDelegate, AssistantOverlayDelegate caller);
    }
}
