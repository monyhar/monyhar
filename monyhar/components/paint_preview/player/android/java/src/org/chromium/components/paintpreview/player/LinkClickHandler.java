// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.paintpreview.player;

import org.monyhar.url.GURL;

/**
 * Interface for processing link click events from the player's hit tests.
 */
public interface LinkClickHandler { void onLinkClicked(GURL url); }
