// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.firstrun;

import org.junit.rules.ExternalResource;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;

/**
 * JUnit 4 rule that disables the First-Run Flow for tests.
 * This is needed to correctly populate the Context Menu.
 * <p>
 * The CommandLineFlags setting is redundant, but helps ensure that clients know that they don't
 * need to add it themselves.  This is also set in ChromeActivityTest, but having this here
 * adds resilience to changes in that class.
 */
@CommandLineFlags.Add(ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE)
public class DisableFirstRun extends ExternalResource {
    @Override
    protected void before() {
        TestThreadUtils.runOnUiThreadBlocking(() -> FirstRunStatus.setFirstRunFlowComplete(true));
    }

    @Override
    protected void after() {
        TestThreadUtils.runOnUiThreadBlocking(() -> FirstRunStatus.setFirstRunFlowComplete(false));
    }
}
