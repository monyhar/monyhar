// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.attribution_reporting;

import org.monyhar.chrome.browser.base.SplitCompatContentProvider;
import org.monyhar.chrome.browser.base.SplitCompatUtils;

/** See {@link AttributionReportingProviderImpl}. */
public class AttributionReportingProvider extends SplitCompatContentProvider {
    private static final String IMPL_CLASS = "org.monyhar.chrome.browser.attribution_reporting"
            + ".AttributionReportingProviderImpl";

    public AttributionReportingProvider() {
        super(SplitCompatUtils.getIdentifierName(IMPL_CLASS));
    }
}
