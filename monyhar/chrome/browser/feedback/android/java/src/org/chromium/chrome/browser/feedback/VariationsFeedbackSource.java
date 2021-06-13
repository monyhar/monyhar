// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.feedback;

import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.components.variations.VariationsAssociatedData;

import java.util.Map;

/** Grabs feedback about the current variations state. */
class VariationsFeedbackSource implements FeedbackSource {
    private final boolean mIsOffTheRecord;

    VariationsFeedbackSource(Profile profile) {
        mIsOffTheRecord = profile.isOffTheRecord();
    }

    @Override
    public Map<String, String> getFeedback() {
        if (mIsOffTheRecord) return null;
        return VariationsAssociatedData.getFeedbackMap();
    }
}