// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.browserservices.digitalgoods;

import android.app.Activity;

import androidx.annotation.VisibleForTesting;

import org.monyhar.chrome.browser.ActivityUtils;
import org.monyhar.chrome.browser.ChromeApplicationImpl;
import org.monyhar.chrome.browser.customtabs.CustomTabActivity;
import org.monyhar.components.payments.MethodStrings;
import org.monyhar.components.payments.PaymentFeatureList;
import org.monyhar.content_public.browser.RenderFrameHost;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.content_public.browser.WebContentsStatics;
import org.monyhar.mojo.system.MojoException;
import org.monyhar.payments.mojom.CreateDigitalGoodsResponseCode;
import org.monyhar.payments.mojom.DigitalGoods;
import org.monyhar.payments.mojom.DigitalGoodsFactory;
import org.monyhar.payments.mojom.DigitalGoodsFactory.CreateDigitalGoodsResponse;

/**
 * An implementation of the mojo {@link DigitalGoodsFactory} interface.
 */
public class DigitalGoodsFactoryImpl implements DigitalGoodsFactory {
    private static DigitalGoods sImplForTesting;

    private final RenderFrameHost mRenderFrameHost;
    private final DigitalGoodsImpl.Delegate mDigitalGoodsDelegate;
    private final DigitalGoodsAdapter mAdapter;

    @VisibleForTesting
    public static void setDigitalGoodsForTesting(DigitalGoods impl) {
        sImplForTesting = impl;
    }

    public DigitalGoodsFactoryImpl(RenderFrameHost renderFrameHost) {
        mRenderFrameHost = renderFrameHost;
        mDigitalGoodsDelegate = mRenderFrameHost::getLastCommittedURL;
        mAdapter = new DigitalGoodsAdapter(
                ChromeApplicationImpl.getComponent().resolveTrustedWebActivityClient());
    }

    private int getResponseCode(String paymentMethod) {
        if (!PaymentFeatureList.isEnabled(PaymentFeatureList.WEB_PAYMENTS_APP_STORE_BILLING)) {
            return CreateDigitalGoodsResponseCode.UNSUPPORTED_CONTEXT;
        }

        // Ensure that the DigitalGoodsImpl is only created if we're in a TWA and on its verified
        // origin.
        WebContents wc = WebContentsStatics.fromRenderFrameHost(mRenderFrameHost);
        Activity activity = ActivityUtils.getActivityFromWebContents(wc);
        if (!(activity instanceof CustomTabActivity)) {
            return CreateDigitalGoodsResponseCode.UNSUPPORTED_CONTEXT;
        }
        CustomTabActivity cta = (CustomTabActivity) activity;
        if (!cta.isInTwaMode()) {
            return CreateDigitalGoodsResponseCode.UNSUPPORTED_CONTEXT;
        }

        if (!MethodStrings.GOOGLE_PLAY_BILLING.equals(paymentMethod)) {
            return CreateDigitalGoodsResponseCode.UNSUPPORTED_PAYMENT_METHOD;
        }

        // TODO(peconn): Add a test for this.

        return CreateDigitalGoodsResponseCode.OK;
    }

    @Override
    public void createDigitalGoods(String paymentMethod, CreateDigitalGoodsResponse callback) {
        if (sImplForTesting != null) {
            callback.call(CreateDigitalGoodsResponseCode.OK, sImplForTesting);
            return;
        }

        // If the user is making Digital Goods payments, this is a good hint that we should enable
        // site isolation for the site.
        SiteIsolator.startIsolatingSite(mDigitalGoodsDelegate.getUrl());

        int code = getResponseCode(paymentMethod);
        CreateDigitalGoodsResponseCode.validate(code);
        if (code == CreateDigitalGoodsResponseCode.OK) {
            callback.call(code, new DigitalGoodsImpl(mAdapter, mDigitalGoodsDelegate));
        } else {
            callback.call(code, null);
        }
    }

    @Override
    public void close() {}

    @Override
    public void onConnectionError(MojoException e) {}
}
