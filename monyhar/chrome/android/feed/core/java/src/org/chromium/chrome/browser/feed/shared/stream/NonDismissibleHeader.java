// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.feed.shared.stream;

import android.view.View;

/**
 * Class providing a default implementation for {@link Header} instances that are not dismissible.
 */
public class NonDismissibleHeader implements Header {
    private final View mView;

    public NonDismissibleHeader(View view) {
        this.mView = view;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public boolean isDismissible() {
        return false;
    }

    @Override
    public void onDismissed() {}
}
