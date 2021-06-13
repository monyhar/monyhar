// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.browserservices.digitalgoods;

import org.monyhar.content_public.browser.RenderFrameHost;
import org.monyhar.payments.mojom.DigitalGoodsFactory;
import org.monyhar.services.service_manager.InterfaceFactory;

/**
 * A factory to produce instances of the mojo {@link DigitalGoodsFactory} interface.
 */
public class DigitalGoodsFactoryFactory implements InterfaceFactory<DigitalGoodsFactory> {
    private final RenderFrameHost mRenderFrameHost;

    public DigitalGoodsFactoryFactory(RenderFrameHost renderFrameHost) {
        mRenderFrameHost = renderFrameHost;
    }

    @Override
    public DigitalGoodsFactory createImpl() {
        return new DigitalGoodsFactoryImpl(mRenderFrameHost);
    }
}
