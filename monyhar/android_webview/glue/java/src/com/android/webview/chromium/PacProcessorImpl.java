// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.android.webview.monyhar;

import android.webkit.PacProcessor;

import org.monyhar.android_webview.AwPacProcessor;
import org.monyhar.base.JNIUtils;
import org.monyhar.base.library_loader.LibraryLoader;

final class PacProcessorImpl implements PacProcessor {
    private PacProcessorImpl() {
        JNIUtils.setClassLoader(WebViewChromiumFactoryProvider.class.getClassLoader());
        LibraryLoader.getInstance().ensureInitialized();

        // This will set up Chromium environment to run proxy resolver.
        AwPacProcessor.initializeEnvironment();
    }

    private static final PacProcessorImpl sInstance = new PacProcessorImpl();

    public static PacProcessorImpl getInstance() {
        return sInstance;
    }

    @Override
    public boolean setProxyScript(String script) {
        return AwPacProcessor.getInstance().setProxyScript(script);
    }

    @Override
    public String findProxyForUrl(String url) {
        return AwPacProcessor.getInstance().makeProxyRequest(url);
    }
}
