// Copyright 2013 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content.app;

/**
 * Privileged (unsandboxed) Services inherit from this class. We enforce the
 * privileged/sandboxed distinction by type-checking objects against this parent class.
 */
public class PrivilegedProcessService extends ContentChildProcessService {}
