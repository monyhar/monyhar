// Copyright (c) 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include "trivial_ctor.h"

// Due to https://bugs.monyhar.org/p/monyhar/issues/detail?id=663463, we treat
// templated classes/structs as non-trivial, even if they really are trivial.
// Thus, classes that have such a class/struct as a member get flagged as being
// themselves non-trivial, even if (like |MySpinLock|) they are. Special-case
// [std::]atomic_int.
class TrivialTemplateOK {
 private:
  MySpinLock lock_;
};

int main() {
  MySpinLock lock;
  TrivialTemplateOK one;
  return 0;
}
