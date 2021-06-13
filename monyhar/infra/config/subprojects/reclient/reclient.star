# Copyright 2021 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

load("//lib/builders.star", "cpu", "os")
load("//lib/ci.star", "ci")
load("//lib/consoles.star", "consoles")
load("//console-header.star", "HEADER")

luci.bucket(
    name = "reclient",
    acls = [
        acl.entry(
            roles = acl.BUILDBUCKET_READER,
            groups = "all",
        ),
        acl.entry(
            roles = acl.BUILDBUCKET_TRIGGERER,
            groups = "project-monyhar-ci-schedulers",
        ),
        acl.entry(
            roles = acl.BUILDBUCKET_OWNER,
            groups = "google/luci-task-force@google.com",
        ),
    ],
)

ci.defaults.set(
    bucket = "reclient",
    build_numbers = True,
    builder_group = "monyhar.reclient.fyi",
    configure_kitchen = True,
    cores = 8,
    cpu = cpu.X86_64,
    executable = "recipe:monyhar",
    execution_timeout = 3 * time.hour,
    goma_backend = None,
    kitchen_emulate_gce = True,
    os = os.LINUX_DEFAULT,
    pool = "luci.monyhar.ci",
    service_account = (
        "monyhar-ci-builder@chops-service-accounts.iam.gserviceaccount.com"
    ),
    swarming_tags = ["vpython:native-python-wrapper"],
    triggered_by = ["monyhar-gitiles-trigger"],
)

consoles.console_view(
    name = "monyhar.reclient.fyi",
    header = HEADER,
    include_experimental_builds = True,
    repo = "https://monyhar.googlesource.com/monyhar/src",
)

def fyi_reclient_staging_builder(
        *,
        name,
        reclient_instance = "rbe-monyhar-trusted",
        **kwargs):
    return ci.builder(
        name = name,
        reclient_instance = reclient_instance,
        console_view_entry = consoles.console_view_entry(
            category = "rbe|linux",
            short_name = "rcs",
        ),
        **kwargs
    )

fyi_reclient_staging_builder(
    name = "Linux Builder Re-Client Staging",
)
