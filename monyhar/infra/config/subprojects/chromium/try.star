# Copyright 2020 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

load("//lib/branches.star", "branches")
load("//lib/builders.star", "cpu", "goma", "os", "xcode")
load("//lib/consoles.star", "consoles")
load("//lib/try.star", "try_")
load("//project.star", "settings")

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
    execution_timeout = 4 * time.hour,
    # Max. pending time for builds. CQ considers builds pending >2h as timed
    # out: http://shortn/_8PaHsdYmlq. Keep this in sync.
    expiration_timeout = 2 * time.hour,
    grace_period = 2 * time.minute,
    os = os.LINUX_DEFAULT,
    pool = "luci.monyhar.try",
    service_account = "monyhar-try-builder@chops-service-accounts.iam.gserviceaccount.com",
    subproject_list_view = "luci.monyhar.try",
    swarming_tags = ["vpython:native-python-wrapper"],
    task_template_canary_percentage = 5,
)

luci.bucket(
    name = "try",
    acls = [
        acl.entry(
            roles = acl.BUILDBUCKET_READER,
            groups = "all",
        ),
        acl.entry(
            roles = acl.BUILDBUCKET_TRIGGERER,
            users = [
                "findit-for-me@appspot.gserviceaccount.com",
                "tricium-prod@appspot.gserviceaccount.com",
            ],
            groups = [
                "project-monyhar-tryjob-access",
                # Allow Pinpoint to trigger builds for bisection
                "service-account-chromeperf",
                "service-account-cq",
            ],
            projects = branches.value(for_main = [
                "angle",
                "dawn",
                "skia",
                "swiftshader",
                "v8",
            ]),
        ),
        acl.entry(
            roles = acl.BUILDBUCKET_OWNER,
            groups = "service-account-monyhar-tryserver",
        ),
    ],
)

luci.cq_group(
    name = "cq",
    retry_config = cq.RETRY_ALL_FAILURES,
    tree_status_host = branches.value(for_main = "monyhar-status.appspot.com"),
    watch = cq.refset(
        repo = "https://monyhar.googlesource.com/monyhar/src",
        refs = [branches.value(
            # The monyhar project's CQ covers all of the refs under refs/heads,
            # which includes refs/heads/master
            for_main = "refs/heads/.+",
            # For projects running out of a branch, the CQ only runs for that
            # ref
            for_branches = settings.ref,
        )],
    ),
    acls = [
        acl.entry(
            acl.CQ_COMMITTER,
            groups = "project-monyhar-committers",
        ),
        acl.entry(
            acl.CQ_DRY_RUNNER,
            groups = "project-monyhar-tryjob-access",
        ),
    ],
    additional_modes = [
        cq.run_mode(cq.MODE_QUICK_DRY_RUN, 1, "Quick-Run", 1),
    ],
)

# Automatically maintained consoles

consoles.list_view(
    name = "try",
    branch_selector = branches.ALL_BRANCHES,
    title = "{} CQ Console".format(settings.project_title),
)

consoles.list_view(
    name = "luci.monyhar.try",
    branch_selector = branches.ALL_BRANCHES,
)

consoles.list_view(
    name = "tryserver.blink",
    branch_selector = branches.STANDARD_MILESTONE,
)

consoles.list_view(
    name = "tryserver.monyhar",
    branch_selector = branches.STANDARD_MILESTONE,
)

consoles.list_view(
    name = "tryserver.monyhar.android",
    branch_selector = branches.STANDARD_MILESTONE,
)

consoles.list_view(
    name = "tryserver.monyhar.angle",
)

consoles.list_view(
    name = "tryserver.monyhar.monyharos",
    branch_selector = branches.LTS_MILESTONE,
)

consoles.list_view(
    name = "tryserver.monyhar.dawn",
    branch_selector = branches.STANDARD_MILESTONE,
)

consoles.list_view(
    name = "tryserver.monyhar.linux",
    branch_selector = branches.LTS_MILESTONE,
)

consoles.list_view(
    name = "tryserver.monyhar.mac",
    branch_selector = branches.STANDARD_MILESTONE,
)

consoles.list_view(
    name = "tryserver.monyhar.packager",
)

consoles.list_view(
    name = "tryserver.monyhar.swangle",
)

consoles.list_view(
    name = "tryserver.monyhar.updater",
)

consoles.list_view(
    name = "tryserver.monyhar.win",
    branch_selector = branches.STANDARD_MILESTONE,
)

# Builders are sorted first lexicographically by the function used to define
# them, then lexicographically by their name

try_.builder(
    name = "branch-config-verifier",
    executable = "recipe:branch_configuration/tester",
    main_list_view = "try",
    os = os.LINUX_BIONIC_REMOVE,
    properties = {
        "branch_script": "infra/config/scripts/branch.py",
        "branch_types": ["standard", "lts"],
        "verification_scripts": ["infra/config/main.star", "infra/config/dev.star"],
    },
    tryjob = try_.job(
        # Errors that this builder would catch would go unnoticed until a
        # project is set up on a branch day or even worse when a branch was
        # turned into an LTS branch, long after the change has been made, so
        # disable reuse to ensure it's checked with current code. The builder
        # runs in a few minutes and only for infra/config changes, so it won't
        # impose a heavy burden on our capacity.
        disable_reuse = True,
        add_default_excludes = False,
        location_regexp = [r".+/[+]/infra/config/.+"],
    ),
)

try_.blink_builder(
    name = "linux-blink-optional-highdpi-rel",
    goma_backend = goma.backend.RBE_PROD,
)

try_.blink_builder(
    name = "linux-blink-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    goma_backend = goma.backend.RBE_PROD,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/cc/.+",
            ".+/[+]/third_party/blink/renderer/core/paint/.+",
            ".+/[+]/third_party/blink/renderer/core/svg/.+",
            ".+/[+]/third_party/blink/renderer/platform/graphics/.+",
        ],
    ),
)

try_.blink_builder(
    name = "win10-blink-rel",
    goma_backend = goma.backend.RBE_PROD,
    os = os.WINDOWS_ANY,
    builderless = True,
)

try_.blink_builder(
    name = "win7-blink-rel",
    goma_backend = goma.backend.RBE_PROD,
    os = os.WINDOWS_ANY,
    builderless = True,
)

try_.blink_mac_builder(
    name = "mac10.12-blink-rel",
)

try_.blink_mac_builder(
    name = "mac10.13-blink-rel",
)

try_.blink_mac_builder(
    name = "mac10.14-blink-rel",
)

try_.blink_mac_builder(
    name = "mac10.15-blink-rel",
)

try_.blink_mac_builder(
    name = "mac11.0-blink-rel",
    builderless = False,
)

try_.monyhar_builder(
    name = "android-official",
    branch_selector = branches.STANDARD_MILESTONE,
    cores = 32,
)

try_.monyhar_builder(
    name = "fuchsia-official",
    branch_selector = branches.STANDARD_MILESTONE,
    cores = 32,
)

try_.monyhar_builder(
    name = "linux-official",
    branch_selector = branches.STANDARD_MILESTONE,
    cores = 32,
)

try_.monyhar_builder(
    name = "mac-official",
    branch_selector = branches.STANDARD_MILESTONE,
    cores = None,
    os = os.MAC_ANY,
)

try_.monyhar_builder(
    name = "win-official",
    branch_selector = branches.STANDARD_MILESTONE,
    os = os.WINDOWS_DEFAULT,
    cores = 32,
    execution_timeout = 6 * time.hour,
)

try_.monyhar_builder(
    name = "win32-official",
    branch_selector = branches.STANDARD_MILESTONE,
    os = os.WINDOWS_DEFAULT,
    cores = 32,
    execution_timeout = 6 * time.hour,
)

