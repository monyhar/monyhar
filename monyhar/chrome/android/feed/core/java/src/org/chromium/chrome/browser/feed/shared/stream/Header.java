// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.feed.shared.stream;

import android.view.View;

/** Interface users can implement to define a header to display above Stream content. */
public interface Header {
    /** Returns the view representing the header. */
    View getView();

    /** Returns true if the header can be dismissed. */
    boolean isDismissible();

    /** Called when the header has been dismissed. */
    void onDismissed();
}
