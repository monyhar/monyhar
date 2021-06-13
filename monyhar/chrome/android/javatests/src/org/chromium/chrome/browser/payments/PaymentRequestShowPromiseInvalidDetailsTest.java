// Copyright 2019 The Chromium Authors. All rights reserved.
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
import org.monyhar.chrome.browser.payments.PaymentRequestTestRule.MainActivityStartCallback;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.ui.test.util.DisableAnimationsTestRule;

import java.util.concurrent.TimeoutException;

/**
 * A payment integration test for the show promise that resolves with invalid details.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class PaymentRequestShowPromiseInvalidDetailsTest implements MainActivityStartCallback {
    // Disable animations to reduce flakiness.
    @ClassRule
    public static DisableAnimationsTestRule sNoAnimationsRule = new DisableAnimationsTestRule();

    @Rule
    public PaymentRequestTestRule mRule =
            new PaymentRequestTestRule("show_promise/invalid_details.html", this);

    @Override
    public void onMainActivityStarted() {}

    @Test
    @MediumTest
    @Feature({"Payments"})
    public void testReject() throws TimeoutException {
        mRule.openPageAndClickNodeAndWait("buy", mRule.getRendererClosedMojoConnection());
        mRule.expectResultContains(new String[] {"Total amount value should be non-negative"});
    }
}
