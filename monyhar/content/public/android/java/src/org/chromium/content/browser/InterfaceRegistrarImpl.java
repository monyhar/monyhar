// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content.browser;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.blink.mojom.AndroidFontLookup;
import org.monyhar.content.browser.androidoverlay.AndroidOverlayProviderImpl;
import org.monyhar.content.browser.font.AndroidFontLookupImpl;
import org.monyhar.content.mojom.LocalTrustTokenFulfiller;
import org.monyhar.content_public.browser.InterfaceRegistrar;
import org.monyhar.content_public.browser.RenderFrameHost;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.content_public.browser.trusttokens.TrustTokenFulfillerManager;
import org.monyhar.media.mojom.AndroidOverlayProvider;
import org.monyhar.mojo.system.impl.CoreImpl;
import org.monyhar.services.service_manager.InterfaceRegistry;

@JNINamespace("content")
class InterfaceRegistrarImpl {
    private static boolean sHasRegisteredRegistrars;

    @CalledByNative
    static void createInterfaceRegistry(int nativeHandle) {
        ensureSingletonRegistrarsAreRegistered();

        InterfaceRegistry registry = InterfaceRegistry.create(
                CoreImpl.getInstance().acquireNativeHandle(nativeHandle).toMessagePipeHandle());
        InterfaceRegistrar.Registry.applySingletonRegistrars(registry);
    }

    @CalledByNative
    static void createInterfaceRegistryForWebContents(int nativeHandle, WebContents webContents) {
        ensureSingletonRegistrarsAreRegistered();

        InterfaceRegistry registry = InterfaceRegistry.create(
                CoreImpl.getInstance().acquireNativeHandle(nativeHandle).toMessagePipeHandle());
        InterfaceRegistrar.Registry.applyWebContentsRegistrars(registry, webContents);
    }

    @CalledByNative
    static void createInterfaceRegistryForRenderFrameHost(
            int nativeHandle, RenderFrameHost renderFrameHost) {
        ensureSingletonRegistrarsAreRegistered();

        InterfaceRegistry registry = InterfaceRegistry.create(
                CoreImpl.getInstance().acquireNativeHandle(nativeHandle).toMessagePipeHandle());
        InterfaceRegistrar.Registry.applyRenderFrameHostRegistrars(registry, renderFrameHost);
    }

    private static void ensureSingletonRegistrarsAreRegistered() {
        if (sHasRegisteredRegistrars) return;
        sHasRegisteredRegistrars = true;
        InterfaceRegistrar.Registry.addSingletonRegistrar(new SingletonInterfaceRegistrar());
    }

    private static class SingletonInterfaceRegistrar implements InterfaceRegistrar<Void> {
        @Override
        public void registerInterfaces(InterfaceRegistry registry, Void v) {
            registry.addInterface(
                    AndroidOverlayProvider.MANAGER, new AndroidOverlayProviderImpl.Factory());
            // TODO(avayvod): Register the PresentationService implementation here.
            registry.addInterface(AndroidFontLookup.MANAGER, new AndroidFontLookupImpl.Factory());
            registry.addInterface(
                    LocalTrustTokenFulfiller.MANAGER, () -> TrustTokenFulfillerManager.create());
        }
    }
}
