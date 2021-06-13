// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include "base/rand_util.h"

// This file provides monyhar-specific implementations of platform abstraction
// functions used by minizip. Minizip provides a POSIX-compatible implementation
// in mz_os_posix.c, but pnacl-newlib has an insufficent POSIX layer such that
// mz_os_posix.c cannot be used.

extern "C" {

int32_t mz_os_rand(uint8_t* buf, int32_t size) {
  if (size < 0)
    return 0;

  base::RandBytes(buf, static_cast<size_t>(size));
  return size;
}

}  // extern "C"
