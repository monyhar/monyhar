// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content_public.browser;

/** Callback interface for WebContents RequestAXTreeSnapshot(). */
public abstract class AccessibilitySnapshotCallback {

    /**
     * Provide accessibility node data. The root node will be null if the
     * accessibility tree cannot be parsed due to a failure/error.
     */
    public abstract void onAccessibilitySnapshot(AccessibilitySnapshotNode root);
}
