// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer_private.payments.test_support;

import android.content.Context;

import androidx.annotation.Nullable;

import org.mockito.Mockito;

import org.monyhar.components.payments.BrowserPaymentRequest;
import org.monyhar.components.payments.JourneyLogger;
import org.monyhar.components.payments.MojoPaymentRequestGateKeeper;
import org.monyhar.components.payments.PaymentAppFactoryInterface;
import org.monyhar.components.payments.PaymentRequestService;
import org.monyhar.components.payments.PaymentRequestSpec;
import org.monyhar.content_public.browser.RenderFrameHost;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.payments.mojom.PaymentCurrencyAmount;
import org.monyhar.payments.mojom.PaymentDetails;
import org.monyhar.payments.mojom.PaymentItem;
import org.monyhar.payments.mojom.PaymentMethodData;
import org.monyhar.payments.mojom.PaymentOptions;
import org.monyhar.payments.mojom.PaymentRequest;
import org.monyhar.payments.mojom.PaymentRequestClient;
import org.monyhar.ui.base.WindowAndroid;
import org.monyhar.url.GURL;
import org.monyhar.url.JUnitTestGURLs;
import org.monyhar.url.Origin;
import org.monyhar.weblayer_private.payments.WebLayerPaymentRequestService;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** The builder of PaymentRequest used in WebLayer, for testing purpose only. */
public class WebLayerPaymentRequestBuilder implements PaymentRequestService.Delegate {
    private final PaymentRequestClient mClient;
    private final PaymentRequestService.Delegate mDelegate;
    private final RenderFrameHost mRenderFrameHost;
    private final PaymentMethodData[] mMethodData;
    private final PaymentDetails mDetails;
    private final WebContents mWebContents;
    private final JourneyLogger mJourneyLogger;
    private final PaymentRequestSpec mSpec;
    private final boolean mGoogleBridgeEligible;
    private final PaymentOptions mOptions;
    private String mSupportedMethod = "https://www.monyhar.org";

    /**
     * Create a default builder.
     * @param client The PaymentRequestClient used for this PaymentRequest.
     * @return The created builder.
     */
    public static WebLayerPaymentRequestBuilder defaultBuilder(PaymentRequestClient client) {
        return new WebLayerPaymentRequestBuilder(client);
    }

    private WebLayerPaymentRequestBuilder(PaymentRequestClient client) {
        mClient = client;
        mDelegate = this;
        mJourneyLogger = Mockito.mock(JourneyLogger.class);
        mWebContents = Mockito.mock(WebContents.class);
        Mockito.doReturn(JUnitTestGURLs.getGURL(JUnitTestGURLs.URL_1))
                .when(mWebContents)
                .getLastCommittedUrl();
        mRenderFrameHost = Mockito.mock(RenderFrameHost.class);
        Mockito.doReturn(JUnitTestGURLs.getGURL(JUnitTestGURLs.URL_2))
                .when(mRenderFrameHost)
                .getLastCommittedURL();
        Origin origin = Mockito.mock(Origin.class);
        Mockito.doReturn(origin).when(mRenderFrameHost).getLastCommittedOrigin();
        mMethodData = new PaymentMethodData[1];
        mDetails = new PaymentDetails();
        mDetails.id = "testId";
        mDetails.total = new PaymentItem();
        mOptions = new PaymentOptions();
        mSpec = Mockito.mock(PaymentRequestSpec.class);
        mGoogleBridgeEligible = false;
    }

    /**
     * Build PaymentRequest and calls its init().
     * @return The built and initialized PaymentRequest.
     */
    public PaymentRequest buildAndInit() {
        mMethodData[0] = new PaymentMethodData();
        mMethodData[0].supportedMethod = mSupportedMethod;

        PaymentCurrencyAmount amount = new PaymentCurrencyAmount();
        amount.currency = "CNY";
        amount.value = "123";
        PaymentItem total = new PaymentItem();
        total.amount = amount;
        Mockito.doReturn(total).when(mSpec).getRawTotal();
        Map<String, PaymentMethodData> methodDataMap = new HashMap<>();
        methodDataMap.put(mMethodData[0].supportedMethod, mMethodData[0]);
        Mockito.doReturn(methodDataMap).when(mSpec).getMethodData();
        Mockito.doReturn(mOptions).when(mSpec).getPaymentOptions();

        PaymentRequest request = new MojoPaymentRequestGateKeeper(
                (client, onClosed)
                        -> new PaymentRequestService(mRenderFrameHost, client, onClosed, this));
        request.init(mClient, mMethodData, mDetails, mOptions, mGoogleBridgeEligible);
        return request;
    }

    /**
     * Sets the method supported by this payment request (currently, only one method is supported).
     * @param supportedMethod The supported method.
     * @return The builder after the setting.
     */
    public WebLayerPaymentRequestBuilder setSupportedMethod(String supportedMethod) {
        mSupportedMethod = supportedMethod;
        return this;
    }

    @Override
    public BrowserPaymentRequest createBrowserPaymentRequest(
            PaymentRequestService paymentRequestService) {
        return new WebLayerPaymentRequestService(paymentRequestService, mDelegate);
    }

    @Override
    public boolean isOffTheRecord() {
        return false;
    }

    @Override
    public String getInvalidSslCertificateErrorMessage() {
        return null;
    }

    @Override
    public boolean prefsCanMakePayment() {
        return false;
    }

    @Nullable
    @Override
    public String getTwaPackageName() {
        return null;
    }

    @Nullable
    @Override
    public WebContents getLiveWebContents(RenderFrameHost renderFrameHost) {
        return mWebContents;
    }

    @Override
    public boolean isOriginSecure(GURL url) {
        return true;
    }

    @Override
    public JourneyLogger createJourneyLogger(boolean isIncognito, WebContents webContents) {
        return mJourneyLogger;
    }

    @Override
    public String formatUrlForSecurityDisplay(GURL uri) {
        return uri.getSpec();
    }

    @Override
    public byte[][] getCertificateChain(WebContents webContents) {
        return new byte[0][];
    }

    @Override
    public boolean isOriginAllowedToUseWebPaymentApis(GURL url) {
        return true;
    }

    @Override
    public boolean validatePaymentDetails(PaymentDetails details) {
        return true;
    }

    @Override
    public PaymentRequestSpec createPaymentRequestSpec(PaymentOptions options,
            PaymentDetails details, Collection<PaymentMethodData> methodData, String appLocale) {
        return mSpec;
    }

    @Override
    public WindowAndroid getWindowAndroid(RenderFrameHost renderFrameHost) {
        WindowAndroid window = Mockito.mock(WindowAndroid.class);
        Context context = Mockito.mock(Context.class);
        WeakReference<Context> weakContext = Mockito.mock(WeakReference.class);
        Mockito.doReturn(context).when(weakContext).get();
        Mockito.doReturn(weakContext).when(window).getContext();
        return window;
    }

    @Override
    public PaymentAppFactoryInterface createAndroidPaymentAppFactory() {
        return null;
    }
}