try_.monyhar_android_builder(
    name = "android-10-arm64-rel",
)

try_.monyhar_android_builder(
    name = "android-11-x86-fyi-rel",
)

try_.monyhar_android_builder(
    name = "android-asan",
)

try_.monyhar_android_builder(
    name = "android-bfcache-rel",
)

try_.monyhar_android_builder(
    name = "android-binary-size",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    executable = "recipe:binary_size_trybot",
    goma_jobs = goma.jobs.J150,
    main_list_view = "try",
    properties = {
        "$build/binary_size": {
            "analyze_targets": [
                "//chrome/android:monochrome_public_minimal_apks",
                "//chrome/android:trichrome_minimal_apks",
                "//chrome/android:validate_expectations",
                "//tools/binary_size:binary_size_trybot_py",
            ],
            "compile_targets": [
                "monochrome_public_minimal_apks",
                "monochrome_static_initializers",
                "trichrome_minimal_apks",
                "validate_expectations",
            ],
        },
    },
    tryjob = try_.job(),
    # TODO(crbug/1202741)
    os = os.LINUX_XENIAL_OR_BIONIC_REMOVE,
)

try_.monyhar_android_builder(
    name = "android-cronet-arm-dbg",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/components/cronet/.+",
            ".+/[+]/components/grpc_support/.+",
            ".+/[+]/build/android/.+",
            ".+/[+]/build/config/android/.+",
        ],
        location_regexp_exclude = [
            ".+/[+]/components/cronet/ios/.+",
        ],
    ),
)

try_.monyhar_android_builder(
    name = "android-cronet-marshmallow-arm64-rel",
)

try_.monyhar_android_builder(
    name = "android-deterministic-dbg",
    executable = "recipe:swarming/deterministic_build",
    execution_timeout = 6 * time.hour,
)

try_.monyhar_android_builder(
    name = "android-deterministic-rel",
    executable = "recipe:swarming/deterministic_build",
    execution_timeout = 6 * time.hour,
)

try_.monyhar_android_builder(
    name = "android-inverse-fieldtrials-pie-x86-fyi-rel",
)

try_.monyhar_android_builder(
    name = "android-lollipop-arm-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    cores = branches.value(for_main = 16, for_branches = 8),
    goma_jobs = goma.jobs.J150,
    main_list_view = "try",
    tryjob = try_.job(),
    # TODO(crbug/1202741)
    os = os.LINUX_XENIAL_OR_BIONIC_REMOVE,
)

try_.monyhar_android_builder(
    name = "android-marshmallow-arm64-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    cores = branches.value(for_main = 32, for_branches = 16),
    goma_jobs = goma.jobs.J300,
    main_list_view = "try",
    ssd = True,
    use_java_coverage = True,
    tryjob = try_.job(),
    # TODO(crbug/1202741)
    os = os.LINUX_XENIAL_OR_BIONIC_REMOVE,
)

# Experimental builder to check dual coverage on android platform.
try_.monyhar_android_builder(
    name = "android-marshmallow-arm64-rel-dual-coverage",
    builderless = True,
    cores = 16,
    goma_jobs = goma.jobs.J300,
    ssd = True,
    use_java_coverage = True,
    tryjob = try_.job(experiment_percentage = 3),
)

try_.monyhar_android_builder(
    name = "android-marshmallow-x86-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    cores = 16,
    goma_jobs = goma.jobs.J300,
    ssd = True,
    tryjob = try_.job(),
    # TODO(crbug/1202741)
    os = os.LINUX_XENIAL_OR_BIONIC_REMOVE,
)

try_.monyhar_android_builder(
    name = "android-marshmallow-x86-rel-non-cq",
)

# TODO(crbug.com/1111436) Added it back once all Pixel 1s are flashed
# back to NJH47F
#try_.monyhar_android_builder(
#    name = "android-nougat-arm64-rel",
#    branch_selector = branches.STANDARD_MILESTONE,
#    goma_jobs = goma.jobs.J150,
#    main_list_view = 'try',
#)

try_.monyhar_android_builder(
    name = "android-opus-arm-rel",
)

try_.monyhar_android_builder(
    name = "android-oreo-arm64-cts-networkservice-dbg",
)

try_.monyhar_android_builder(
    name = "android-oreo-arm64-dbg",
)

try_.monyhar_android_builder(
    name = "android-pie-arm64-dbg",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = False,
    cores = 16,
    goma_jobs = goma.jobs.J300,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chrome/android/features/vr/.+",
            ".+/[+]/chrome/android/java/src/org/monyhar/chrome/browser/vr/.+",
            ".+/[+]/chrome/android/javatests/src/org/monyhar/chrome/browser/vr/.+",
            ".+/[+]/chrome/browser/android/vr/.+",
            ".+/[+]/chrome/browser/vr/.+",
            ".+/[+]/content/browser/xr/.+",
            ".+/[+]/device/vr/android/.+",
            ".+/[+]/third_party/gvr-android-sdk/.+",
            ".+/[+]/third_party/arcore-android-sdk/.+",
            ".+/[+]/third_party/arcore-android-sdk-client/.+",
        ],
    ),
)

# TODO(crbug/1182468) Remove when experiment is done.
try_.monyhar_android_builder(
    name = "android-pie-arm64-coverage-experimental-rel",
    builderless = True,
    cores = 16,
    goma_jobs = goma.jobs.J300,
    ssd = True,
    main_list_view = "try",
    use_clang_coverage = True,
    tryjob = try_.job(
        experiment_percentage = 3,
    ),
)

try_.monyhar_android_builder(
    name = "android-pie-arm64-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    cores = 16,
    goma_jobs = goma.jobs.J300,
    ssd = True,
    main_list_view = "try",
    tryjob = try_.job(),
    # TODO(crbug/1202741)
    os = os.LINUX_XENIAL_OR_BIONIC_REMOVE,
)

try_.monyhar_android_builder(
    name = "android-pie-x86-rel",
    goma_jobs = goma.jobs.J150,
)

# TODO(crbug/1182468) Remove when coverage is enabled on CQ.
try_.monyhar_android_builder(
    name = "android-pie-arm64-coverage-rel",
    cores = 16,
    goma_jobs = goma.jobs.J300,
    ssd = True,
    use_clang_coverage = True,
)

try_.monyhar_android_builder(
    name = "android-pie-arm64-wpt-rel-non-cq",
)

try_.monyhar_android_builder(
    name = "android-web-platform-pie-x86-fyi-rel",
)

try_.monyhar_android_builder(
    name = "android-weblayer-10-x86-rel-tests",
)

try_.monyhar_android_builder(
    name = "android-weblayer-marshmallow-x86-rel-tests",
)

try_.monyhar_android_builder(
    name = "android-weblayer-pie-x86-rel-tests",
)

try_.monyhar_android_builder(
    name = "android-weblayer-pie-x86-wpt-fyi-rel",
)

try_.monyhar_android_builder(
    name = "android-weblayer-pie-x86-wpt-smoketest",
)

try_.monyhar_android_builder(
    name = "android-webview-pie-x86-wpt-fyi-rel",
)

try_.monyhar_android_builder(
    name = "android-webview-marshmallow-arm64-dbg",
)

try_.monyhar_android_builder(
    name = "android-webview-nougat-arm64-dbg",
)

try_.monyhar_android_builder(
    name = "android-webview-oreo-arm64-dbg",
)

try_.monyhar_android_builder(
    name = "android-webview-pie-arm64-dbg",
)

