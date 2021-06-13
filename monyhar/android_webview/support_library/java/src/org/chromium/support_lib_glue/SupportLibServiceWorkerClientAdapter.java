// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.support_lib_glue;

import android.webkit.WebResourceResponse;

import com.android.webview.monyhar.ServiceWorkerClientAdapter;
import com.android.webview.monyhar.WebResourceRequestAdapter;

import org.monyhar.android_webview.AwContentsClient.AwWebResourceRequest;
import org.monyhar.android_webview.AwServiceWorkerClient;
import org.monyhar.components.embedder_support.util.WebResourceResponseInfo;
import org.monyhar.support_lib_boundary.ServiceWorkerClientBoundaryInterface;
import org.monyhar.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;
import org.monyhar.support_lib_boundary.util.Features;

/**
 * Adapter between ServiceWorkerClientBoundaryInterface and AwServiceWorkerClient.
 */
class SupportLibServiceWorkerClientAdapter extends AwServiceWorkerClient {
    ServiceWorkerClientBoundaryInterface mImpl;

    SupportLibServiceWorkerClientAdapter(ServiceWorkerClientBoundaryInterface impl) {
        mImpl = impl;
    }

    @Override
    public WebResourceResponseInfo shouldInterceptRequest(AwWebResourceRequest request) {
        if (!BoundaryInterfaceReflectionUtil.containsFeature(mImpl.getSupportedFeatures(),
                    Features.SERVICE_WORKER_SHOULD_INTERCEPT_REQUEST)) {
            // If the shouldInterceptRequest callback isn't supported, return null;
            return null;
        }
        WebResourceResponse response =
                mImpl.shouldInterceptRequest(new WebResourceRequestAdapter(request));
        return ServiceWorkerClientAdapter.fromWebResourceResponse(response);
    }
}
