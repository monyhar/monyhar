# Copyright 2016 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

"""Chrome Autofill Task Flow

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

import abc
from urlparse import urlparse
import os
import shutil
from random import choice
from string import ascii_lowercase

from selenium import webdriver
from selenium.common.exceptions import TimeoutException, WebDriverException
from selenium.webdriver.chrome.options import Options


class TaskFlow(object):
  """Represents an executable set of Autofill Tasks.

  Attributes:
    profile: Dict of profile data that acts as the master source for
      validating autofill behaviour.
    debug: Whether debug output should be printed (False if not specified).
  """
  __metaclass__ = abc.ABCMeta
  def __init__(self, profile, debug=False):
    self.set_profile(profile)
    self._debug = debug
    self._running = False

    self._tasks = self._generate_task_sequence()

  def set_profile(self, profile):
    """Validates |profile| before assigning it as the source of user data.

    Args:
      profile: Dict of profile data that acts as the master source for
        validating autofill behaviour.

    Raises:
      ValueError: The |profile| dict provided is missing required keys
    """
    if not isinstance(profile, dict):
      raise ValueError('profile must be a a valid dictionary');

    self._profile = profile

  def run(self, user_data_dir, chrome_binary=None):
    """Generates and executes a sequence of chrome driver tasks.

    Args:
      user_data_dir: Path string for the writable directory in which profiles
        should be stored.
      chrome_binary: Path string to the Chrome binary that should be used by
        ChromeDriver.

        If None then it will use the PATH to find a binary.

    Raises:
      RuntimeError: Running the TaskFlow was attempted while it's already
        running.
      Exception: Any failure encountered while running the tests
    """
    if self._running:
      raise RuntimeError('Cannot run TaskFlow when already running')

    self._running = True

    self._run_tasks(user_data_dir, chrome_binary=chrome_binary)

    self._running = False

  @abc.abstractmethod
  def _generate_task_sequence(self):
    """Generates a set of executable tasks that will be run in ChromeDriver.

    Note: Subclasses must implement this method.

    Raises:
      NotImplementedError: Subclass did not implement the method

    Returns:
      A list of AutofillTask instances that are to be run in ChromeDriver.

      These tasks are to be run in order.
    """
    raise NotImplementedError()

  def _run_tasks(self, user_data_dir, chrome_binary=None):
    """Runs the internal set of tasks in a fresh ChromeDriver instance.

    Args:
      user_data_dir: Path string for the writable directory in which profiles
        should be stored.
      chrome_binary: Path string to the Chrome binary that should be used by
        ChromeDriver.

        If None then it will use the PATH to find a binary.

    Raises:
      Exception: Any failure encountered while running the tests
    """
    driver = self._get_driver(user_data_dir, chrome_binary=chrome_binary)
    try:
      for task in self._tasks:
        task.run(driver)
    finally:
      driver.quit()
      shutil.rmtree(self._profile_dir_dst)

  def _get_driver(self, user_data_dir, profile_name=None, chrome_binary=None,
                  chromedriver_binary='chromedriver'):
    """Spin up a ChromeDriver instance that uses a given set of user data.

    Generates a temporary profile data directory using a local set of test data.

    Args:
      user_data_dir: Path string for the writable directory in which profiles
        should be stored.
      profile_name: Name of the profile data directory to be created/used in
        user_data_dir.

        If None then an eight character name will be generated randomly.

        This directory will be removed after the task flow completes.
      chrome_binary: Path string to the Chrome binary that should be used by
        ChromeDriver.

        If None then it will use the PATH to find a binary.

    Returns: The generated Chrome Driver instance.
    """
    options = Options()

    if profile_name is None:
      profile_name = ''.join(choice(ascii_lowercase) for i in range(8))

    options.add_argument('--profile-directory=%s' % profile_name)

    full_path = os.path.realpath(__file__)
    path, filename = os.path.split(full_path)
    profile_dir_src = os.path.join(path, 'testdata', 'Default')
    self._profile_dir_dst = os.path.join(user_data_dir, profile_name)
    self._copy_tree(profile_dir_src, self._profile_dir_dst)

    if chrome_binary is not None:
      options.binary_location = chrome_binary

    options.add_argument('--user-data-dir=%s' % user_data_dir)
    options.add_argument('--show-autofill-type-predictions')

    service_args = []

    driver = webdriver.Chrome(executable_path=chromedriver_binary,
                              chrome_options=options,
                              service_args=service_args)
    driver.set_page_load_timeout(15)  # seconds
    return driver

  def _copy_tree(self, src, dst):
    """Recursively copy a directory tree.

    If the destination directory does not exist then it will be created for you.
    Doesn't overwrite newer existing files.

    Args:
      src: Path to the target source directory. It must exist.
      dst: Path to the target destination directory. Permissions to create the
        the directory (if necessary) and modify it's contents.
    """
    if not os.path.exists(dst):
      os.makedirs(dst)
    for item in os.listdir(src):
      src_item = os.path.join(src, item)
      dst_item = os.path.join(dst, item)
      if os.path.isdir(src_item):
        self._copy_tree(src_item, dst_item)
      elif (not os.path.exists(dst_item) or
        os.stat(src_item).st_mtime - os.stat(dst_item).st_mtime > 1):
        # Copy a file if it doesn't already exist, or if existing one is older.
        shutil.copy2(src_item, dst_item)