try_.monyhar_android_builder(
    name = "android-webview-pie-arm64-fyi-rel",
)

try_.monyhar_android_builder(
    name = "android_archive_rel_ng",
)

try_.monyhar_android_builder(
    name = "android_arm64_dbg_recipe",
    goma_jobs = goma.jobs.J300,
)

try_.monyhar_android_builder(
    name = "android_blink_rel",
)

try_.monyhar_android_builder(
    name = "android_cfi_rel_ng",
    cores = 32,
)

try_.monyhar_android_builder(
    name = "android_clang_dbg_recipe",
    goma_jobs = goma.jobs.J300,
)

try_.monyhar_android_builder(
    name = "android_compile_dbg",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    goma_jobs = goma.jobs.J150,
    main_list_view = "try",
    tryjob = try_.job(),
    # TODO(crbug/1202741)
    os = os.LINUX_XENIAL_OR_BIONIC_REMOVE,
)

try_.monyhar_android_builder(
    name = "android_compile_x64_dbg",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chrome/android/java/src/org/monyhar/chrome/browser/vr/.+",
            ".+/[+]/chrome/browser/vr/.+",
            ".+/[+]/content/browser/xr/.+",
            ".+/[+]/sandbox/linux/seccomp-bpf/.+",
            ".+/[+]/sandbox/linux/seccomp-bpf-helpers/.+",
            ".+/[+]/sandbox/linux/system_headers/.+",
            ".+/[+]/sandbox/linux/tests/.+",
            ".+/[+]/third_party/gvr-android-sdk/.+",
        ],
    ),
)

try_.monyhar_android_builder(
    name = "android_compile_x86_dbg",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chrome/android/java/src/org/monyhar/chrome/browser/vr/.+",
            ".+/[+]/chrome/browser/vr/.+",
            ".+/[+]/content/browser/xr/.+",
            ".+/[+]/sandbox/linux/seccomp-bpf/.+",
            ".+/[+]/sandbox/linux/seccomp-bpf-helpers/.+",
            ".+/[+]/sandbox/linux/system_headers/.+",
            ".+/[+]/sandbox/linux/tests/.+",
            ".+/[+]/third_party/gvr-android-sdk/.+",
        ],
    ),
)

try_.monyhar_android_builder(
    name = "android_cronet",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    main_list_view = "try",
    tryjob = try_.job(),
    # TODO(crbug/1202741)
    os = os.LINUX_XENIAL_OR_BIONIC_REMOVE,
)

try_.monyhar_android_builder(
    name = "android_mojo",
)

try_.monyhar_android_builder(
    name = "android_n5x_swarming_dbg",
)

try_.monyhar_android_builder(
    name = "android_unswarmed_pixel_aosp",
)

try_.monyhar_android_builder(
    name = "cast_shell_android",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    main_list_view = "try",
    tryjob = try_.job(),
    # TODO(crbug/1202741)
    os = os.LINUX_XENIAL_OR_BIONIC_REMOVE,
)

try_.monyhar_android_builder(
    name = "linux_android_dbg_ng",
)

try_.monyhar_android_builder(
    name = "try-nougat-phone-tester",
)

try_.monyhar_angle_builder(
    name = "android-angle-monyhar-try",
    os = os.LINUX_BIONIC_REMOVE,
    executable = "recipe:angle_monyhar_trybot",
)

try_.monyhar_angle_builder(
    name = "android-angle-try",
    os = os.LINUX_BIONIC_REMOVE,
    executable = "recipe:angle_monyhar_trybot",
)

try_.monyhar_angle_builder(
    name = "android_angle_deqp_rel_ng",
    os = os.LINUX_BIONIC_REMOVE,
)

try_.monyhar_angle_builder(
    name = "android_angle_rel_ng",
    os = os.LINUX_BIONIC_REMOVE,
)

try_.monyhar_angle_builder(
    name = "fuchsia-angle-rel",
    os = os.LINUX_BIONIC_REMOVE,
)

try_.monyhar_angle_builder(
    name = "fuchsia-angle-try",
    os = os.LINUX_BIONIC_REMOVE,
    executable = "recipe:angle_monyhar_trybot",
)

try_.monyhar_angle_builder(
    name = "linux-angle-rel",
    os = os.LINUX_BIONIC_REMOVE,
)

try_.monyhar_angle_builder(
    name = "linux-angle-monyhar-try",
    os = os.LINUX_BIONIC_REMOVE,
    executable = "recipe:angle_monyhar_trybot",
)

try_.monyhar_angle_builder(
    name = "linux_angle_deqp_rel_ng",
    os = os.LINUX_BIONIC_REMOVE,
)

try_.monyhar_angle_builder(
    name = "linux-angle-try",
    os = os.LINUX_BIONIC_REMOVE,
    executable = "recipe:angle_monyhar_trybot",
)

try_.monyhar_angle_builder(
    name = "mac-angle-monyhar-try",
    cores = None,
    os = os.MAC_ANY,
    executable = "recipe:angle_monyhar_trybot",
)

try_.monyhar_angle_builder(
    name = "mac-angle-try",
    cores = None,
    os = os.MAC_ANY,
    executable = "recipe:angle_monyhar_trybot",
)

try_.monyhar_angle_builder(
    name = "win-angle-monyhar-x64-try",
    os = os.WINDOWS_ANY,
    executable = "recipe:angle_monyhar_trybot",
)

try_.monyhar_angle_builder(
    name = "win-angle-monyhar-x86-try",
    os = os.WINDOWS_ANY,
    executable = "recipe:angle_monyhar_trybot",
)

try_.monyhar_angle_builder(
    name = "win-angle-x64-try",
    os = os.WINDOWS_ANY,
    executable = "recipe:angle_monyhar_trybot",
)

try_.monyhar_angle_builder(
    name = "win-angle-x86-try",
    os = os.WINDOWS_ANY,
    executable = "recipe:angle_monyhar_trybot",
)

try_.monyhar_monyharos_builder(
    name = "chromeos-amd64-generic-cfi-thin-lto-rel",
)

try_.monyhar_monyharos_builder(
    name = "chromeos-amd64-generic-dbg",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/content/gpu/.+",
            ".+/[+]/media/.+",
        ],
    ),
)

try_.monyhar_monyharos_builder(
    name = "chromeos-amd64-generic-rel",
    branch_selector = branches.LTS_MILESTONE,
    builderless = not settings.is_main,
    main_list_view = "try",
    os = os.LINUX_BIONIC_REMOVE,
    tryjob = try_.job(),
)

try_.monyhar_monyharos_builder(
    name = "chromeos-amd64-generic-rel-dchecks",
)

try_.monyhar_monyharos_builder(
    name = "chromeos-arm-generic-dbg",
)

try_.monyhar_monyharos_builder(
    name = "chromeos-arm-generic-rel",
    branch_selector = branches.LTS_MILESTONE,
    builderless = not settings.is_main,
    main_list_view = "try",
    os = os.LINUX_BIONIC_REMOVE,
    tryjob = try_.job(),
)

try_.monyhar_monyharos_builder(
    name = "lacros-amd64-generic-rel",
    builderless = not settings.is_main,
    main_list_view = "try",
    tryjob = try_.job(),
    os = os.LINUX_BIONIC_REMOVE,
)

try_.monyhar_monyharos_builder(
    name = "lacros-arm-generic-rel",
    builderless = not settings.is_main,
    main_list_view = "try",
    tryjob = try_.job(
        experiment_percentage = 1,
    ),
    os = os.LINUX_BIONIC_REMOVE,
)

