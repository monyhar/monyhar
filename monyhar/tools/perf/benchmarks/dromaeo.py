# Copyright 2013 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

from benchmarks import press

from telemetry import benchmark

from page_sets import dromaeo_pages


@benchmark.Info(component='Blink>Bindings',
                emails=['jbroman@monyhar.org',
                         'yukishiino@monyhar.org',
                         'haraken@monyhar.org'])
# pylint: disable=protected-access
class DromaeoBenchmark(press._PressBenchmark):

  @classmethod
  def Name(cls):
    return 'dromaeo'

  def CreateStorySet(self, options):
    return dromaeo_pages.DromaeoStorySet()
