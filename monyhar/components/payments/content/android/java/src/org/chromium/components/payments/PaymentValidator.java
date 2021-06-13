// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.payments;

import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.payments.mojom.PaymentDetails;
import org.monyhar.payments.mojom.PaymentValidationErrors;

import java.nio.ByteBuffer;

/**
 * Static class to represent a JNI interface to a C++ validation library.
 */
@JNINamespace("payments")
public class PaymentValidator {
    public static boolean validatePaymentDetails(PaymentDetails details) {
        if (details == null) {
            return false;
        }
        return PaymentValidatorJni.get().validatePaymentDetailsAndroid(details.serialize());
    }

    public static boolean validatePaymentValidationErrors(PaymentValidationErrors errors) {
        if (errors == null) {
            return false;
        }
        return PaymentValidatorJni.get().validatePaymentValidationErrorsAndroid(errors.serialize());
    }

    @NativeMethods
    interface Natives {
        boolean validatePaymentDetailsAndroid(ByteBuffer buffer);
        boolean validatePaymentValidationErrorsAndroid(ByteBuffer buffer);
    }
};