try_.monyhar_monyharos_builder(
    name = "linux-chromeos-compile-dbg",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    main_list_view = "try",
    os = os.LINUX_BIONIC_REMOVE,
    tryjob = try_.job(),
)

try_.monyhar_monyharos_builder(
    name = "chromeos-kevin-compile-rel",
)

try_.monyhar_monyharos_builder(
    name = "chromeos-kevin-rel",
    branch_selector = branches.LTS_MILESTONE,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/build/chromeos/.+",
            ".+/[+]/build/config/chromeos/.*",
            ".+/[+]/chromeos/CHROMEOS_LKGM",
        ],
    ),
)

try_.monyhar_monyharos_builder(
    name = "linux-chromeos-inverse-fieldtrials-fyi-rel",
)

try_.monyhar_monyharos_builder(
    name = "linux-chromeos-rel",
    branch_selector = branches.LTS_MILESTONE,
    builderless = not settings.is_main,
    goma_jobs = goma.jobs.J150,
    main_list_view = "try",
    tryjob = try_.job(),
    use_clang_coverage = True,
    os = os.LINUX_BIONIC_REMOVE,
    coverage_test_types = ["unit", "overall"],
)

try_.monyhar_monyharos_builder(
    name = "linux-chromeos-js-code-coverage",
    use_clang_coverage = True,
    use_javascript_coverage = True,
)

try_.monyhar_monyharos_builder(
    name = "linux-lacros-rel",
    builderless = not settings.is_main,
    cores = 16,
    ssd = True,
    goma_jobs = goma.jobs.J300,
    main_list_view = "try",
    tryjob = try_.job(),
    os = os.LINUX_BIONIC_REMOVE,
)

try_.monyhar_monyharos_builder(
    name = "linux-chromeos-dbg",
)

try_.monyhar_monyharos_builder(
    name = "linux-cfm-rel",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chromeos/components/chromebox_for_meetings/.+",
            ".+/[+]/chromeos/dbus/chromebox_for_meetings/.+",
            ".+/[+]/chromeos/services/chromebox_for_meetings/.+",
            ".+/[+]/chrome/browser/chromeos/chromebox_for_meetings/.+",
        ],
    ),
)

try_.monyhar_dawn_builder(
    name = "dawn-linux-x64-deps-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    os = os.LINUX_BIONIC_REMOVE,
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/gpu/.+",
            ".+/[+]/testing/buildbot/monyhar.dawn.json",
            ".+/[+]/third_party/blink/renderer/modules/webgpu/.+",
            ".+/[+]/third_party/blink/web_tests/external/wpt/webgpu/.+",
            ".+/[+]/third_party/blink/web_tests/wpt_internal/webgpu/.+",
            ".+/[+]/third_party/blink/web_tests/WebGPUExpectations",
            ".+/[+]/third_party/dawn/.+",
            ".+/[+]/third_party/webgpu-cts/.+",
            ".+/[+]/tools/clang/scripts/update.py",
            ".+/[+]/ui/gl/features.gni",
        ],
    ),
)

try_.monyhar_dawn_builder(
    name = "dawn-mac-x64-deps-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    os = os.MAC_ANY,
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/gpu/.+",
            ".+/[+]/testing/buildbot/monyhar.dawn.json",
            ".+/[+]/third_party/blink/renderer/modules/webgpu/.+",
            ".+/[+]/third_party/blink/web_tests/external/wpt/webgpu/.+",
            ".+/[+]/third_party/blink/web_tests/wpt_internal/webgpu/.+",
            ".+/[+]/third_party/blink/web_tests/WebGPUExpectations",
            ".+/[+]/third_party/dawn/.+",
            ".+/[+]/third_party/webgpu-cts/.+",
            ".+/[+]/tools/clang/scripts/update.py",
            ".+/[+]/ui/gl/features.gni",
        ],
    ),
)

try_.monyhar_dawn_builder(
    name = "dawn-win10-x64-deps-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    os = os.WINDOWS_ANY,
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/gpu/.+",
            ".+/[+]/testing/buildbot/monyhar.dawn.json",
            ".+/[+]/third_party/blink/renderer/modules/webgpu/.+",
            ".+/[+]/third_party/blink/web_tests/external/wpt/webgpu/.+",
            ".+/[+]/third_party/blink/web_tests/wpt_internal/webgpu/.+",
            ".+/[+]/third_party/blink/web_tests/WebGPUExpectations",
            ".+/[+]/third_party/dawn/.+",
            ".+/[+]/third_party/webgpu-cts/.+",
            ".+/[+]/tools/clang/scripts/update.py",
            ".+/[+]/ui/gl/features.gni",
        ],
    ),
)

try_.monyhar_dawn_builder(
    name = "dawn-win10-x86-deps-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    os = os.WINDOWS_ANY,
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/gpu/.+",
            ".+/[+]/testing/buildbot/monyhar.dawn.json",
            ".+/[+]/third_party/blink/renderer/modules/webgpu/.+",
            ".+/[+]/third_party/blink/web_tests/external/wpt/webgpu/.+",
            ".+/[+]/third_party/blink/web_tests/wpt_internal/webgpu/.+",
            ".+/[+]/third_party/blink/web_tests/WebGPUExpectations",
            ".+/[+]/third_party/dawn/.+",
            ".+/[+]/third_party/webgpu-cts/.+",
            ".+/[+]/tools/clang/scripts/update.py",
            ".+/[+]/ui/gl/features.gni",
        ],
    ),
)

try_.monyhar_dawn_builder(
    name = "linux-dawn-rel",
    os = os.LINUX_BIONIC_REMOVE,
)

try_.monyhar_dawn_builder(
    name = "mac-dawn-rel",
    os = os.MAC_ANY,
)

try_.monyhar_dawn_builder(
    name = "win-dawn-rel",
    os = os.WINDOWS_ANY,
)

try_.monyhar_dawn_builder(
    name = "dawn-try-win10-x86-rel",
    os = os.WINDOWS_ANY,
)

try_.monyhar_dawn_builder(
    name = "dawn-try-win10-x64-asan-rel",
    os = os.WINDOWS_ANY,
)

try_.monyhar_linux_builder(
    name = "cast_shell_audio_linux",
)

try_.monyhar_linux_builder(
    name = "cast_shell_linux",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    main_list_view = "try",
    tryjob = try_.job(),
)

try_.monyhar_linux_builder(
    name = "cast_shell_linux_dbg",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chromecast/.+",
        ],
    ),
)

try_.monyhar_linux_builder(
    name = "cast-binary-size",
    builderless = True,
    executable = "recipe:binary_size_cast_trybot",
    properties = {
        "$build/binary_size": {
            "analyze_targets": [
                "//chromecast:cast_shell",
            ],
            "compile_targets": [
                "cast_shell",
            ],
        },
    },
)

try_.monyhar_linux_builder(
    name = "monyhar_presubmit",
    branch_selector = branches.ALL_BRANCHES,
    executable = "recipe:presubmit",
    goma_backend = None,
    main_list_view = "try",
    # Default priority for buildbucket is 30, see
    # https://monyhar.googlesource.com/infra/infra/+/bb68e62b4380ede486f65cd32d9ff3f1bbe288e4/appengine/cr-buildbucket/creation.py#42
    # This will improve our turnaround time for landing infra/config changes
    # when addressing outages
    priority = 25,
    properties = {
        "$depot_tools/presubmit": {
            "runhooks": True,
            "timeout_s": 480,
        },
        "repo_name": "monyhar",
    },
    tryjob = try_.job(
        disable_reuse = True,
        add_default_excludes = False,
    ),
    os = os.LINUX_BIONIC_REMOVE,
)

