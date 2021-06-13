# Copyright 2020 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

luci.console_view(
    name = "monyhar.dev",
    header = "//dev/monyhar-header.textpb",
    repo = "https://monyhar.googlesource.com/monyhar/src",
    entries = [
        luci.console_view_entry(builder = "ci/android-lollipop-arm-rel-swarming"),
        luci.console_view_entry(builder = "ci/android-marshmallow-arm64-rel-swarming"),
        luci.console_view_entry(builder = "ci/linux-rel-swarming"),
        luci.console_view_entry(builder = "ci/mac-rel-swarming"),
        luci.console_view_entry(builder = "ci/win-rel-swarming"),
    ],
)

luci.console_view(
    name = "monyhar.staging",
    header = "//dev/monyhar-header.textpb",
    repo = "https://monyhar.googlesource.com/monyhar/src",
    entries = [
        luci.console_view_entry(builder = "ci/linux-rel-swarming-staging"),
        luci.console_view_entry(builder = "ci/win-rel-swarming-staging"),
    ],
)
