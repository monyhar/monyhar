// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chromecast.shell;

import android.content.Intent;

import org.monyhar.chromecast.base.Controller;
import org.monyhar.chromecast.base.Observable;

import java.util.HashMap;

/**
 * This class stores intents that can be used to resume a Cast Activity, for use by Play Next cards
 * and other services.
 */
public class ResumeIntents {
    private static final HashMap<String, Controller<Intent>> sSesionIdToIntent = new HashMap<>();

    static void addResumeIntent(String sessionId, Intent i) {
        Intent intent = new Intent(i);

        Controller<Intent> controller = sSesionIdToIntent.get(sessionId);
        if (controller == null) {
            controller = new Controller<>();
            sSesionIdToIntent.put(sessionId, controller);
        }
        controller.set(intent);
    }

    static void removeResumeIntent(String sessionId) {
        Controller<Intent> controller = sSesionIdToIntent.remove(sessionId);
        if (controller != null) {
            controller.reset();
        }
    }

    public static Observable<Intent> getResumeIntent(String sessionId) {
        Controller<Intent> controller = sSesionIdToIntent.get(sessionId);
        if (controller == null) {
            controller = new Controller<>();
            sSesionIdToIntent.put(sessionId, controller);
        }
        return controller;
    }
}