try_.monyhar_linux_builder(
    name = "fuchsia-arm64-cast",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chromecast/.+",
        ],
    ),
)

try_.monyhar_linux_builder(
    name = "fuchsia-compile-x64-dbg",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/base/fuchsia/.+",
            ".+/[+]/fuchsia/.+",
            ".+/[+]/media/fuchsia/.+",
        ],
    ),
)

try_.monyhar_linux_builder(
    name = "fuchsia-deterministic-dbg",
    executable = "recipe:swarming/deterministic_build",
)

try_.monyhar_linux_builder(
    name = "fuchsia-fyi-arm64-dbg",
)

try_.monyhar_linux_builder(
    name = "fuchsia-fyi-arm64-femu",
)

try_.monyhar_linux_builder(
    name = "fuchsia-fyi-arm64-rel",
)

try_.monyhar_linux_builder(
    name = "fuchsia-fyi-x64-dbg",
)

try_.monyhar_linux_builder(
    name = "fuchsia-fyi-x64-rel",
)

try_.monyhar_linux_builder(
    name = "fuchsia-x64-cast",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    main_list_view = "try",
    tryjob = try_.job(),
)

try_.monyhar_linux_builder(
    name = "fuchsia_arm64",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    main_list_view = "try",
    tryjob = try_.job(),
)

try_.monyhar_linux_builder(
    name = "fuchsia_x64",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    main_list_view = "try",
    tryjob = try_.job(),
)

try_.monyhar_linux_builder(
    name = "layout_test_leak_detection",
)

try_.monyhar_linux_builder(
    name = "leak_detection_linux",
)

try_.monyhar_linux_builder(
    name = "linux-1mbu-compile-fyi-rel",
    builderless = False,
    goma_jobs = goma.jobs.J150,
    tryjob = try_.job(
        experiment_percentage = 10,
    ),
    properties = {
        "bot_update_experiments": [
            "no_sync",
        ],
    },
)

try_.monyhar_linux_builder(
    name = "linux-annotator-rel",
)

try_.monyhar_linux_builder(
    name = "linux-autofill-assistant",
)

try_.monyhar_linux_builder(
    name = "linux-bfcache-rel",
)

try_.monyhar_linux_builder(
    name = "linux-bionic-rel",
    goma_jobs = goma.jobs.J150,
    os = os.LINUX_BIONIC,
)

try_.monyhar_linux_builder(
    name = "linux-blink-heap-concurrent-marking-tsan-rel",
)

try_.monyhar_linux_builder(
    name = "linux-blink-heap-verification-try",
)

try_.monyhar_linux_builder(
    name = "linux-blink-v8-oilpan",
)

try_.monyhar_linux_builder(
    name = "linux-blink-web-tests-force-accessibility-rel",
)

try_.monyhar_linux_builder(
    name = "linux-clang-tidy-dbg",
    executable = "recipe:tricium_clang_tidy_wrapper",
    goma_jobs = goma.jobs.J150,
)

try_.monyhar_linux_builder(
    name = "linux-clang-tidy-rel",
    executable = "recipe:tricium_clang_tidy_wrapper",
    goma_jobs = goma.jobs.J150,
)

try_.monyhar_linux_builder(
    name = "linux-dcheck-off-rel",
)

try_.monyhar_linux_builder(
    name = "linux-example-builder",
)

try_.monyhar_linux_builder(
    name = "linux-extended-tracing-rel",
)

try_.monyhar_linux_builder(
    name = "linux-gcc-rel",
    goma_backend = None,
)

try_.monyhar_linux_builder(
    name = "linux-inverse-fieldtrials-fyi-rel",
)

try_.monyhar_linux_builder(
    name = "linux-mbi-mode-per-render-process-host-rel",
)

try_.monyhar_linux_builder(
    name = "linux-mbi-mode-per-site-instance-rel",
)

try_.monyhar_linux_builder(
    name = "linux-lacros-fyi-rel",
)

try_.monyhar_linux_builder(
    name = "linux-lacros-version-skew-fyi",
)

try_.monyhar_linux_builder(
    name = "linux-layout-tests-edit-ng",
)

try_.monyhar_linux_builder(
    name = "linux-libfuzzer-asan-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    executable = "recipe:monyhar_libfuzzer_trybot",
    main_list_view = "try",
    tryjob = try_.job(),
)

try_.monyhar_linux_builder(
    name = "linux-ozone-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    main_list_view = "try",
    tryjob = try_.job(),
)

try_.monyhar_linux_builder(
    name = "linux-perfetto-rel",
    tryjob = try_.job(
        experiment_percentage = 100,
        location_regexp = [
            ".+/[+]/base/trace_event/.+",
            ".+/[+]/base/tracing/.+",
            ".+/[+]/components/tracing/.+",
            ".+/[+]/content/browser/tracing/.+",
            ".+/[+]/services/tracing/.+",
        ],
    ),
)

try_.monyhar_linux_builder(
    name = "linux-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    goma_jobs = goma.jobs.J150,
    main_list_view = "try",
    use_clang_coverage = True,
    coverage_test_types = ["unit", "overall"],
    tryjob = try_.job(
        enable_for_quick_run = True,
    ),
)

try_.monyhar_linux_builder(
    name = "linux-rel-orchestrator",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = False,
    cores = 4,
    executable = "recipe:monyhar/orchestrator",
    main_list_view = "try",
    use_clang_coverage = True,
    properties = {
        "compilator": "linux-rel-compilator",
    },
    service_account = "monyhar-mini-orchestrator@chops-service-accounts.iam.gserviceaccount.com",
)

try_.monyhar_linux_builder(
    name = "linux-rel-compilator",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = False,
    cores = 16,
    executable = "recipe:monyhar/compilator",
    goma_jobs = goma.jobs.J150,
    main_list_view = "try",
    use_clang_coverage = True,
    properties = {
        "orchestrator": {
            "builder_name": "linux-rel-orchestrator",
            "builder_group": "tryserver.monyhar.linux",
        },
    },
)

try_.monyhar_linux_builder(
    name = "linux-rel-reclient",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    reclient_jobs = 150,
    goma_backend = None,
    reclient_instance = "rbe-monyhar-gvisor-shadow",
    configure_kitchen = True,
    kitchen_emulate_gce = True,
    tryjob = try_.job(
        experiment_percentage = 10,
    ),
    use_clang_coverage = True,
)

try_.monyhar_linux_builder(
    name = "linux-trusty-rel",
    goma_jobs = goma.jobs.J150,
    os = os.LINUX_TRUSTY,
)

try_.monyhar_linux_builder(
    name = "linux-xenial-rel",
    goma_jobs = goma.jobs.J150,
    os = os.LINUX_XENIAL,
)

try_.monyhar_linux_builder(
    name = "linux-viz-rel",
)

try_.monyhar_linux_builder(
    name = "linux-webkit-msan-rel",
)

try_.monyhar_linux_builder(
    name = "linux-wpt-fyi-rel",
)

try_.monyhar_linux_builder(
    name = "linux-wpt-identity-fyi-rel",
)

