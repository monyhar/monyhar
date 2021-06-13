// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content.app;

import org.monyhar.base.annotations.MainDex;

/**
 * This is needed to register multiple PrivilegedProcess services so that we can have
 * more than one unsandboxed process.
 */
@MainDex
public class PrivilegedProcessService3 extends PrivilegedProcessService {}
