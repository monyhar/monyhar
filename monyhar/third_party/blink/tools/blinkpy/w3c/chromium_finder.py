# Copyright 2017 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

from blinkpy.common.memoized import memoized
from blinkpy.common.path_finder import PathFinder


@memoized
def absolute_monyhar_wpt_dir(host):
    finder = PathFinder(host.filesystem)
    return finder.path_from_web_tests('external', 'wpt')


@memoized
def absolute_monyhar_dir(host):
    finder = PathFinder(host.filesystem)
    return finder.monyhar_base()