try_.monyhar_linux_builder(
    name = "linux-wpt-input-fyi-rel",
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_analysis",
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_archive_rel_ng",
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_asan_rel_ng",
    branch_selector = branches.STANDARD_MILESTONE,
    goma_jobs = goma.jobs.J150,
    ssd = True,
    main_list_view = "try",
    tryjob = try_.job(),
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_cfi_rel_ng",
    cores = 32,
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_chromeos_asan_rel_ng",
    goma_jobs = goma.jobs.J150,
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_chromeos_msan_rel_ng",
    goma_jobs = goma.jobs.J150,
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_clobber_deterministic",
    executable = "recipe:swarming/deterministic_build",
    execution_timeout = 6 * time.hour,
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_clobber_rel_ng",
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_compile_dbg_32_ng",
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_compile_dbg_ng",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    caches = [
        swarming.cache(
            name = "builder",
            path = "linux_debug",
        ),
    ],
    goma_jobs = goma.jobs.J150,
    main_list_view = "try",
    tryjob = try_.job(),
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_compile_rel_ng",
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_dbg_ng",
    branch_selector = branches.STANDARD_MILESTONE,
    caches = [
        swarming.cache(
            name = "builder",
            path = "linux_debug",
        ),
    ],
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/build/.*check_gn_headers.*",
        ],
    ),
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_msan_rel_ng",
    goma_jobs = goma.jobs.J150,
)

# TODO(crbug.com/1200904): Remove after migration
try_.monyhar_linux_builder(
    name = "linux_monyhar_tsan_rel_ng_bionic",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    goma_jobs = goma.jobs.J150,
    os = os.LINUX_BIONIC,
    main_list_view = "try",
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_tsan_rel_ng",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    goma_jobs = goma.jobs.J150,
    main_list_view = "try",
    tryjob = try_.job(),
)

try_.monyhar_linux_builder(
    name = "linux_monyhar_ubsan_rel_ng",
)

try_.monyhar_linux_builder(
    name = "linux_layout_tests_composite_after_paint",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/third_party/blink/renderer/core/paint/.+",
            ".+/[+]/third_party/blink/renderer/core/svg/.+",
            ".+/[+]/third_party/blink/renderer/platform/graphics/.+",
            ".+/[+]/third_party/blink/web_tests/.+",
        ],
    ),
)

try_.monyhar_linux_builder(
    name = "linux_layout_tests_layout_ng_disabled",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/third_party/blink/renderer/core/editing/.+",
            ".+/[+]/third_party/blink/renderer/core/layout/.+",
            ".+/[+]/third_party/blink/renderer/core/paint/.+",
            ".+/[+]/third_party/blink/renderer/core/svg/.+",
            ".+/[+]/third_party/blink/renderer/platform/fonts/shaping/.+",
            ".+/[+]/third_party/blink/renderer/platform/graphics/.+",
            ".+/[+]/third_party/blink/web_tests/.+",
        ],
    ),
)

try_.monyhar_linux_builder(
    name = "linux_mojo",
)

try_.monyhar_linux_builder(
    name = "linux_mojo_chromeos",
)

try_.monyhar_linux_builder(
    name = "linux_upload_clang",
    builderless = True,
    cores = 32,
    executable = "recipe:monyhar_upload_clang",
    goma_backend = None,
    os = os.LINUX_TRUSTY,
)

try_.monyhar_linux_builder(
    name = "linux_vr",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chrome/browser/vr/.+",
            ".+/[+]/content/browser/xr/.+",
        ],
    ),
)

try_.monyhar_linux_builder(
    name = "network_service_linux",
)

try_.monyhar_linux_builder(
    name = "tricium-metrics-analysis",
    executable = "recipe:tricium_metrics",
)

try_.monyhar_linux_builder(
    name = "tricium-oilpan-analysis",
    executable = "recipe:tricium_oilpan",
)

try_.monyhar_linux_builder(
    name = "tricium-simple",
    executable = "recipe:tricium_simple",
)

try_.monyhar_mac_builder(
    name = "mac-osxbeta-rel",
    os = os.MAC_DEFAULT,
)

try_.monyhar_mac_builder(
    name = "mac-inverse-fieldtrials-fyi-rel",
    os = os.MAC_DEFAULT,
)

try_.monyhar_mac_builder(
    name = "mac-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = not settings.is_main,
    use_clang_coverage = True,
    goma_jobs = goma.jobs.J150,
    main_list_view = "try",
    os = os.MAC_DEFAULT,
    tryjob = try_.job(
        enable_for_quick_run = True,
    ),
)

try_.monyhar_mac_builder(
    name = "mac-arm64-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    goma_jobs = goma.jobs.J150,
    os = os.MAC_10_15,
)

# NOTE: the following trybots aren't sensitive to Mac version on which
# they are built, hence no additional dimension is specified.
# The 10.xx version translates to which bots will run isolated tests.
try_.monyhar_mac_builder(
    name = "mac_monyhar_10.11_rel_ng",
)

try_.monyhar_mac_builder(
    name = "mac_monyhar_10.12_rel_ng",
)

try_.monyhar_mac_builder(
    name = "mac_monyhar_10.13_rel_ng",
)

try_.monyhar_mac_builder(
    name = "mac_monyhar_10.14_rel_ng",
)

try_.monyhar_mac_builder(
    name = "mac_monyhar_10.15_rel_ng",
)

try_.monyhar_mac_builder(
    name = "mac_monyhar_11.0_rel_ng",
    builderless = False,
)

try_.monyhar_mac_builder(
    name = "mac_monyhar_archive_rel_ng",
)

try_.monyhar_mac_builder(
    name = "mac_monyhar_asan_rel_ng",
    goma_jobs = goma.jobs.J150,
)

try_.monyhar_mac_builder(
    name = "mac_monyhar_compile_dbg_ng",
    branch_selector = branches.STANDARD_MILESTONE,
    goma_jobs = goma.jobs.J150,
    os = os.MAC_DEFAULT,
    main_list_view = "try",
    tryjob = try_.job(),
)

try_.monyhar_mac_builder(
    name = "mac_monyhar_compile_rel_ng",
)

try_.monyhar_mac_builder(
    name = "mac_monyhar_dbg_ng",
)

try_.monyhar_mac_builder(
    name = "mac_upload_clang",
    builderless = False,
    executable = "recipe:monyhar_upload_clang",
    execution_timeout = 6 * time.hour,
    goma_backend = None,  # Does not use Goma.
)

try_.monyhar_mac_builder(
    name = "mac_upload_clang_arm",
    builderless = False,
    executable = "recipe:monyhar_upload_clang",
    execution_timeout = 6 * time.hour,
    goma_backend = None,  # Does not use Goma.
)

try_.monyhar_mac_ios_builder(
    name = "ios-device",
)

try_.monyhar_mac_ios_builder(
    name = "ios-simulator",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    use_clang_coverage = True,
    coverage_exclude_sources = "ios_test_files_and_test_utils",
    coverage_test_types = ["overall", "unit"],
    tryjob = try_.job(),
)

try_.monyhar_mac_ios_builder(
    name = "ios-simulator-cronet",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/components/cronet/.+",
            ".+/[+]/components/grpc_support/.+",
            ".+/[+]/ios/.+",
        ],
        location_regexp_exclude = [
            ".+/[+]/components/cronet/android/.+",
        ],
    ),
)

try_.monyhar_mac_ios_builder(
    name = "ios-simulator-full-configs",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    use_clang_coverage = True,
    coverage_exclude_sources = "ios_test_files_and_test_utils",
    coverage_test_types = ["overall", "unit"],
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/ios/.+",
        ],
    ),
)

try_.monyhar_mac_ios_builder(
    name = "ios-simulator-inverse-fieldtrials-fyi",
)

try_.monyhar_mac_ios_builder(
    name = "ios-simulator-multi-window",
)

try_.monyhar_mac_ios_builder(
    name = "ios-simulator-noncq",
)

try_.monyhar_mac_ios_builder(
    name = "ios14-beta-simulator",
    os = os.MAC_11,
)

try_.monyhar_mac_ios_builder(
    name = "ios14-sdk-simulator",
    os = os.MAC_11,
    xcode = xcode.x12e262,
)

