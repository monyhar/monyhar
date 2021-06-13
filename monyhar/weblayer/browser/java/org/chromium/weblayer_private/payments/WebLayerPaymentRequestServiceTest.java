// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer_private.payments;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.robolectric.annotation.Config;

import org.monyhar.base.test.BaseRobolectricTestRunner;
import org.monyhar.base.test.util.Feature;
import org.monyhar.base.test.util.JniMocker;
import org.monyhar.components.payments.ErrorMessageUtil;
import org.monyhar.components.payments.ErrorMessageUtilJni;
import org.monyhar.components.payments.PayerData;
import org.monyhar.components.payments.PaymentApp;
import org.monyhar.components.payments.PaymentApp.InstrumentDetailsCallback;
import org.monyhar.components.payments.PaymentAppFactoryDelegate;
import org.monyhar.components.payments.PaymentAppFactoryInterface;
import org.monyhar.components.payments.PaymentAppService;
import org.monyhar.components.payments.PaymentFeatureList;
import org.monyhar.components.payments.PaymentRequestService;
import org.monyhar.components.payments.test_support.ShadowPaymentFeatureList;
import org.monyhar.payments.mojom.PaymentRequest;
import org.monyhar.payments.mojom.PaymentRequestClient;
import org.monyhar.payments.mojom.PaymentResponse;
import org.monyhar.weblayer_private.payments.test_support.WebLayerPaymentRequestBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A test for the integration of PaymentRequestService, MojoPaymentRequestGateKeeper,
 * WebLayerPaymentRequestService and PaymentAppService.
 */
@RunWith(BaseRobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = {ShadowPaymentFeatureList.class})
public class WebLayerPaymentRequestServiceTest {
    private static final String METHOD_NAME = "https://www.monyhar.org";
    private static final String STRINGIFIED_DETAILS = "test stringifiedDetails";
    private final ArgumentCaptor<InstrumentDetailsCallback> mPaymentAppCallbackCaptor =
            ArgumentCaptor.forClass(InstrumentDetailsCallback.class);

    @Rule
    public MockitoRule mMockitoRule = MockitoJUnit.rule().strictness(Strictness.WARN);

    @Rule
    public JniMocker mJniMocker = new JniMocker();

    @Mock
    private ErrorMessageUtil.Natives mErrorMessageUtilMock;

    private PaymentRequestClient mClient;
    private PaymentAppFactoryInterface mFactory;
    private PaymentApp mPaymentApp;
    private boolean mIsUserGesture;
    private boolean mWaitForUpdatedDetails;

    @Before
    public void setUp() {
        mJniMocker.mock(ErrorMessageUtilJni.TEST_HOOKS, mErrorMessageUtilMock);
        Mockito.doAnswer(args -> {
                   String[] methods = args.getArgument(0);
                   return "(Mock) Not supported error: " + Arrays.toString(methods);
               })
                .when(mErrorMessageUtilMock)
                .getNotSupportedErrorMessage(Mockito.any());

        ShadowPaymentFeatureList.setFeatureEnabled(
                PaymentFeatureList.WEB_PAYMENTS_SINGLE_APP_UI_SKIP, true);
        PaymentRequestService.resetShowingPaymentRequestForTest();
        PaymentAppService.getInstance().resetForTest();

        mClient = Mockito.mock(PaymentRequestClient.class);
        mPaymentApp = mockPaymentApp();
        mFactory = Mockito.mock(PaymentAppFactoryInterface.class);
        Mockito.doAnswer((args) -> {
                   PaymentAppFactoryDelegate delegate = args.getArgument(0);
                   delegate.onCanMakePaymentCalculated(true);
                   delegate.onPaymentAppCreated(mPaymentApp);
                   delegate.onDoneCreatingPaymentApps(mFactory);
                   return null;
               })
                .when(mFactory)
                .create(Mockito.any());
    }

    @After
    public void tearDown() {
        PaymentRequestService.resetShowingPaymentRequestForTest();
        PaymentAppService.getInstance().resetForTest();
    }

    private PaymentApp mockPaymentApp() {
        PaymentApp app = Mockito.mock(PaymentApp.class);
        Set<String> methodNames = new HashSet<>();
        methodNames.add(METHOD_NAME);
        Mockito.doReturn(methodNames).when(app).getInstrumentMethodNames();
        Mockito.doReturn("testPaymentApp").when(app).getIdentifier();
        Mockito.doReturn(true).when(app).handlesShippingAddress();
        return app;
    }

    private WebLayerPaymentRequestBuilder defaultBuilder() {
        WebLayerPaymentRequestBuilder builder =
                WebLayerPaymentRequestBuilder.defaultBuilder(mClient);
        PaymentAppService.getInstance().addUniqueFactory(mFactory, "testFactoryId");
        return builder;
    }

    private void show(PaymentRequest request) {
        request.show(mIsUserGesture, mWaitForUpdatedDetails);
    }

    private void assertNoError() {
        Mockito.verify(mClient, Mockito.never()).onError(Mockito.anyInt(), Mockito.anyString());
    }

    private void assertResponse() {
        ArgumentCaptor<PaymentResponse> responseCaptor =
                ArgumentCaptor.forClass(PaymentResponse.class);
        Mockito.verify(mClient, Mockito.times(1)).onPaymentResponse(responseCaptor.capture());
        PaymentResponse response = responseCaptor.getValue();
        Assert.assertNotNull(response);
        Assert.assertEquals(METHOD_NAME, response.methodName);
        Assert.assertEquals(STRINGIFIED_DETAILS, response.stringifiedDetails);
    }

    private void assertInvokePaymentAppCalled() {
        Mockito.verify(mPaymentApp, Mockito.times(1))
                .invokePaymentApp(Mockito.any(), Mockito.any(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(),
                        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                        mPaymentAppCallbackCaptor.capture());
    }

    private void simulatePaymentAppRespond() {
        mPaymentAppCallbackCaptor.getValue().onInstrumentDetailsReady(
                METHOD_NAME, STRINGIFIED_DETAILS, new PayerData());
    }

    @Test
    @Feature({"Payments"})
    public void testPaymentIsSuccessful() {
        PaymentRequest request = defaultBuilder().buildAndInit();
        Assert.assertNotNull(request);
        assertNoError();

        show(request);
        assertNoError();
        assertInvokePaymentAppCalled();

        simulatePaymentAppRespond();
        assertResponse();
    }
}
