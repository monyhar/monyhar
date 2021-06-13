# Copyright 2016 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

"""Chrome Autofill Test Flow

Execute a set of autofill tasks in a fresh ChromeDriver instance that has been
pre-loaded with some default profile.

Requires:
  - Selenium python bindings
    http://selenium-python.readthedocs.org/

  - ChromeDriver
    https://sites.google.com/a/monyhar.org/chromedriver/downloads
    The ChromeDriver executable must be available on the search PATH.

  - Chrome
"""

import importlib
import unittest

# Local Imports
from autofill_task.autofill_task import AutofillTask
from testdata import profile_data
from .case import AutofillTestCase


class AutofillTestSuite(unittest.TestSuite):
  """Represents an aggregation of individual Autofill test cases.

  Attributes:
    user_data_dir: Path string for the writable directory in which profiles
      should be stored.
    chrome_binary: Path string to the Chrome binary that should be used by
      ChromeDriver.

      If None then it will use the PATH to find a binary.
    test_class: Name of the test class that should be run.
      If this is set, then only the specified class will be executed
    module: The module to load test cases from. This is relative to the tasks
      package.
    profile: Dict of profile data that acts as the master source for
      validating autofill behaviour. If not specified then default profile data
      will be used from testdata.profile_data.
    debug: Whether debug output should be printed (False if not specified).
  """
  def __init__(self, user_data_dir, chrome_binary=None, test_class=None,
               module='sites', profile=None, debug=False):
    if profile is None:
      profile = profile_data.DEFAULT

    super(AutofillTestSuite, self).__init__()
    self._test_class = test_class
    self._profile = profile
    self._debug = debug

    module = 'tasks.%s' % module

    try:
      importlib.import_module(module)
    except ImportError:
      print 'Unable to load %s from tasks.' % module
      raise

    self._generate_tests(user_data_dir, chrome_binary)

  def _generate_tests(self, user_data_dir, chrome_binary=None):
    task_classes = AutofillTask.__subclasses__()
    tests = []

    if self._test_class:
      for task in task_classes:
        if task.__name__ == self._test_class:
          test = AutofillTestCase(task, user_data_dir, self._profile,
                                  chrome_binary=chrome_binary,
                                  debug=self._debug)
          self.addTest(test)
          return

      raise ValueError('Autofill Test \'%s\' could not be found.' %
                       self._test_class)
    else:
      for task in task_classes:
        tests.append(AutofillTestCase(task, user_data_dir, self._profile,
                                      chrome_binary=chrome_binary,
                                      debug=self._debug))

    self.addTests(tests)
