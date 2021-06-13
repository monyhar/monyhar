// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.payments;

import androidx.test.filters.MediumTest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.metrics.RecordHistogram;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.autofill.AutofillTestHelper;
import org.monyhar.chrome.browser.autofill.PersonalDataManager.AutofillProfile;
import org.monyhar.chrome.browser.autofill.PersonalDataManager.CreditCard;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.MainActivityStartCallback;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.components.payments.NotShownReason;
import org.monyhar.ui.test.util.DisableAnimationsTestRule;

import java.util.concurrent.TimeoutException;

/**
 * A payment integration test for a merchant that calls show() twice.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class PaymentRequestShowTwiceTest implements MainActivityStartCallback {
    // Disable animations to reduce flakiness.
    @ClassRule
    public static DisableAnimationsTestRule sNoAnimationsRule = new DisableAnimationsTestRule();

    @Rule
    public PaymentRequestTestRule mPaymentRequestTestRule =
            new PaymentRequestTestRule("payment_request_show_twice_test.html", this);

    @Override
    public void onMainActivityStarted() throws TimeoutException {
        AutofillTestHelper helper = new AutofillTestHelper();
        String billingAddressId = helper.setProfile(new AutofillProfile("", "https://example.com",
                true, "" /* honorific prefix */, "Jon Doe", "Google", "340 Main St", "CA",
                "Los Angeles", "", "90291", "", "US", "555-555-5555", "", "en-US"));
        helper.setCreditCard(new CreditCard("", "https://example.com", true, true, "Jon Doe",
                "4111111111111111", "1111", "12", "2050", "visa", R.drawable.visa_card,
                billingAddressId, "" /* serverId */));
    }

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testSecondShowRequestCancelled() throws TimeoutException {
        mPaymentRequestTestRule.triggerUIAndWait(mPaymentRequestTestRule.getReadyToPay());
        mPaymentRequestTestRule.expectResultContains(new String[] {
                "Second request: AbortError: Another PaymentRequest UI is already showing in a "
                + "different tab or window"});

        // The web payments UI was not aborted.
        mPaymentRequestTestRule.assertOnlySpecificAbortMetricLogged(-1 /* none */);

        // The UI was never shown due to another web payments UI already showing.
        Assert.assertEquals(1,
                RecordHistogram.getHistogramValueCountForTesting(
                        "PaymentRequest.CheckoutFunnel.NoShow",
                        NotShownReason.CONCURRENT_REQUESTS));

        mPaymentRequestTestRule.clickAndWait(
                R.id.close_button, mPaymentRequestTestRule.getDismissed());
    }
}