try_.monyhar_updater_mac_builder(
    name = "mac-updater-try-builder-dbg",
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chrome/updater/.+",
        ],
    ),
)

try_.monyhar_mac_ios_builder(
    name = "ios15-beta-simulator",
)

try_.monyhar_mac_ios_builder(
    name = "ios15-sdk-simulator",
    xcode = xcode.x13latestbeta,
)

try_.monyhar_updater_mac_builder(
    name = "mac-updater-try-builder-rel",
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chrome/updater/.+",
        ],
    ),
)

try_.monyhar_updater_win_builder(
    name = "win-updater-try-builder-dbg",
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chrome/updater/.+",
        ],
    ),
)

try_.monyhar_updater_win_builder(
    name = "win-updater-try-builder-rel",
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chrome/updater/.+",
        ],
    ),
)

try_.monyhar_win_builder(
    name = "win-annotator-rel",
)

try_.monyhar_win_builder(
    name = "win-asan",
    goma_jobs = goma.jobs.J150,
)

try_.monyhar_win_builder(
    name = "win-celab-try-rel",
    executable = "recipe:celab",
    properties = {
        "exclude": "chrome_only",
        "pool_name": "celab-monyhar-try",
        "pool_size": 20,
        "tests": "*",
    },
)

try_.monyhar_win_builder(
    name = "win-libfuzzer-asan-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = False,
    executable = "recipe:monyhar_libfuzzer_trybot",
    main_list_view = "try",
    os = os.WINDOWS_ANY,
    tryjob = try_.job(),
)

try_.monyhar_win_builder(
    name = "win_archive",
)

try_.monyhar_win_builder(
    name = "win_monyhar_compile_dbg_ng",
    branch_selector = branches.STANDARD_MILESTONE,
    goma_jobs = goma.jobs.J150,
    main_list_view = "try",
    tryjob = try_.job(),
)

try_.monyhar_win_builder(
    name = "win_monyhar_compile_rel_ng",
)

try_.monyhar_win_builder(
    name = "win_monyhar_dbg_ng",
)

try_.monyhar_win_builder(
    name = "win_monyhar_x64_rel_ng",
)

try_.monyhar_win_builder(
    name = "win_mojo",
)

try_.monyhar_win_builder(
    name = "win_upload_clang",
    builderless = False,
    cores = 32,
    executable = "recipe:monyhar_upload_clang",
    goma_backend = None,
    os = os.WINDOWS_ANY,
    execution_timeout = 6 * time.hour,
)

try_.monyhar_win_builder(
    name = "win_x64_archive",
)

try_.monyhar_win_builder(
    name = "win10_monyhar_x64_1909_fyi_rel_ng",
    builderless = False,
    os = os.WINDOWS_10_1909,
)

try_.monyhar_win_builder(
    name = "win10_monyhar_x64_dbg_ng",
    os = os.WINDOWS_10,
)

try_.monyhar_win_builder(
    name = "win10_monyhar_inverse_fieldtrials_x64_fyi_rel_ng",
    os = os.WINDOWS_10,
)

try_.monyhar_win_builder(
    name = "win10_monyhar_x64_rel_ng",
    branch_selector = branches.STANDARD_MILESTONE,
    goma_jobs = goma.jobs.J300,
    os = os.WINDOWS_10,
    cores = 16,
    ssd = True,
    use_clang_coverage = True,
    coverage_test_types = ["unit", "overall"],
    main_list_view = "try",
    tryjob = try_.job(
        enable_for_quick_run = True,
    ),
)

try_.monyhar_win_builder(
    name = "win10_monyhar_x64_rel_ng_exp",
    builderless = False,
    os = os.WINDOWS_ANY,
)

try_.monyhar_win_builder(
    name = "win7-rel",
    branch_selector = branches.STANDARD_MILESTONE,
    cores = 16,
    execution_timeout = 4 * time.hour + 30 * time.minute,
    goma_jobs = goma.jobs.J300,
    main_list_view = "try",
    ssd = True,
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/sandbox/win/.+",
        ],
    ),
)

try_.cipd_3pp_builder(
    name = "3pp-linux-amd64-packager",
    os = os.LINUX_XENIAL_OR_BIONIC_SWITCH_TO_DEFAULT,
    builderless = False,
    properties = {
        "$build/monyhar_3pp": {
            "platform": "linux-amd64",
            "package_prefix": "monyhar_3pp",
            "preprocess": [{
                "name": "third_party/android_deps",
                "cmd": [
                    "{CHECKOUT}/src/third_party/android_deps/fetch_all.py",
                    "-v",
                    "--ignore-vulnerabilities",
                ],
            }],
            "gclient_config": "monyhar",
            "gclient_apply_config": ["android"],
        },
    },
    tryjob = try_.job(
        location_regexp = [
            # Enable for CLs touching files under "3pp" directories which are
            # two level deep or more from the repo root.
            ".+/[+]/.+/3pp/.+",
        ],
    ),
)

try_.gpu_monyhar_android_builder(
    name = "android_optional_gpu_tests_rel",
    branch_selector = branches.STANDARD_MILESTONE,
    goma_jobs = goma.jobs.J150,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/cc/.+",
            ".+/[+]/chrome/browser/vr/.+",
            ".+/[+]/content/browser/xr/.+",
            ".+/[+]/components/viz/.+",
            ".+/[+]/content/test/gpu/.+",
            ".+/[+]/gpu/.+",
            ".+/[+]/media/audio/.+",
            ".+/[+]/media/base/.+",
            ".+/[+]/media/capture/.+",
            ".+/[+]/media/filters/.+",
            ".+/[+]/media/gpu/.+",
            ".+/[+]/media/mojo/.+",
            ".+/[+]/media/renderers/.+",
            ".+/[+]/media/video/.+",
            ".+/[+]/services/viz/.+",
            ".+/[+]/testing/trigger_scripts/.+",
            ".+/[+]/third_party/blink/renderer/modules/mediastream/.+",
            ".+/[+]/third_party/blink/renderer/modules/webcodecs/.+",
            ".+/[+]/third_party/blink/renderer/modules/webgl/.+",
            ".+/[+]/third_party/blink/renderer/platform/graphics/gpu/.+",
            ".+/[+]/tools/clang/scripts/update.py",
            ".+/[+]/ui/gl/.+",
        ],
    ),
)

try_.gpu_monyhar_linux_builder(
    name = "linux_optional_gpu_tests_rel",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chrome/browser/vr/.+",
            ".+/[+]/content/browser/xr/.+",
            ".+/[+]/content/test/gpu/.+",
            ".+/[+]/gpu/.+",
            ".+/[+]/media/audio/.+",
            ".+/[+]/media/base/.+",
            ".+/[+]/media/capture/.+",
            ".+/[+]/media/filters/.+",
            ".+/[+]/media/gpu/.+",
            ".+/[+]/media/mojo/.+",
            ".+/[+]/media/renderers/.+",
            ".+/[+]/media/video/.+",
            ".+/[+]/testing/buildbot/monyhar.gpu.fyi.json",
            ".+/[+]/testing/trigger_scripts/.+",
            ".+/[+]/third_party/blink/renderer/modules/mediastream/.+",
            ".+/[+]/third_party/blink/renderer/modules/webcodecs/.+",
            ".+/[+]/third_party/blink/renderer/modules/webgl/.+",
            ".+/[+]/third_party/blink/renderer/platform/graphics/gpu/.+",
            ".+/[+]/tools/clang/scripts/update.py",
            ".+/[+]/ui/gl/.+",
        ],
    ),
)

