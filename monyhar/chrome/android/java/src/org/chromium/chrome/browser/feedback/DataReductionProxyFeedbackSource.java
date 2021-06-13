// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.feedback;

import org.monyhar.chrome.browser.net.spdyproxy.DataReductionProxySettings;
import org.monyhar.chrome.browser.profiles.Profile;

import java.util.Map;

/** Grabs feedback about the data reduction proxy. */
class DataReductionProxyFeedbackSource implements FeedbackSource {
    private final boolean mIsOffTheRecord;

    DataReductionProxyFeedbackSource(Profile profile) {
        mIsOffTheRecord = profile.isOffTheRecord();
    }

    @Override
    public Map<String, String> getFeedback() {
        if (mIsOffTheRecord) return null;
        return DataReductionProxySettings.getInstance().toFeedbackMap();
    }
}