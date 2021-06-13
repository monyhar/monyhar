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

import org.monyhar.base.metrics.RecordHistogram;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.AppPresence;
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.AppSpeed;
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.FactorySpeed;
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.MainActivityStartCallback;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.ui.test.util.DisableAnimationsTestRule;

import java.util.concurrent.TimeoutException;

/**
 * A payment integration test for the show promise with digital goods.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class PaymentRequestShowPromiseDigitalGoodsTest implements MainActivityStartCallback {
    // Disable animations to reduce flakiness.
    @ClassRule
    public static DisableAnimationsTestRule sNoAnimationsRule = new DisableAnimationsTestRule();

    @Rule
    public PaymentRequestTestRule mRule =
            new PaymentRequestTestRule("show_promise/digital_goods.html", this);

    @Override
    public void onMainActivityStarted() {}

    // The initial total in digital_goods.js is 99.99 while the final total is 1.00. Transaction
    // amount metrics must record the final total rather than the initial one. The final total falls
    // into the micro transaction category.
    private static final int sMicroTransaction = 1;

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testDigitalGoodsFastApp() throws TimeoutException {
        mRule.addPaymentAppFactory("basic-card", AppPresence.HAVE_APPS, FactorySpeed.FAST_FACTORY);
        mRule.openPage();
        mRule.executeJavaScriptAndWaitForResult("create('basic-card');");
        mRule.triggerUIAndWait(mRule.getReadyToPay());

        Assert.assertEquals("USD $1.00", mRule.getOrderSummaryTotal());

        mRule.clickAndWait(R.id.button_primary, mRule.getDismissed());

        mRule.expectResultContains(new String[] {"\"total\":\"1.00\""});
        Assert.assertEquals(1,
                RecordHistogram.getHistogramValueCountForTesting(
                        "PaymentRequest.TransactionAmount.Triggered", sMicroTransaction));
        Assert.assertEquals(1,
                RecordHistogram.getHistogramValueCountForTesting(
                        "PaymentRequest.TransactionAmount.Completed", sMicroTransaction));
    }

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testDigitalGoodsSlowApp() throws TimeoutException {
        mRule.addPaymentAppFactory(
                "basic-card", AppPresence.HAVE_APPS, FactorySpeed.SLOW_FACTORY, AppSpeed.SLOW_APP);
        mRule.openPage();
        mRule.executeJavaScriptAndWaitForResult("create('basic-card');");
        mRule.triggerUIAndWait(mRule.getReadyToPay());

        Assert.assertEquals("USD $1.00", mRule.getOrderSummaryTotal());

        mRule.clickAndWait(R.id.button_primary, mRule.getDismissed());

        mRule.expectResultContains(new String[] {"\"total\":\"1.00\""});
        Assert.assertEquals(1,
                RecordHistogram.getHistogramValueCountForTesting(
                        "PaymentRequest.TransactionAmount.Triggered", sMicroTransaction));
        Assert.assertEquals(1,
                RecordHistogram.getHistogramValueCountForTesting(
                        "PaymentRequest.TransactionAmount.Completed", sMicroTransaction));
    }

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testSkipUIFastApp() throws TimeoutException {
        mRule.addPaymentAppFactory("basic-card", AppPresence.HAVE_APPS, FactorySpeed.FAST_FACTORY);
        mRule.openPage();
        mRule.executeJavaScriptAndWaitForResult("create('basic-card');");
        mRule.enableSkipUIForBasicCard();

        mRule.openPageAndClickNodeAndWait("buy", mRule.getDismissed());

        mRule.expectResultContains(new String[] {"\"total\":\"1.00\""});
    }

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testSkipUISlowApp() throws TimeoutException {
        mRule.addPaymentAppFactory(
                "basic-card", AppPresence.HAVE_APPS, FactorySpeed.SLOW_FACTORY, AppSpeed.SLOW_APP);
        mRule.openPage();
        mRule.executeJavaScriptAndWaitForResult("create('basic-card');");
        mRule.enableSkipUIForBasicCard();

        mRule.openPageAndClickNodeAndWait("buy", mRule.getDismissed());

        mRule.expectResultContains(new String[] {"\"total\":\"1.00\""});
    }
}
