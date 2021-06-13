// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.password_manager;

import org.monyhar.ui.modelutil.PropertyModel;

/**
 * Data model for the password generation modal dialog.
 */

class PasswordGenerationDialogModel extends PropertyModel {
    /** The generated password to be displayed in the dialog. */
    public static final WritableObjectPropertyKey<String> GENERATED_PASSWORD =
            new WritableObjectPropertyKey<>();

    /** Explanation text for how the generated password is saved. */
    public static final WritableObjectPropertyKey<String> SAVE_EXPLANATION_TEXT =
            new WritableObjectPropertyKey<>();

    /** Default constructor */
    public PasswordGenerationDialogModel() {
        super(GENERATED_PASSWORD, SAVE_EXPLANATION_TEXT);
    }
}
