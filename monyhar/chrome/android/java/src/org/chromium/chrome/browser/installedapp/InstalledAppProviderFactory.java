// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.installedapp;

import org.monyhar.chrome.browser.instantapps.InstantAppsHandler;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.components.installedapp.InstalledAppProviderImpl;
import org.monyhar.content_public.browser.RenderFrameHost;
import org.monyhar.content_public.browser.WebContentsStatics;
import org.monyhar.installedapp.mojom.InstalledAppProvider;
import org.monyhar.services.service_manager.InterfaceFactory;

/** Factory to create instances of the InstalledAppProvider Mojo service. */
public class InstalledAppProviderFactory implements InterfaceFactory<InstalledAppProvider> {
    private final RenderFrameHost mRenderFrameHost;

    public InstalledAppProviderFactory(RenderFrameHost renderFrameHost) {
        mRenderFrameHost = renderFrameHost;
    }

    @Override
    public InstalledAppProvider createImpl() {
        return new InstalledAppProviderImpl(
                Profile.fromWebContents(WebContentsStatics.fromRenderFrameHost(mRenderFrameHost)),
                mRenderFrameHost, InstantAppsHandler.getInstance()::isInstantAppAvailable);
    }
}
