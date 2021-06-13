// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer_private.interfaces;

import org.monyhar.weblayer_private.interfaces.IClientNavigation;

/**
 * Allows the client to override the default way of handling user interactions
 * with error pages (such as SSL interstitials).
 */
interface IErrorPageCallbackClient {
  boolean onBackToSafety() = 0;
  String getErrorPageContent(IClientNavigation navigation) = 1;
}
