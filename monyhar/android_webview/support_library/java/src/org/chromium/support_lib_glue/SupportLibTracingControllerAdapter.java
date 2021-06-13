// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.support_lib_glue;

import static org.monyhar.support_lib_glue.SupportLibWebViewChromiumFactory.recordApiCall;

import com.android.webview.monyhar.SharedTracingControllerAdapter;

import org.monyhar.support_lib_boundary.TracingControllerBoundaryInterface;
import org.monyhar.support_lib_glue.SupportLibWebViewChromiumFactory.ApiCall;

import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.Executor;

/**
 * Adapter between AwTracingController and TracingControllerBoundaryInterface.
 */
public class SupportLibTracingControllerAdapter implements TracingControllerBoundaryInterface {
    private final SharedTracingControllerAdapter mTracingController;

    public SupportLibTracingControllerAdapter(SharedTracingControllerAdapter tracingController) {
        mTracingController = tracingController;
    }

    @Override
    public boolean isTracing() {
        recordApiCall(ApiCall.TRACING_CONTROLLER_IS_TRACING);
        return mTracingController.isTracing();
    }

    @Override
    public void start(int predefinedCategories,
                      Collection<String> customIncludedCategories, int mode)
            throws IllegalStateException, IllegalArgumentException {
        recordApiCall(ApiCall.TRACING_CONTROLLER_START);
        mTracingController.start(predefinedCategories, customIncludedCategories, mode);
    }

    @Override
    public boolean stop(OutputStream outputStream, Executor executor) {
        recordApiCall(ApiCall.TRACING_CONTROLLER_STOP);
        return mTracingController.stop(outputStream, executor);
    }
}
