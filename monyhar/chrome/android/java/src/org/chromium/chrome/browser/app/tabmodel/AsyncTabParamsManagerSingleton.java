// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.app.tabmodel;

import org.monyhar.chrome.browser.tabmodel.AsyncTabParamsManager;
import org.monyhar.chrome.browser.tabmodel.AsyncTabParamsManagerFactory;

/** Holds a singleton {@link AsyncTabParamsManager} */
public class AsyncTabParamsManagerSingleton {
    /** Singleton instance. */
    private static final AsyncTabParamsManager INSTANCE =
            AsyncTabParamsManagerFactory.createAsyncTabParamsManager();

    /** Get the singleton instance of {@link AsyncTabParamsManager}. */
    public static AsyncTabParamsManager getInstance() {
        return INSTANCE;
    }
}
