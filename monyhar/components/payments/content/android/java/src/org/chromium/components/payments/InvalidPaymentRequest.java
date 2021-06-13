// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.payments;

import org.monyhar.mojo.system.MojoException;
import org.monyhar.payments.mojom.CanMakePaymentQueryResult;
import org.monyhar.payments.mojom.HasEnrolledInstrumentQueryResult;
import org.monyhar.payments.mojom.PaymentDetails;
import org.monyhar.payments.mojom.PaymentErrorReason;
import org.monyhar.payments.mojom.PaymentMethodData;
import org.monyhar.payments.mojom.PaymentOptions;
import org.monyhar.payments.mojom.PaymentRequest;
import org.monyhar.payments.mojom.PaymentRequestClient;
import org.monyhar.payments.mojom.PaymentValidationErrors;

/**
 * An implementation of PaymentRequest that immediately rejects all connections.
 * Necessary because Mojo does not handle null returned from createImpl().
 */
public final class InvalidPaymentRequest implements PaymentRequest {
    private PaymentRequestClient mClient;

    @Override
    public void init(PaymentRequestClient client, PaymentMethodData[] unusedMethodData,
            PaymentDetails unusedDetails, PaymentOptions unusedOptions,
            boolean unusedGooglePayBridgeEligible) {
        mClient = client;
    }

    @Override
    public void show(boolean unusedIsUserGesture, boolean unusedWaitForUpdatedDetails) {
        if (mClient != null) {
            mClient.onError(PaymentErrorReason.USER_CANCEL, ErrorStrings.WEB_PAYMENT_API_DISABLED);
            mClient.close();
        }
    }

    @Override
    public void updateWith(PaymentDetails unusedDetails) {}

    @Override
    public void onPaymentDetailsNotUpdated() {}

    @Override
    public void abort() {}

    @Override
    public void complete(int unusedResult) {}

    @Override
    public void retry(PaymentValidationErrors unusedErrors) {}

    @Override
    public void canMakePayment() {
        if (mClient != null) {
            mClient.onCanMakePayment(CanMakePaymentQueryResult.CANNOT_MAKE_PAYMENT);
        }
    }

    @Override
    public void hasEnrolledInstrument() {
        if (mClient != null) {
            mClient.onHasEnrolledInstrument(
                    HasEnrolledInstrumentQueryResult.HAS_NO_ENROLLED_INSTRUMENT);
        }
    }

    @Override
    public void close() {}

    @Override
    public void onConnectionError(MojoException e) {}
}
