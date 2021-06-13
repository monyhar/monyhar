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

  - Chrome (>= 53)
"""

# Local Imports
from task_flow import TaskFlow


class AutofillTestFlow(TaskFlow):
  """Represents an executable set of Autofill Tasks.

  Note: currently the test flows consist of a single AutofillTask

  Used for automated autofill integration testing.

  Attributes:
    task_class: AutofillTask to use for the test.
    profile: Dict of profile data that acts as the master source for
      validating autofill behaviour.
    debug: Whether debug output should be printed (False if not specified).
  """
  def __init__(self, task_class, profile, debug=False):
    self._task_class = task_class
    super(AutofillTestFlow, self).__init__(profile, debug)

  def _generate_task_sequence(self):
    """Generates a set of executable tasks that will be run in ChromeDriver.

    Returns:
      A list of AutofillTask instances that are to be run in ChromeDriver.

      These tasks are to be run in order.
    """

    task = self._task_class(self._profile, self._debug)
    return [task]

  def __str__(self):
    if self._tasks:
      return 'Autofill Test Flow using \'%s\'' % self._tasks[0]
    else:
      return 'Empty Autofill Test Flow'
