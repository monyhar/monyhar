// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.mojo;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.blink.mojom.Authenticator;
import org.monyhar.chrome.browser.browserservices.digitalgoods.DigitalGoodsFactoryFactory;
import org.monyhar.chrome.browser.installedapp.InstalledAppProviderFactory;
import org.monyhar.chrome.browser.payments.ChromePaymentRequestFactory;
import org.monyhar.chrome.browser.webshare.ShareServiceImplementationFactory;
import org.monyhar.components.webauthn.AuthenticatorFactory;
import org.monyhar.content_public.browser.InterfaceRegistrar;
import org.monyhar.content_public.browser.RenderFrameHost;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.installedapp.mojom.InstalledAppProvider;
import org.monyhar.payments.mojom.DigitalGoodsFactory;
import org.monyhar.payments.mojom.PaymentRequest;
import org.monyhar.services.service_manager.InterfaceRegistry;
import org.monyhar.webshare.mojom.ShareService;

/** Registers mojo interface implementations exposed to C++ code at the Chrome layer. */
class ChromeInterfaceRegistrar {
    @CalledByNative
    private static void registerMojoInterfaces() {
        InterfaceRegistrar.Registry.addWebContentsRegistrar(
                new ChromeWebContentsInterfaceRegistrar());
        InterfaceRegistrar.Registry.addRenderFrameHostRegistrar(
                new ChromeRenderFrameHostInterfaceRegistrar());
    }

    private static class ChromeWebContentsInterfaceRegistrar
            implements InterfaceRegistrar<WebContents> {
        @Override
        public void registerInterfaces(InterfaceRegistry registry, final WebContents webContents) {
            registry.addInterface(
                    ShareService.MANAGER, new ShareServiceImplementationFactory(webContents));
        }
    }

    private static class ChromeRenderFrameHostInterfaceRegistrar
            implements InterfaceRegistrar<RenderFrameHost> {
        @Override
        public void registerInterfaces(
                InterfaceRegistry registry, final RenderFrameHost renderFrameHost) {
            registry.addInterface(
                    PaymentRequest.MANAGER, new ChromePaymentRequestFactory(renderFrameHost));
            registry.addInterface(
                    InstalledAppProvider.MANAGER, new InstalledAppProviderFactory(renderFrameHost));
            registry.addInterface(Authenticator.MANAGER, new AuthenticatorFactory(renderFrameHost));
            registry.addInterface(
                    DigitalGoodsFactory.MANAGER, new DigitalGoodsFactoryFactory(renderFrameHost));
        }
    }
}
