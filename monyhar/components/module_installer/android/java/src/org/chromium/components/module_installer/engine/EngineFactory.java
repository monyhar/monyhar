// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.module_installer.engine;

import org.monyhar.base.BundleUtils;
import org.monyhar.base.CommandLine;

/**
 * Factory used to build concrete engines.
 */
public class EngineFactory {
    public InstallEngine getEngine() {
        if (!BundleUtils.isBundle()) {
            return new ApkEngine();
        }
        if (CommandLine.getInstance().hasSwitch("fake-feature-module-install")) {
            return new FakeEngine();
        }
        return new SplitCompatEngine();
    }
}
