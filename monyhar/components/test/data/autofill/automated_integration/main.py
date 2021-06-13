#!/usr/bin/env python
# Copyright 2016 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

"""Autofill automated integration test runner

Allows you to run integration test(s) for Autofill.
At this time only a limited set of websites are supported.

Requires:
  - Selenium python bindings
    http://selenium-python.readthedocs.org/

  - ChromeDriver
    https://sites.google.com/a/monyhar.org/chromedriver/downloads
    The ChromeDriver executable must be available on the search PATH.

  - Chrome (>= 53)

  - Write access to '/var/google/autofill/chrome_user_data'

Instructions:
  - Add tests to tasks/sites.py (or a new module in tasks/)
  - Run main.py -h to view the available flags.
    - All tests in tasks/sites.py will be run in the default chrome binary if
      no flags are specified.
"""

import argparse
import types
import sys

# Local Imports
from autofill_test.suite import AutofillTestSuite
from autofill_test.runner import AutofillTestRunner

USER_DATA_DIR = '/var/google/autofill/chrome_user_data'


def parse_args():
  description = 'Allows you to run integration test(s) for Autofill.'
  epilog = ('All tests in tasks/sites.py will be run in the default chrome '
            'binary if no flags are specified. At this time only a limited '
            'set of websites are supported.')

  parser = argparse.ArgumentParser(description=description, epilog=epilog)
  parser.add_argument('--user-data-dir', dest='user_data_dir', metavar='PATH',
                      default=USER_DATA_DIR, help='chrome user data directory')
  parser.add_argument('--chrome-binary', dest='chrome_binary', metavar='PATH',
                      default=None, help='chrome binary location')
  parser.add_argument('--module', default='sites', help='task module name')
  parser.add_argument('--test', dest='test_class',
                      help='name of a specific test to run')
  parser.add_argument('-d', '--debug', action='store_true', default=False,
                      help='print additional information, useful for debugging')

  args = parser.parse_args()

  args.debug = bool(args.debug)

  return args


def run(args):
  if args.debug:
    print 'Running with arguments: %s' % vars(args)

  try:
    test_suite = AutofillTestSuite(args.user_data_dir,
                                   chrome_binary=args.chrome_binary,
                                   test_class=args.test_class,
                                   module=args.module, debug=args.debug)
    verbosity = 2 if args.debug else 1
    runner = AutofillTestRunner(verbosity=verbosity)
    runner.run(test_suite)
  except ImportError as e:
    print 'Test Execution failed. %s' % str(e)
  except Exception as e:
    raise


if __name__ == '__main__':
  args = parse_args()
  run(args)
