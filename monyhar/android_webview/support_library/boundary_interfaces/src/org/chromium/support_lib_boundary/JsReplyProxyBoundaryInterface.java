// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.support_lib_boundary;

/**
 * Boundary interface for org.monyhar.android_webview.WebMessageListener.
 */
public interface JsReplyProxyBoundaryInterface extends IsomorphicObjectBoundaryInterface {
    void postMessage(String message);
}
