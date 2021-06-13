// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.password_manager;

import android.content.res.Resources;
import android.view.View;

import org.monyhar.base.Callback;
import org.monyhar.chrome.R;
import org.monyhar.ui.modaldialog.ModalDialogProperties;
import org.monyhar.ui.modelutil.PropertyModel;

/** Mediator class responsible for initializing the model state. */
public class PasswordGenerationDialogMediator {
    private static class DialogController implements ModalDialogProperties.Controller {
        private final Callback<Boolean> mPasswordActionCallback;

        public DialogController(Callback<Boolean> passwordActionCallback) {
            mPasswordActionCallback = passwordActionCallback;
        }

        @Override
        public void onClick(PropertyModel model, int buttonType) {
            switch (buttonType) {
                case ModalDialogProperties.ButtonType.POSITIVE:
                    mPasswordActionCallback.onResult(true);
                    break;
                case ModalDialogProperties.ButtonType.NEGATIVE:
                    mPasswordActionCallback.onResult(false);
                    break;
                default:
                    assert false : "Unexpected button pressed in dialog: " + buttonType;
            }
        }

        @Override
        public void onDismiss(PropertyModel model, int dismissalCause) {
            mPasswordActionCallback.onResult(false);
        }
    }

    public static void initializeState(
            PasswordGenerationDialogModel model, String password, String saveExplanationText) {
        model.set(PasswordGenerationDialogModel.GENERATED_PASSWORD, password);
        model.set(PasswordGenerationDialogModel.SAVE_EXPLANATION_TEXT, saveExplanationText);
    }

    static PropertyModel.Builder createDialogModelBuilder(
            Callback<Boolean> onPasswordAcceptedOrRejected, View customView) {
        Resources resources = customView.getResources();
        return new PropertyModel.Builder(ModalDialogProperties.ALL_KEYS)
                .with(ModalDialogProperties.CONTROLLER,
                        new DialogController(onPasswordAcceptedOrRejected))
                .with(ModalDialogProperties.TITLE, resources,
                        R.string.password_generation_dialog_title)
                .with(ModalDialogProperties.CUSTOM_VIEW, customView)
                .with(ModalDialogProperties.POSITIVE_BUTTON_TEXT, resources,
                        R.string.password_generation_dialog_use_password_button)
                .with(ModalDialogProperties.NEGATIVE_BUTTON_TEXT, resources,
                        R.string.password_generation_dialog_cancel_button);
    }
}
