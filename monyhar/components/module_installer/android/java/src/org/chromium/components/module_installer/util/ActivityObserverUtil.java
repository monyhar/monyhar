// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.module_installer.util;

import org.monyhar.base.ApplicationStatus;
import org.monyhar.base.ThreadUtils;
import org.monyhar.components.module_installer.engine.EngineFactory;
import org.monyhar.components.module_installer.observer.ActivityObserver;
import org.monyhar.components.module_installer.observer.InstallerObserver;

/**
 * Initializes an activity observer responsible to listen to state changes in activities
 * and split compat them when appropriate.
 */
class ActivityObserverUtil {
    private static volatile InstallerObserver sObserver;

    public static void registerDefaultObserver() {
        ThreadUtils.assertOnUiThread();

        if (sObserver != null) {
            return;
        }

        EngineFactory engineFactory = new EngineFactory();
        ActivityObserver observer = new ActivityObserver(engineFactory.getEngine());

        ApplicationStatus.registerStateListenerForAllActivities(observer);

        sObserver = observer;
    }

    public static void notifyObservers() {
        assert sObserver != null;
        sObserver.onModuleInstalled();
    }
}
