// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.photo_picker;

import org.monyhar.chrome.browser.base.SplitCompatService;
import org.monyhar.chrome.browser.base.SplitCompatUtils;

/** See {@link DecoderServiceImpl}. */
public class DecoderService extends SplitCompatService {
    public DecoderService() {
        super(SplitCompatUtils.getIdentifierName(
                "org.monyhar.chrome.browser.photo_picker.DecoderServiceImpl"));
    }
}
