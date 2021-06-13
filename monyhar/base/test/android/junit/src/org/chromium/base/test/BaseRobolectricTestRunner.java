// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.base.test;

import androidx.test.core.app.ApplicationProvider;

import org.junit.runners.model.InitializationError;
import org.robolectric.DefaultTestLifecycle;
import org.robolectric.TestLifecycle;

import org.monyhar.base.ApplicationStatus;
import org.monyhar.base.CommandLine;
import org.monyhar.base.ContextUtils;
import org.monyhar.base.LifetimeAssert;
import org.monyhar.base.PathUtils;
import org.monyhar.base.metrics.UmaRecorderHolder;
import org.monyhar.testing.local.LocalRobolectricTestRunner;

import java.lang.reflect.Method;

/**
 * A Robolectric Test Runner that initializes base globals.
 */
public class BaseRobolectricTestRunner extends LocalRobolectricTestRunner {
    /**
     * Enables a per-test setUp / tearDown hook.
     */
    public static class BaseTestLifecycle extends DefaultTestLifecycle {
        @Override
        public void beforeTest(Method method) {
            ContextUtils.initApplicationContextForTests(
                    ApplicationProvider.getApplicationContext());
            ApplicationStatus.initialize(ApplicationProvider.getApplicationContext());
            UmaRecorderHolder.resetForTesting();
            CommandLine.init(null);
            super.beforeTest(method);
        }

        @Override
        public void afterTest(Method method) {
            try {
                LifetimeAssert.assertAllInstancesDestroyedForTesting();
            } finally {
                ApplicationStatus.destroyForJUnitTests();
                PathUtils.resetForTesting();
                super.afterTest(method);
            }
        }
    }

    public BaseRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected Class<? extends TestLifecycle> getTestLifecycleClass() {
        return BaseTestLifecycle.class;
    }
}
