// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chromoting.help;

import org.monyhar.chromoting.R;
import org.monyhar.chromoting.WebViewActivity;

/**
 * The Activity for showing the Credits screen.
 */
public class CreditsActivity extends WebViewActivity {
    private static final String CREDITS_URL = "file:///android_res/raw/credits.html";

    public CreditsActivity() {
        super(R.string.credits, CREDITS_URL);
    }
}
