// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content_public.browser.test.util;

import android.content.Intent;
import android.view.ActionMode;
import android.view.textclassifier.TextClassifier;

import org.monyhar.content_public.browser.ActionModeCallbackHelper;
import org.monyhar.content_public.browser.SelectionClient;
import org.monyhar.content_public.browser.SelectionPopupController;

/**
 * A dummy {@link SelectionPopupController} implementation that can be overriden by tests
 * to customize behavior.
 */
public class TestSelectionPopupController implements SelectionPopupController {
    public TestSelectionPopupController() {}

    @Override
    public void setActionModeCallback(ActionMode.Callback callback) {}

    @Override
    public void setNonSelectionActionModeCallback(ActionMode.Callback callback) {}

    @Override
    public SelectionClient.ResultCallback getResultCallback() {
        return null;
    }

    @Override
    public String getSelectedText() {
        return null;
    }

    @Override
    public boolean isFocusedNodeEditable() {
        return false;
    }

    @Override
    public boolean hasSelection() {
        return false;
    }

    @Override
    public void destroySelectActionMode() {}

    @Override
    public boolean isSelectActionBarShowing() {
        return false;
    }

    @Override
    public ActionModeCallbackHelper getActionModeCallbackHelper() {
        return null;
    }

    @Override
    public void clearSelection() {}

    @Override
    public void onReceivedProcessTextResult(int resultCode, Intent data) {}

    @Override
    public void setSelectionClient(SelectionClient selectionClient) {}

    @Override
    public void setTextClassifier(TextClassifier textClassifier) {}

    @Override
    public TextClassifier getTextClassifier() {
        return null;
    }

    @Override
    public TextClassifier getCustomTextClassifier() {
        return null;
    }

    @Override
    public void setPreserveSelectionOnNextLossOfFocus(boolean preserve) {}

    @Override
    public void updateTextSelectionUI(boolean focused) {}
}
