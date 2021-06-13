// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer_private;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.blink.mojom.Authenticator;
import org.monyhar.components.webauthn.AuthenticatorFactory;
import org.monyhar.content_public.browser.InterfaceRegistrar;
import org.monyhar.content_public.browser.RenderFrameHost;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.installedapp.mojom.InstalledAppProvider;
import org.monyhar.payments.mojom.PaymentRequest;
import org.monyhar.services.service_manager.InterfaceRegistry;
import org.monyhar.weblayer_private.payments.WebLayerPaymentRequestFactory;
import org.monyhar.webshare.mojom.ShareService;

/**
 * Registers Java implementations of mojo interfaces.
 */
class MojoInterfaceRegistrar {
    @CalledByNative
    private static void registerMojoInterfaces() {
        InterfaceRegistrar.Registry.addWebContentsRegistrar(new WebContentsInterfaceRegistrar());
        InterfaceRegistrar.Registry.addRenderFrameHostRegistrar(
                new RenderFrameHostInterfaceRegistrar());
    }

    private static class WebContentsInterfaceRegistrar implements InterfaceRegistrar<WebContents> {
        @Override
        public void registerInterfaces(InterfaceRegistry registry, final WebContents webContents) {
            registry.addInterface(ShareService.MANAGER, new WebShareServiceFactory(webContents));
        }
    }

    private static class RenderFrameHostInterfaceRegistrar
            implements InterfaceRegistrar<RenderFrameHost> {
        @Override
        public void registerInterfaces(
                InterfaceRegistry registry, final RenderFrameHost renderFrameHost) {
            registry.addInterface(Authenticator.MANAGER, new AuthenticatorFactory(renderFrameHost));
            registry.addInterface(
                    InstalledAppProvider.MANAGER, new InstalledAppProviderFactory(renderFrameHost));
            registry.addInterface(
                    PaymentRequest.MANAGER, new WebLayerPaymentRequestFactory(renderFrameHost));
        }
    }
}
