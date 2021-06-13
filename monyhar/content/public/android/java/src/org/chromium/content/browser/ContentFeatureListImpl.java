// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content.browser;

import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.MainDex;
import org.monyhar.base.annotations.NativeMethods;

/**
 * Implementation of {@link ContentFeatureList}.
 * Java accessor for base/feature_list.h state.
 */
@JNINamespace("content::android")
@MainDex
public class ContentFeatureListImpl {
    /**
     * Returns whether the specified feature is enabled or not.
     *
     * Note: Features queried through this API must be added to the array
     * |kFeaturesExposedToJava| in content/browser/android/content_feature_list.cc
     *
     * @param featureName The name of the feature to query.
     * @return Whether the feature is enabled or not.
     */
    public static boolean isEnabled(String featureName) {
        return ContentFeatureListImplJni.get().isEnabled(featureName);
    }

    @NativeMethods
    public interface Natives {
        boolean isEnabled(String featureName);
    }
}
