// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.support_lib_glue;

import static org.monyhar.support_lib_glue.SupportLibWebViewChromiumFactory.recordApiCall;

import org.monyhar.android_webview.AwServiceWorkerController;
import org.monyhar.support_lib_boundary.ServiceWorkerClientBoundaryInterface;
import org.monyhar.support_lib_boundary.ServiceWorkerControllerBoundaryInterface;
import org.monyhar.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;
import org.monyhar.support_lib_glue.SupportLibWebViewChromiumFactory.ApiCall;

import java.lang.reflect.InvocationHandler;

/**
 * Adapter between AwServiceWorkerController and ServiceWorkerControllerBoundaryInterface.
 */
class SupportLibServiceWorkerControllerAdapter implements ServiceWorkerControllerBoundaryInterface {
    AwServiceWorkerController mAwServiceWorkerController;

    SupportLibServiceWorkerControllerAdapter(AwServiceWorkerController awServiceController) {
        mAwServiceWorkerController = awServiceController;
    }

    @Override
    public InvocationHandler getServiceWorkerWebSettings() {
        recordApiCall(ApiCall.GET_SERVICE_WORKER_WEB_SETTINGS);
        return BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(
                new SupportLibServiceWorkerSettingsAdapter(
                        mAwServiceWorkerController.getAwServiceWorkerSettings()));
    }

    @Override
    public void setServiceWorkerClient(InvocationHandler client) {
        recordApiCall(ApiCall.SET_SERVICE_WORKER_CLIENT);
        mAwServiceWorkerController.setServiceWorkerClient(new SupportLibServiceWorkerClientAdapter(
                BoundaryInterfaceReflectionUtil.castToSuppLibClass(
                        ServiceWorkerClientBoundaryInterface.class, client)));
    }
}
