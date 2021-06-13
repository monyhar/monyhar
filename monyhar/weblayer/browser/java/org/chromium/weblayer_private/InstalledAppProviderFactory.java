// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer_private;

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
        TabImpl tab =
                TabImpl.fromWebContents(WebContentsStatics.fromRenderFrameHost(mRenderFrameHost));
        if (tab == null) return null;
        return new InstalledAppProviderImpl(
                tab.getProfile(), mRenderFrameHost, (unusedA, unusedB, unusedC) -> false);
    }
}