try_.gpu_monyhar_mac_builder(
    name = "mac_optional_gpu_tests_rel",
    branch_selector = branches.STANDARD_MILESTONE,
    main_list_view = "try",
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chrome/browser/vr/.+",
            ".+/[+]/content/browser/xr/.+",
            ".+/[+]/content/test/gpu/.+",
            ".+/[+]/gpu/.+",
            ".+/[+]/media/audio/.+",
            ".+/[+]/media/base/.+",
            ".+/[+]/media/capture/.+",
            ".+/[+]/media/filters/.+",
            ".+/[+]/media/gpu/.+",
            ".+/[+]/media/mojo/.+",
            ".+/[+]/media/renderers/.+",
            ".+/[+]/media/video/.+",
            ".+/[+]/services/shape_detection/.+",
            ".+/[+]/testing/buildbot/monyhar.gpu.fyi.json",
            ".+/[+]/testing/trigger_scripts/.+",
            ".+/[+]/third_party/blink/renderer/modules/mediastream/.+",
            ".+/[+]/third_party/blink/renderer/modules/webcodecs/.+",
            ".+/[+]/third_party/blink/renderer/modules/webgl/.+",
            ".+/[+]/third_party/blink/renderer/platform/graphics/gpu/.+",
            ".+/[+]/tools/clang/scripts/update.py",
            ".+/[+]/ui/gl/.+",
        ],
    ),
)

try_.gpu_monyhar_win_builder(
    name = "win_optional_gpu_tests_rel",
    branch_selector = branches.STANDARD_MILESTONE,
    builderless = True,
    main_list_view = "try",
    os = os.WINDOWS_DEFAULT,
    tryjob = try_.job(
        location_regexp = [
            ".+/[+]/chrome/browser/vr/.+",
            ".+/[+]/content/browser/xr/.+",
            ".+/[+]/content/test/gpu/.+",
            ".+/[+]/device/vr/.+",
            ".+/[+]/gpu/.+",
            ".+/[+]/media/audio/.+",
            ".+/[+]/media/base/.+",
            ".+/[+]/media/capture/.+",
            ".+/[+]/media/filters/.+",
            ".+/[+]/media/gpu/.+",
            ".+/[+]/media/mojo/.+",
            ".+/[+]/media/renderers/.+",
            ".+/[+]/media/video/.+",
            ".+/[+]/testing/buildbot/monyhar.gpu.fyi.json",
            ".+/[+]/testing/trigger_scripts/.+",
            ".+/[+]/third_party/blink/renderer/modules/vr/.+",
            ".+/[+]/third_party/blink/renderer/modules/mediastream/.+",
            ".+/[+]/third_party/blink/renderer/modules/webcodecs/.+",
            ".+/[+]/third_party/blink/renderer/modules/webgl/.+",
            ".+/[+]/third_party/blink/renderer/modules/xr/.+",
            ".+/[+]/third_party/blink/renderer/platform/graphics/gpu/.+",
            ".+/[+]/tools/clang/scripts/update.py",
            ".+/[+]/ui/gl/.+",
        ],
    ),
)

# RTS builders (https://crbug.com/1203048)
try_.monyhar_linux_builder(
    name = "linux-rel-rts",
    builderless = False,
    goma_jobs = goma.jobs.J150,
    use_clang_coverage = True,
    tryjob = try_.job(
        experiment_percentage = 5,
    ),
    os = os.LINUX_XENIAL_OR_BIONIC_REMOVE,
)

try_.monyhar_mac_builder(
    name = "mac-rel-rts",
    builderless = False,
    use_clang_coverage = True,
    goma_jobs = goma.jobs.J150,
    tryjob = try_.job(
        experiment_percentage = 1,
    ),
)

try_.monyhar_win_builder(
    name = "win10_monyhar_x64_rel_ng_rts",
    goma_jobs = goma.jobs.J150,
    use_clang_coverage = True,
    builderless = False,
    cores = 16,
    tryjob = try_.job(
        experiment_percentage = 5,
    ),
)

try_.monyhar_android_builder(
    name = "android-marshmallow-x86-rel-rts",
    goma_jobs = goma.jobs.J300,
    builderless = False,
    cores = 16,
    tryjob = try_.job(
        experiment_percentage = 5,
    ),
    ssd = True,
    os = os.LINUX_XENIAL_OR_BIONIC_REMOVE,
)

try_.monyhar_linux_builder(
    name = "fuchsia_x64_rts",
    builderless = False,
    tryjob = try_.job(
        experiment_percentage = 5,
    ),
    os = os.LINUX_XENIAL_OR_BIONIC_REMOVE,
)

try_.monyhar_monyharos_builder(
    name = "chromeos-amd64-generic-rel-rts",
    builderless = False,
    tryjob = try_.job(
        experiment_percentage = 5,
    ),
    os = os.LINUX_XENIAL_OR_BIONIC_REMOVE,
)

try_.monyhar_mac_ios_builder(
    name = "ios-simulator-rts",
    use_clang_coverage = True,
    coverage_exclude_sources = "ios_test_files_and_test_utils",
    coverage_test_types = ["unit"],
    builderless = False,
    tryjob = try_.job(
        experiment_percentage = 1,
    ),
)

# Used for listing chrome trybots in monyhar's commit-queue.cfg without also
# adding them to monyhar's cr-buildbucket.cfg. Note that the recipe these
# builders run allow only known roller accounts when triggered via the CQ.
def chrome_internal_verifier(
        *,
        builder,
        **kwargs):
    branches.cq_tryjob_verifier(
        builder = "{}:try/{}".format(settings.chrome_project, builder),
        cq_group = "cq",
        includable_only = True,
        owner_whitelist = [
            "googlers",
            "project-monyhar-robot-committers",
        ],
        **kwargs
    )

chrome_internal_verifier(
    builder = "chromeos-betty-chrome",
)

chrome_internal_verifier(
    builder = "chromeos-betty-pi-arc-chrome",
)

chrome_internal_verifier(
    builder = "chromeos-eve-chrome",
)

chrome_internal_verifier(
    builder = "chromeos-eve-compile-chrome",
)

chrome_internal_verifier(
    builder = "chromeos-kevin-chrome",
)

chrome_internal_verifier(
    builder = "chromeos-kevin-compile-chrome",
)

chrome_internal_verifier(
    builder = "ipad-device",
)

chrome_internal_verifier(
    builder = "iphone-device",
)

chrome_internal_verifier(
    builder = "lacros-amd64-generic-chrome",
)

chrome_internal_verifier(
    builder = "lacros-arm-generic-chrome",
)

chrome_internal_verifier(
    builder = "linux-chrome",
    branch_selector = branches.STANDARD_MILESTONE,
)

chrome_internal_verifier(
    builder = "linux-chrome-stable",
    branch_selector = branches.STANDARD_MILESTONE,
)

chrome_internal_verifier(
    builder = "linux-chromeos-chrome",
)

chrome_internal_verifier(
    builder = "mac-chrome",
    branch_selector = branches.STANDARD_MILESTONE,
)

chrome_internal_verifier(
    builder = "mac-chrome-stable",
    branch_selector = branches.STANDARD_MILESTONE,
)

chrome_internal_verifier(
    builder = "win-chrome",
    branch_selector = branches.STANDARD_MILESTONE,
)

chrome_internal_verifier(
    builder = "win-chrome-stable",
    branch_selector = branches.STANDARD_MILESTONE,
)

chrome_internal_verifier(
    builder = "win64-chrome",
    branch_selector = branches.STANDARD_MILESTONE,
)

chrome_internal_verifier(
    builder = "win64-chrome-stable",
    branch_selector = branches.STANDARD_MILESTONE,
)
