// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.support_lib_glue;

import android.annotation.SuppressLint;
import android.webkit.SafeBrowsingResponse;
import android.webkit.ServiceWorkerWebSettings;
import android.webkit.WebMessagePort;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;

import com.android.webview.monyhar.SafeBrowsingResponseAdapter;
import com.android.webview.monyhar.ServiceWorkerSettingsAdapter;
import com.android.webview.monyhar.WebMessagePortAdapter;
import com.android.webview.monyhar.WebResourceErrorAdapter;
import com.android.webview.monyhar.WebkitToSharedGlueConverter;

import org.monyhar.support_lib_boundary.WebkitToCompatConverterBoundaryInterface;
import org.monyhar.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;
import org.monyhar.support_lib_callback_glue.SupportLibSafeBrowsingResponse;
import org.monyhar.support_lib_callback_glue.SupportLibWebResourceError;

import java.lang.reflect.InvocationHandler;

/**
 * Adapter used for fetching implementations for Compat objects given their corresponding
 * webkit-object.
 */
class SupportLibWebkitToCompatConverterAdapter implements WebkitToCompatConverterBoundaryInterface {
    SupportLibWebkitToCompatConverterAdapter() {}

    // WebSettingsBoundaryInterface
    @Override
    public InvocationHandler convertSettings(WebSettings webSettings) {
        return BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(
                new SupportLibWebSettingsAdapter(
                        WebkitToSharedGlueConverter.getSettings(webSettings)));
    }

    // WebResourceRequestBoundaryInterface
    @Override
    public InvocationHandler convertWebResourceRequest(WebResourceRequest request) {
        return BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(
                new SupportLibWebResourceRequest(
                        WebkitToSharedGlueConverter.getWebResourceRequest(request)));
    }

    // ServiceWorkerWebSettingsBoundaryInterface
    @SuppressLint("NewApi")
    @Override
    public InvocationHandler convertServiceWorkerSettings(
            /* ServiceWorkerWebSettings */ Object serviceWorkerWebSettings) {
        return BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(
                new SupportLibServiceWorkerSettingsAdapter(
                        WebkitToSharedGlueConverter.getServiceWorkerSettings(
                                (ServiceWorkerWebSettings) serviceWorkerWebSettings)));
    }

    @Override
    public /* ServiceWorkerWebSettings */ Object convertServiceWorkerSettings(
            /* SupportLibServiceWorkerSettings */ InvocationHandler serviceWorkerSettings) {
        SupportLibServiceWorkerSettingsAdapter supportLibWebSettings =
                (SupportLibServiceWorkerSettingsAdapter) BoundaryInterfaceReflectionUtil
                        .getDelegateFromInvocationHandler(serviceWorkerSettings);
        return new ServiceWorkerSettingsAdapter(supportLibWebSettings.getAwServiceWorkerSettings());
    }

    @SuppressLint("NewApi")
    @Override
    public /* SupportLibWebResourceError */ InvocationHandler convertWebResourceError(
            /* WebResourceError */ Object webResourceError) {
        return BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(
                new SupportLibWebResourceError(WebkitToSharedGlueConverter.getAwWebResourceError(
                        (WebResourceError) webResourceError)));
    }

    @Override
    public /* WebResourceError */ Object convertWebResourceError(
            /* SupportLibWebResourceError */ InvocationHandler webResourceError) {
        SupportLibWebResourceError supportLibError =
                (SupportLibWebResourceError) BoundaryInterfaceReflectionUtil
                        .getDelegateFromInvocationHandler(webResourceError);
        return new WebResourceErrorAdapter(supportLibError.getAwWebResourceError());
    }

    @SuppressLint("NewApi")
    @Override
    public /* SupportLibSafeBrowsingResponse */ InvocationHandler convertSafeBrowsingResponse(
            /* SafeBrowsingResponse */ Object safeBrowsingResponse) {
        return BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(
                new SupportLibSafeBrowsingResponse(
                        WebkitToSharedGlueConverter.getAwSafeBrowsingResponseCallback(
                                (SafeBrowsingResponse) safeBrowsingResponse)));
    }

    @Override
    public /* SafeBrowsingResponse */ Object convertSafeBrowsingResponse(
            /* SupportLibSafeBrowsingResponse */ InvocationHandler safeBrowsingResponse) {
        SupportLibSafeBrowsingResponse supportLibResponse =
                (SupportLibSafeBrowsingResponse) BoundaryInterfaceReflectionUtil
                        .getDelegateFromInvocationHandler(safeBrowsingResponse);
        return new SafeBrowsingResponseAdapter(
                supportLibResponse.getAwSafeBrowsingResponseCallback());
    }

    @SuppressLint("NewApi")
    @Override
    public /* SupportLibWebMessagePort */ InvocationHandler convertWebMessagePort(
            /* WebMessagePort */ Object webMessagePort) {
        return BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(
                new SupportLibWebMessagePortAdapter(WebkitToSharedGlueConverter.getMessagePort(
                        (WebMessagePort) webMessagePort)));
    }

    @Override
    public /* WebMessagePort */ Object convertWebMessagePort(
            /* SupportLibWebMessagePort */ InvocationHandler webMessagePort) {
        SupportLibWebMessagePortAdapter supportLibMessagePort =
                (SupportLibWebMessagePortAdapter) BoundaryInterfaceReflectionUtil
                        .getDelegateFromInvocationHandler(webMessagePort);
        return new WebMessagePortAdapter(supportLibMessagePort.getPort());
    }
}
