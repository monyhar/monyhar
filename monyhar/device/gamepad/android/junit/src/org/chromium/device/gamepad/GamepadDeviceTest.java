// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.device.gamepad;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import org.monyhar.base.test.BaseRobolectricTestRunner;
import org.monyhar.base.test.util.Feature;

/**
 * Verify no regressions in gamepad mappings.
 */
@RunWith(BaseRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class GamepadDeviceTest {
    @Test
    @Feature({"Gamepad"})
    public void testRelevantKeycodesAreSorted() {
        for (int i = 0; i < GamepadDevice.RELEVANT_KEYCODES.length - 1; ++i) {
            Assert.assertTrue(
                    GamepadDevice.RELEVANT_KEYCODES[i] < GamepadDevice.RELEVANT_KEYCODES[i + 1]);
        }
    }
}
