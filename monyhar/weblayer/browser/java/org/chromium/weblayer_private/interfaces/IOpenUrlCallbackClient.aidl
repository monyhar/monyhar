// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.weblayer_private.interfaces;

import org.monyhar.weblayer_private.interfaces.IBrowser;

// Since 91.
interface IOpenUrlCallbackClient {
  IBrowser getBrowserForNewTab() = 0;
  void onTabAdded(in int tabId) = 1;
}
