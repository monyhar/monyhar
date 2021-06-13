// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser;

/**
 * Non-translatable strings cause apk size bloat if they are added in strings. Use constants
 * instead of marking the message 'translateable="false"'.
 */
public class ChromeStringConstants {
    public static final String AUTOFILL_MANAGE_WALLET_CARD_URL =
            "https://payments.google.com/#paymentMethods";
    public static final String AUTOFILL_MANAGE_WALLET_ADDRESSES_URL =
            "https://payments.google.com/#paymentMethods";
    public static final String SYNC_DASHBOARD_URL = "https://www.google.com/settings/chrome/sync";
}
