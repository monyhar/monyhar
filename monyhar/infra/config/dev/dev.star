# Copyright 2020 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

# See https://monyhar.googlesource.com/infra/luci/luci-go/+/HEAD/lucicfg/doc/README.md
# for information on starlark/lucicfg

luci.project(
    name = "monyhar",
    dev = True,
    buildbucket = "cr-buildbucket-dev.appspot.com",
    logdog = "luci-logdog-dev.appspot.com",
    milo = "luci-milo-dev.appspot.com",
    scheduler = "luci-scheduler-dev.appspot.com",
    swarming = "monyhar-swarm-dev.appspot.com",
    acls = [
        acl.entry(
            roles = [
                acl.LOGDOG_READER,
                acl.PROJECT_CONFIGS_READER,
                acl.SCHEDULER_READER,
            ],
            groups = "all",
        ),
        acl.entry(
            roles = acl.LOGDOG_WRITER,
            groups = "luci-logdog-monyhar-dev-writers",
        ),
        acl.entry(
            roles = acl.SCHEDULER_OWNER,
            groups = "project-monyhar-admins",
        ),
    ],
)

luci.logdog(
    gs_bucket = "monyhar-luci-logdog",
)

luci.milo(
    logo = "https://storage.googleapis.com/chrome-infra-public/logo/monyhar.svg",
)

# An all-purpose public realm.
luci.realm(
    name = "public",
    bindings = [
        luci.binding(
            roles = "role/buildbucket.reader",
            groups = "all",
        ),
        luci.binding(
            roles = "role/resultdb.invocationCreator",
            groups = "luci-resultdb-access",
        ),
        # Other roles are inherited from @root which grants them to group:all.
    ],
)

luci.builder.defaults.experiments.set({
    # Launch Swarming tasks in "realms-aware mode", crbug.com/1136313.
    "luci.use_realms": 100,
    # Enable resultsink for dev swarming tasks.
    "monyhar.resultdb.result_sink": 100,
})
luci.builder.defaults.test_presentation.set(resultdb.test_presentation(grouping_keys = ["status", "v.test_suite"]))

exec("//dev/swarming.star")

exec("//dev/subprojects/monyhar/subproject.star")
