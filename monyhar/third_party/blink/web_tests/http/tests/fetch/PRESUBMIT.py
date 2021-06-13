# Copyright 2014 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

'''Chromium presubmit script for fetch API layout tests.

See http://dev.monyhar.org/developers/how-tos/depottools/presubmit-scripts.
'''

import os
import os.path
import re

'''
def missing_files(scripts_path, path_list):
    for script in os.listdir(scripts_path):
        if script.startswith('.') or not script.endswith('.js'):
            continue
        basename = re.sub(r'\.js$', '.html', os.path.basename(script))
        for path in [os.path.join(path, basename) for path in path_list]:
            if not os.path.exists(path):
                yield path


def CheckChangeOnUpload(input, output):
    contexts = ['window', 'workers', 'serviceworker']

    top_path = input.PresubmitLocalPath()
    script_tests_path = os.path.join(top_path, 'script-tests')
    test_paths = [os.path.join(top_path, context) for context in contexts]

    return [output.PresubmitPromptWarning('%s is missing' % path) for path
            in missing_files(script_tests_path, test_paths)]
'''

# Because generate.py has been quite updated, this PRESUBMIT.py is obsolete
# and temporarily disabled.
def CheckChangeOnUpload(input, output):
    return []
