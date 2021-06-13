// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.content_settings;

import androidx.annotation.Nullable;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.components.embedder_support.browser_context.BrowserContextHandle;
import org.monyhar.content_public.browser.WebContents;

/**
 * Communicates between CookieControlsController (C++ backend) and PageInfoView (Java UI).
 */
@JNINamespace("content_settings")
public class CookieControlsBridge {
    private long mNativeCookieControlsBridge;
    private CookieControlsObserver mObserver;

    /**
     * Initializes a CookieControlsBridge instance.
     * @param observer An observer to call with updates from the cookie controller.
     * @param webContents The WebContents instance to observe.
     * @param originalBrowserContext The "original" browser context. In Chrome, this corresponds to
     *         the regular profile when webContents is incognito.
     */
    public CookieControlsBridge(CookieControlsObserver observer, WebContents webContents,
            @Nullable BrowserContextHandle originalBrowserContext) {
        mObserver = observer;
        mNativeCookieControlsBridge = CookieControlsBridgeJni.get().init(
                CookieControlsBridge.this, webContents, originalBrowserContext);
    }

    public void setThirdPartyCookieBlockingEnabledForSite(boolean blockCookies) {
        if (mNativeCookieControlsBridge != 0) {
            CookieControlsBridgeJni.get().setThirdPartyCookieBlockingEnabledForSite(
                    mNativeCookieControlsBridge, blockCookies);
        }
    }

    public void onUiClosing() {
        if (mNativeCookieControlsBridge != 0) {
            CookieControlsBridgeJni.get().onUiClosing(mNativeCookieControlsBridge);
        }
    }

    /**
     * Destroys the native counterpart of this class.
     */
    public void destroy() {
        if (mNativeCookieControlsBridge != 0) {
            CookieControlsBridgeJni.get().destroy(
                    mNativeCookieControlsBridge, CookieControlsBridge.this);
            mNativeCookieControlsBridge = 0;
        }
    }

    public static boolean isCookieControlsEnabled(BrowserContextHandle handle) {
        return CookieControlsBridgeJni.get().isCookieControlsEnabled(handle);
    }

    @CalledByNative
    private void onCookieBlockingStatusChanged(
            @CookieControlsStatus int status, @CookieControlsEnforcement int enforcement) {
        mObserver.onCookieBlockingStatusChanged(status, enforcement);
    }

    @CalledByNative
    private void onCookiesCountChanged(int allowedCookies, int blockedCookies) {
        mObserver.onCookiesCountChanged(allowedCookies, blockedCookies);
    }

    @NativeMethods
    interface Natives {
        long init(CookieControlsBridge caller, WebContents webContents,
                BrowserContextHandle originalContextHandle);
        void destroy(long nativeCookieControlsBridge, CookieControlsBridge caller);
        void setThirdPartyCookieBlockingEnabledForSite(
                long nativeCookieControlsBridge, boolean blockCookies);
        void onUiClosing(long nativeCookieControlsBridge);
        boolean isCookieControlsEnabled(BrowserContextHandle browserContextHandle);
    }
}
