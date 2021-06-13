// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.autofill_assistant.infobox;

import androidx.annotation.VisibleForTesting;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.ui.modelutil.PropertyModel;

/**
 * State for the infobox of the Autofill Assistant.
 */
@JNINamespace("autofill_assistant")
public class AssistantInfoBoxModel extends PropertyModel {
    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public static final WritableObjectPropertyKey<AssistantInfoBox> INFO_BOX =
            new WritableObjectPropertyKey<>();

    public AssistantInfoBoxModel() {
        super(INFO_BOX);
    }

    @CalledByNative
    private void setInfoBox(AssistantInfoBox infobox) {
        set(INFO_BOX, infobox);
    }

    @CalledByNative
    private void clearInfoBox() {
        set(INFO_BOX, null);
    }
}
