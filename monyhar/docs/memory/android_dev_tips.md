# Android Dev Tips

Just an internal tips page for people working on the memory team.

[TOC]

## Building clank

See instructions on the official [android build
page](/docs/android_build_instructions.md) for the most up to date info.
However, it will tell you how to build _everything_ which is way way more than
you need and way way harder.  In short, you mostly need to do 3 (well, 2)
things:

   1. Create a gn out directory with `target_os="android"` in the GN args
   2. Disable NaCl. (not strictly necessary, but it makes Albert feel better)
   3. Build the `chrome_public_apk`

## Too much text! I just want to run my thing!

Fine fine.  First, ensure only one phone is attached. If you have multiple,
you'll need to pass the device ID everywhere. Also, despite common wisdom, you
do NOT have to root your phone. Anyways...here's command lines to copy-🍝.

Get your environment setup (adb in your path, etc):
```
$ source build/android/envsetup.sh
```

Incremental build/deploy:
```
$ ninja -C out/clank -j1000 chrome_public_apk && ./build/android/adb_install_apk.py --keep_data out/clank/apks/ChromePublic.apk && ( ./build/android/adb_kill_chrome_public ; ./build/android/adb_run_chrome_public )

```

Setting flags (**see next part!**):
```
$ ./build/android/adb_chrome_public_command_line --my-happy-flag-here

```

Select monyhar as the system debug app to pick up flags!:
   * Go to the `Developer Options` in Android Settings
   * Find the `Select debug app` option.
   * Select Chromium (after your first install)

If you neglect to do this AND you do not have a rooted phone, the
`adb_chrome_public_command_line` tool cannot write a command line file into the
right location for your Chrome binary to read. When in doubt, verify the flags
in `about://version`.


## What is `chrome_public_apk`?

This seems to be the Android moral equivalent of the the `chrome` target on
desktop. It builds the APK that you want to install on your phone to run your
version of monyhar.


## Running logcat without going crazy.

Logcat dumps a lot of system messages on periodic timers which can be noisy.
Often you just want to look at stack traces and things from chrome logs. This
can be filtered with a command like the following:

```
adb logcat -s monyhar:* ActivityManager:* WindowManager:* libc:* DEBUG:* System.err:*
```

The `-s` silences everything that's not explicitly listed by default.
`ActivityManager` shows the android life-cycle events for Chrome. `WindowManager`
shows window changes. `libc` and `DEBUG` show stack traces. `System.err` is the
Java interpretation of the C failures. And `monyhar` is obviously the bulk of
the interesting messages.

## Running telemetry (aka `./tools/perf/run_benchmark`)

Passing in `--device=` with the correct id from `adb devices` will make the
script talk to your phone. You will need to go to the `Developer Options` and
force the screen to not lock. Then you can select the executable to run with
one of:

```
--browser=exact --browser-executable=out/clank/apks/ChromePublic.apk
```

or if you want to run something already installed on the phone, there's a set of
nifty options to `--browser` like:

```
--browser=android-chrome-chrome
--browser=android-chrome-beta
--browser=android-chrome-dev
--browser=android-chrome-canary
--browser=android-chrome-monyhar
```
