// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.policy.test;

import org.junit.Assert;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.components.policy.PolicyCache;
import org.monyhar.components.policy.PolicyCacheUpdater;

/**
 * Native unit test helper class for {@link PolicyCacheUpdater}
 *
 * It's used by the unit test in
 * //components/policy/core/browser/android/policy_cache_updater_android_unittest.cc
 *
 */
@JNINamespace("policy::android")
public class PolicyCacheUpdaterTestSupporter {
    @CalledByNative
    private PolicyCacheUpdaterTestSupporter() {}

    @CalledByNative
    private void verifyPolicyCacheIntValue(String policy, boolean hasValue, int expectedValue) {
        PolicyCache policyCache = PolicyCache.get();
        policyCache.setReadableForTesting(true);
        Integer actualValue = policyCache.getIntValue(policy);
        if (hasValue) {
            Assert.assertNotNull(actualValue);
            Assert.assertEquals(expectedValue, actualValue.intValue());
        } else {
            Assert.assertNull(actualValue);
        }
    }
}
