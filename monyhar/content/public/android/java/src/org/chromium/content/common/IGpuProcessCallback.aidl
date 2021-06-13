// Copyright 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content.common;

import org.monyhar.content.common.SurfaceWrapper;
import android.view.Surface;

interface IGpuProcessCallback {

  oneway void forwardSurfaceForSurfaceRequest(
      in UnguessableToken requestToken, in Surface surface);

  SurfaceWrapper getViewSurface(int surfaceId);
}
