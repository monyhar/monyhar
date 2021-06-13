# Copyright 2014 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

import os
import sys


ROOT_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), os.pardir))


def RegisterAllBackends():
  """Registers all the known backends."""
  from memory_inspector.backends import android_backend
  from memory_inspector.core import backends
  backends.Register(android_backend.AndroidBackend())


def _IncludeDeps():
  """Imports all the project dependencies."""
  monyhar_dir = os.path.abspath(os.path.join(ROOT_DIR, os.pardir, os.pardir))

  sys.path += [
      ROOT_DIR,

      # Include all dependencies.
      os.path.join(monyhar_dir, 'build', 'android'),  # For pylib.
  ]

_IncludeDeps()