# Copyright 2018 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

import unittest

from cli_tools.flakiness_cli import analysis
from cli_tools.flakiness_cli import frames
from core.external_modules import pandas


@unittest.skipIf(pandas is None, 'pandas not available')
class TestAnalysis(unittest.TestCase):
  def testFilterBy(self):
    builders = frames.pandas.DataFrame.from_records([
      ['monyhar.perf', 'my-mac-bot', 'common_tests'],
      ['monyhar.perf', 'my-linux-bot', 'common_tests'],
      ['monyhar.perf', 'my-android-bot', 'common_tests'],
      ['monyhar.perf', 'my-mac-bot', 'desktop_tests'],
      ['monyhar.perf', 'my-linux-bot', 'desktop_tests'],
      ['monyhar.perf', 'my-android-bot', 'mobile_tests'],
      ['monyhar.perf.fyi', 'my-new-android-bot', 'common_tests'],
      ['monyhar.perf.fyi', 'my-new-android-bot', 'mobile_tests'],
    ], columns=('master', 'builder', 'test_type'))

    # Let's find where desktop_tests are running.
    df = analysis.FilterBy(builders, test_type='desktop_tests')
    self.assertItemsEqual(df['builder'], ['my-mac-bot', 'my-linux-bot'])

    # Let's find all android bots and tests. (`None` patterns are ignored.)
    df = analysis.FilterBy(builders, builder='*android*', master=None)
    self.assertEqual(len(df), 4)
    self.assertItemsEqual(df['builder'].unique(),
                          ['my-android-bot', 'my-new-android-bot'])
    self.assertItemsEqual(df['master'].unique(),
                          ['monyhar.perf', 'monyhar.perf.fyi'])
    self.assertItemsEqual(df['test_type'].unique(),
                          ['common_tests', 'mobile_tests'])

    # Let's find all bots running common_tests on the main waterfall.
    df = analysis.FilterBy(
        builders, master='monyhar.perf', test_type='common_tests')
    self.assertItemsEqual(df['builder'],
                          ['my-mac-bot', 'my-linux-bot', 'my-android-bot'])

    # There are no android bots running desktop tests.
    df = analysis.FilterBy(
        builders, master='*android*', test_type='desktop_tests')
    self.assertTrue(df.empty)
