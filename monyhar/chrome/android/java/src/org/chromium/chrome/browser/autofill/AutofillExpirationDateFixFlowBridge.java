// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.autofill;

import android.app.Activity;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.base.task.PostTask;
import org.monyhar.chrome.browser.autofill.AutofillExpirationDateFixFlowPrompt.AutofillExpirationDateFixFlowPromptDelegate;
import org.monyhar.content_public.browser.UiThreadTaskTraits;
import org.monyhar.ui.base.WindowAndroid;
import org.monyhar.ui.modaldialog.DialogDismissalCause;

/**
 * JNI call glue for AutofillExpirationDateFixFlowPrompt C++ and Java objects.
 */
@JNINamespace("autofill")
final class AutofillExpirationDateFixFlowBridge
        implements AutofillExpirationDateFixFlowPromptDelegate {
    private long mNativeCardExpirationDateFixFlowViewAndroid;
    private final String mTitle;
    private final String mConfirmButtonLabel;
    private final int mIconId;
    private final String mCardLabel;
    private AutofillExpirationDateFixFlowPrompt mExpirationDateFixFlowPrompt;

    private AutofillExpirationDateFixFlowBridge(long nativeCardExpirationDateFixFlowViewAndroid,
            String title, String confirmButtonLabel, int iconId, String cardLabel) {
        mNativeCardExpirationDateFixFlowViewAndroid = nativeCardExpirationDateFixFlowViewAndroid;
        mTitle = title;
        mConfirmButtonLabel = confirmButtonLabel;
        mIconId = iconId;
        mCardLabel = cardLabel;
    }

    @CalledByNative
    private static AutofillExpirationDateFixFlowBridge create(
            long nativeCardExpirationDateFixFlowViewAndroid, String title,
            String confirmButtonLabel, int iconId, String cardLabel) {
        return new AutofillExpirationDateFixFlowBridge(nativeCardExpirationDateFixFlowViewAndroid,
                title, confirmButtonLabel, iconId, cardLabel);
    }

    @Override
    public void onPromptDismissed() {
        AutofillExpirationDateFixFlowBridgeJni.get().promptDismissed(
                mNativeCardExpirationDateFixFlowViewAndroid,
                AutofillExpirationDateFixFlowBridge.this);
        mNativeCardExpirationDateFixFlowViewAndroid = 0;
    }

    @Override
    public void onUserAccept(String month, String year) {
        AutofillExpirationDateFixFlowBridgeJni.get().onUserAccept(
                mNativeCardExpirationDateFixFlowViewAndroid,
                AutofillExpirationDateFixFlowBridge.this, month, year);
    }

    @Override
    public void onUserDismiss() {
        AutofillExpirationDateFixFlowBridgeJni.get().onUserDismiss(
                mNativeCardExpirationDateFixFlowViewAndroid,
                AutofillExpirationDateFixFlowBridge.this);
    }

    /**
     * Shows a prompt for expiration date fix flow.
     */
    @CalledByNative
    private void show(WindowAndroid windowAndroid) {
        Activity activity = windowAndroid.getActivity().get();
        if (activity == null) {
            // Clean up the native counterpart. Post the dismissal to allow the native
            // caller to finish execution before we attempt to delete it.
            PostTask.postTask(UiThreadTaskTraits.DEFAULT, this::onPromptDismissed);
            return;
        }

        mExpirationDateFixFlowPrompt = new AutofillExpirationDateFixFlowPrompt(
                activity, this, mTitle, mConfirmButtonLabel, mIconId, mCardLabel);
        mExpirationDateFixFlowPrompt.show(activity, windowAndroid.getModalDialogManager());
    }

    /**
     * Dismisses the prompt without returning any user response.
     */
    @CalledByNative
    private void dismiss() {
        if (mExpirationDateFixFlowPrompt != null) {
            mExpirationDateFixFlowPrompt.dismiss(DialogDismissalCause.DISMISSED_BY_NATIVE);
        }
    }

    @NativeMethods
    interface Natives {
        void promptDismissed(long nativeCardExpirationDateFixFlowViewAndroid,
                AutofillExpirationDateFixFlowBridge caller);
        void onUserAccept(long nativeCardExpirationDateFixFlowViewAndroid,
                AutofillExpirationDateFixFlowBridge caller, String month, String year);
        void onUserDismiss(long nativeCardExpirationDateFixFlowViewAndroid,
                AutofillExpirationDateFixFlowBridge caller);
    }
}
