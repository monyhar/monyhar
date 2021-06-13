// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.payments;

import androidx.test.filters.MediumTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.autofill.AutofillTestHelper;
import org.monyhar.chrome.browser.autofill.PersonalDataManager.CreditCard;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.MainActivityStartCallback;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;

import java.util.concurrent.TimeoutException;

/**
 * A payment integration test for checking whether user can make a payment via a credit card.
 * This user does not have a complete credit card on file.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class PaymentRequestCcCanMakePaymentQueryNoCardTest implements MainActivityStartCallback {
    @Rule
    public PaymentRequestTestRule mPaymentRequestTestRule =
            new PaymentRequestTestRule("payment_request_can_make_payment_query_cc_test.html", this);

    @Override
    public void onMainActivityStarted() throws TimeoutException {
        // The user has an incomplete credit card on file. This is not sufficient for
        // canMakePayment() to return true.
        new AutofillTestHelper().setCreditCard(new CreditCard("", "https://example.com", true, true,
                "" /* nameOnCard */, "4111111111111111", "1111", "12", "2050", "visa",
                R.drawable.visa_card, "" /* billingAddressId */, "" /* serverId */));
    }

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testCanMakePayment() throws TimeoutException {
        mPaymentRequestTestRule.openPageAndClickBuyAndWait(
                mPaymentRequestTestRule.getCanMakePaymentQueryResponded());
        mPaymentRequestTestRule.expectResultContains(new String[] {"true"});
    }

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testHasEnrolledInstrument() throws TimeoutException {
        mPaymentRequestTestRule.openPageAndClickNodeAndWait("has-enrolled-instrument-visa",
                mPaymentRequestTestRule.getHasEnrolledInstrumentQueryResponded());
        mPaymentRequestTestRule.expectResultContains(new String[] {"false"});
    }
}
