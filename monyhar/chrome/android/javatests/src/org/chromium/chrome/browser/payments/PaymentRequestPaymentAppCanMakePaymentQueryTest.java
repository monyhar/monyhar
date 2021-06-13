// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.payments;

import androidx.test.filters.MediumTest;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.AppPresence;
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.FactorySpeed;
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.MainActivityStartCallback;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.ui.test.util.DisableAnimationsTestRule;

import java.util.concurrent.TimeoutException;

/**
 * A payment integration test for checking whether user can make a payment using a payment app.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class PaymentRequestPaymentAppCanMakePaymentQueryTest implements MainActivityStartCallback {
    // Disable animations to reduce flakiness.
    @ClassRule
    public static DisableAnimationsTestRule sNoAnimationsRule = new DisableAnimationsTestRule();

    @Rule
    public PaymentRequestTestRule mPaymentRequestTestRule = new PaymentRequestTestRule(
            "payment_request_can_make_payment_query_bobpay_test.html", this);

    @Override
    public void onMainActivityStarted() {}

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testBobPayInstalledLater() throws InterruptedException, TimeoutException {
        // hasEnrolledInstrument returns false, since BobPay is not installed.
        mPaymentRequestTestRule.openPageAndClickNodeAndWait("hasEnrolledInstrument",
                mPaymentRequestTestRule.getHasEnrolledInstrumentQueryResponded());
        mPaymentRequestTestRule.expectResultContains(new String[] {"false, false"});

        mPaymentRequestTestRule.addPaymentAppFactory(
                AppPresence.HAVE_APPS, FactorySpeed.FAST_FACTORY);
        Thread.sleep(10000);

        // hasEnrolledInstrument returns true now for BobPay, but still returns false for AlicePay.
        mPaymentRequestTestRule.clickNodeAndWait("hasEnrolledInstrument",
                mPaymentRequestTestRule.getHasEnrolledInstrumentQueryResponded());
        mPaymentRequestTestRule.expectResultContains(new String[] {"true, false"});
    }

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testNoAppsInFastBobPayFactory() throws TimeoutException {
        mPaymentRequestTestRule.addPaymentAppFactory(
                AppPresence.NO_APPS, FactorySpeed.FAST_FACTORY);

        // canMakePayment returns true for BobPay and false for AlicePay.
        mPaymentRequestTestRule.openPageAndClickNodeAndWait(
                "otherBuy", mPaymentRequestTestRule.getCanMakePaymentQueryResponded());
        mPaymentRequestTestRule.expectResultContains(new String[] {"true, false"});

        // hasEnrolledInstrument returns false for BobPay (installed but no instrument) and
        // false for AlicePay (not installed).
        mPaymentRequestTestRule.clickNodeAndWait("hasEnrolledInstrument",
                mPaymentRequestTestRule.getHasEnrolledInstrumentQueryResponded());
        mPaymentRequestTestRule.expectResultContains(new String[] {"false, false"});
    }

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testNoAppsInSlowBobPayFactory() throws TimeoutException {
        // Add BobPay factory.
        mPaymentRequestTestRule.addPaymentAppFactory(
                AppPresence.NO_APPS, FactorySpeed.SLOW_FACTORY);

        // canMakePayment returns true for BobPay and false for AlicePay.
        mPaymentRequestTestRule.openPageAndClickNodeAndWait(
                "otherBuy", mPaymentRequestTestRule.getCanMakePaymentQueryResponded());
        mPaymentRequestTestRule.expectResultContains(new String[] {"true, false"});

        // hasEnrolledInstrument returns false for BobPay (installed but no instrument) and
        // false for AlicePay (not installed).
        mPaymentRequestTestRule.clickNodeAndWait("hasEnrolledInstrument",
                mPaymentRequestTestRule.getHasEnrolledInstrumentQueryResponded());
        mPaymentRequestTestRule.expectResultContains(new String[] {"false, false"});
    }

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testPayViaFastBobPay() throws TimeoutException {
        // Install BobPay.
        mPaymentRequestTestRule.addPaymentAppFactory(
                AppPresence.HAVE_APPS, FactorySpeed.FAST_FACTORY);

        // canMakePayment returns true for BobPay and false for AlicePay.
        mPaymentRequestTestRule.openPageAndClickNodeAndWait(
                "otherBuy", mPaymentRequestTestRule.getCanMakePaymentQueryResponded());
        mPaymentRequestTestRule.expectResultContains(new String[] {"true, false"});

        // hasEnrolledInstrument returns true for BobPay and false for AlicePay.
        mPaymentRequestTestRule.clickNodeAndWait("hasEnrolledInstrument",
                mPaymentRequestTestRule.getHasEnrolledInstrumentQueryResponded());
        mPaymentRequestTestRule.expectResultContains(new String[] {"true, false"});
    }

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testPayViaSlowBobPayFactory() throws TimeoutException {
        // Install BobPay.
        mPaymentRequestTestRule.addPaymentAppFactory(
                AppPresence.HAVE_APPS, FactorySpeed.SLOW_FACTORY);

        // canMakePayment returns true for BobPay and false for AlicePay.
        mPaymentRequestTestRule.openPageAndClickNodeAndWait(
                "otherBuy", mPaymentRequestTestRule.getCanMakePaymentQueryResponded());
        mPaymentRequestTestRule.expectResultContains(new String[] {"true, false"});

        // hasEnrolledInstrument returns true for BobPay and false for AlicePay.
        mPaymentRequestTestRule.clickNodeAndWait("hasEnrolledInstrument",
                mPaymentRequestTestRule.getHasEnrolledInstrumentQueryResponded());
        mPaymentRequestTestRule.expectResultContains(new String[] {"true, false"});
    }
}
