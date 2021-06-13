# Copyright 2014 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

import os


PROJECT_SRC = os.path.abspath(os.path.join(__file__, os.pardir, os.pardir))
CLASSIFICATION_RULES_PATH = os.path.join(PROJECT_SRC, 'classification_rules')
PREBUILTS_PATH = os.path.join(PROJECT_SRC, 'prebuilts')
PREBUILTS_BASE_URL = 'https://storage.googleapis.com/monyhar-telemetry/'
