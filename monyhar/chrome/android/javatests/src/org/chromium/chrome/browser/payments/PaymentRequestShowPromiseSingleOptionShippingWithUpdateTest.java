// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.payments;

import androidx.test.filters.MediumTest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.autofill.AutofillTestHelper;
import org.monyhar.chrome.browser.autofill.PersonalDataManager.AutofillProfile;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.AppPresence;
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.AppSpeed;
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.FactorySpeed;
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.MainActivityStartCallback;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.ui.test.util.DisableAnimationsTestRule;

import java.util.concurrent.TimeoutException;

/**
 * A payment integration test for the show promise with a single pre-selected shipping option and a
 * shipping address change handler.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class PaymentRequestShowPromiseSingleOptionShippingWithUpdateTest
        implements MainActivityStartCallback {
    // Disable animations to reduce flakiness.
    @ClassRule
    public static DisableAnimationsTestRule sNoAnimationsRule = new DisableAnimationsTestRule();

    @Rule
    public PaymentRequestTestRule mRule = new PaymentRequestTestRule(
            "show_promise/single_option_shipping_with_update.html", this);

    @Override
    public void onMainActivityStarted() throws TimeoutException {
        AutofillTestHelper autofillTestHelper = new AutofillTestHelper();
        autofillTestHelper.setProfile(new AutofillProfile("", "https://example.com", true,
                "" /* honorific prefix */, "Jon Doe", "Google", "340 Main St", "California",
                "Los Angeles", "", "90291", "", "US", "555-222-2222", "", "en-US"));
        autofillTestHelper.setProfile(new AutofillProfile("", "https://example.com", true,
                "" /* honorific prefix */, "Jane Smith", "Google", "340 Main St", "California",
                "Los Angeles", "", "90291", "", "US", "555-111-1111", "", "en-US"));
    }

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testFastApp() throws TimeoutException {
        mRule.addPaymentAppFactory("basic-card", AppPresence.HAVE_APPS, FactorySpeed.FAST_FACTORY);
        mRule.triggerUIAndWait(mRule.getReadyToPay());
        Assert.assertEquals("USD $1.00", mRule.getOrderSummaryTotal());
        Assert.assertEquals("$0.00", mRule.getShippingOptionCostSummaryOnBottomSheet());
        mRule.clickInShippingAddressAndWait(R.id.payments_section, mRule.getReadyForInput());
        mRule.clickOnShippingAddressSuggestionOptionAndWait(1, mRule.getReadyForInput());
        mRule.clickAndWait(R.id.button_primary, mRule.getDismissed());
        mRule.expectResultContains(new String[] {"\"total\":\"1.00\""});
    }

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testSlowApp() throws TimeoutException {
        mRule.addPaymentAppFactory(
                "basic-card", AppPresence.HAVE_APPS, FactorySpeed.SLOW_FACTORY, AppSpeed.SLOW_APP);
        mRule.triggerUIAndWait(mRule.getReadyToPay());
        Assert.assertEquals("USD $1.00", mRule.getOrderSummaryTotal());
        Assert.assertEquals("$0.00", mRule.getShippingOptionCostSummaryOnBottomSheet());
        mRule.clickInShippingAddressAndWait(R.id.payments_section, mRule.getReadyForInput());
        mRule.clickOnShippingAddressSuggestionOptionAndWait(1, mRule.getReadyForInput());
        mRule.clickAndWait(R.id.button_primary, mRule.getDismissed());
        mRule.expectResultContains(new String[] {"\"total\":\"1.00\""});
    }
}
