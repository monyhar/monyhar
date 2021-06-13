// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.omaha.inline;

import com.google.android.play.core.appupdate.AppUpdateManagerFactory;

import org.monyhar.base.ContextUtils;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.omaha.UpdateConfigs;

/**
 * A factory that creates an {@link InlineUpdateController} instance.
 */
public class InlineUpdateControllerFactory {
    /**
     * @return A new {@link InlineUpdateController} to use to interact with Play for inline updates.
     */
    public static InlineUpdateController create(Runnable callback) {
        @FakeAppUpdateManagerWrapper.Type
        int mockInlineEndState = UpdateConfigs.getMockInlineScenarioEndState();
        if (mockInlineEndState != FakeAppUpdateManagerWrapper.Type.NO_SIMULATION) {
            // The config requires to run through a test controller, using the
            // PlayInlineUpdateController, but with a fake Google Play backend that automatically
            // goes through a scenario flow.
            return new PlayInlineUpdateController(
                    callback, new FakeAppUpdateManagerWrapper(mockInlineEndState));
        }

        if (ChromeFeatureList.isEnabled(ChromeFeatureList.INLINE_UPDATE_FLOW)) {
            // The application configuration requires to use the real Google Play backend for inline
            // updates.
            return new PlayInlineUpdateController(
                    callback, AppUpdateManagerFactory.create(ContextUtils.getApplicationContext()));
        }

        // No test scenario was in place, and the inline flow has not been enabled, so use a
        // controller with no functionality.
        return new NoopInlineUpdateController(callback);
    }
}
