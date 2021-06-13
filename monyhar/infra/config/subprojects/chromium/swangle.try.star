# Copyright 2020 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

load("//lib/builders.star", "cpu", "os")
load("//lib/try.star", "try_")

try_.defaults.set(
    bucket = "try",
    build_numbers = True,
    caches = [
        swarming.cache(
            name = "win_toolchain",
            path = "win_toolchain",
        ),
    ],
    configure_kitchen = True,
    cores = 8,
    cpu = cpu.X86_64,
    cq_group = "cq",
    executable = "recipe:monyhar_trybot",
    execution_timeout = 2 * time.hour,
    # Max. pending time for builds. CQ considers builds pending >2h as timed
    # out: http://shortn/_8PaHsdYmlq. Keep this in sync.
    expiration_timeout = 2 * time.hour,
    os = os.LINUX_DEFAULT,
    pool = "luci.monyhar.try",
    service_account = "monyhar-try-gpu-builder@chops-service-accounts.iam.gserviceaccount.com",
    subproject_list_view = "luci.monyhar.try",
    swarming_tags = ["vpython:native-python-wrapper"],
    task_template_canary_percentage = 5,
)

try_.monyhar_swangle_linux_builder(
    name = "linux-swangle-monyhar-try-x64",
    pool = "luci.monyhar.swangle.monyhar.linux.x64.try",
    execution_timeout = 6 * time.hour,
    pinned = False,
)

try_.monyhar_swangle_linux_builder(
    name = "linux-swangle-try-tot-angle-x64",
    pool = "luci.monyhar.swangle.angle.linux.x64.try",
)

try_.monyhar_swangle_linux_builder(
    name = "linux-swangle-try-tot-swiftshader-x64",
    pool = "luci.monyhar.swangle.sws.linux.x64.try",
)

try_.monyhar_swangle_linux_builder(
    name = "linux-swangle-try-x64",
    pool = "luci.monyhar.swangle.deps.linux.x64.try",
    pinned = False,
)

try_.monyhar_swangle_mac_builder(
    name = "mac-swangle-monyhar-try-x64",
    pool = "luci.monyhar.swangle.monyhar.mac.x64.try",
    execution_timeout = 6 * time.hour,
    pinned = False,
)

try_.monyhar_swangle_windows_builder(
    name = "win-swangle-monyhar-try-x86",
    pool = "luci.monyhar.swangle.monyhar.win.x86.try",
    execution_timeout = 6 * time.hour,
    pinned = False,
)

try_.monyhar_swangle_windows_builder(
    name = "win-swangle-try-tot-angle-x64",
    pool = "luci.monyhar.swangle.win.x64.try",
)

try_.monyhar_swangle_windows_builder(
    name = "win-swangle-try-tot-angle-x86",
    pool = "luci.monyhar.swangle.angle.win.x86.try",
)

try_.monyhar_swangle_windows_builder(
    name = "win-swangle-try-tot-swiftshader-x64",
    pool = "luci.monyhar.swangle.win.x64.try",
)

try_.monyhar_swangle_windows_builder(
    name = "win-swangle-try-tot-swiftshader-x86",
    pool = "luci.monyhar.swangle.sws.win.x86.try",
)

try_.monyhar_swangle_windows_builder(
    name = "win-swangle-try-x64",
    pool = "luci.monyhar.swangle.win.x64.try",
    pinned = False,
)

try_.monyhar_swangle_windows_builder(
    name = "win-swangle-try-x86",
    pool = "luci.monyhar.swangle.deps.win.x86.try",
    pinned = False,
)
