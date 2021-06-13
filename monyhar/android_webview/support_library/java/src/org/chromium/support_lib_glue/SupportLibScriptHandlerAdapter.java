// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.support_lib_glue;

import static org.monyhar.support_lib_glue.SupportLibWebViewChromiumFactory.recordApiCall;

import org.monyhar.android_webview.ScriptHandler;
import org.monyhar.support_lib_boundary.ScriptHandlerBoundaryInterface;
import org.monyhar.support_lib_glue.SupportLibWebViewChromiumFactory.ApiCall;

/**
 * Adapter between ScriptHandlerBoundaryInterface and ScriptHandler.
 */
class SupportLibScriptHandlerAdapter implements ScriptHandlerBoundaryInterface {
    private ScriptHandler mScriptHandler;

    public SupportLibScriptHandlerAdapter(ScriptHandler scriptHandler) {
        mScriptHandler = scriptHandler;
    }

    @Override
    public void remove() {
        recordApiCall(ApiCall.REMOVE_DOCUMENT_START_SCRIPT);
        mScriptHandler.remove();
    }
}
