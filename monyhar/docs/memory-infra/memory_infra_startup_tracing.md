# Startup Tracing with MemoryInfra

[MemoryInfra](README.md) supports startup tracing.

## The Simple Way

Start Chrome as follows:

    $ chrome --no-sandbox \
             --trace-startup=-*,disabled-by-default-memory-infra \
             --trace-startup-file=/tmp/trace.json \
             --trace-startup-duration=7

On Android, first ensure that Chrome can write output files to storage. Replace
"org.monyhar.chrome" with the Chrome package you are tracing:

    $ adb shell pm grant org.monyhar.chrome android.permission.READ_EXTERNAL_STORAGE
    $ adb shell pm grant org.monyhar.chrome android.permission.WRITE_EXTERNAL_STORAGE

Then enable startup tracing and start Chrome as follows:

    $ build/android/adb_chrome_public_command_line \
          --trace-startup=-*,disabled-by-default-memory-infra \
          --trace-startup-file=/sdcard/Download/trace.json \
          --trace-startup-duration=7

    $ build/android/adb_run_chrome_public

    $ adb pull /sdcard/Download/trace.json  # After tracing.

Note that startup tracing will be enabled upon every Chrome launch until you
delete the command-line flags:

    $ build/android/adb_chrome_public_command_line ""

This will use the default configuration: one memory dump every 250 ms with a
detailed dump ever two seconds.

## The Advanced Way

If you need more control over the granularity of the memory dumps, you can
specify a custom trace config file as follows:

    $ cat > /tmp/trace.config
    {
      "startup_duration": 4,
      "result_file": "/tmp/trace.json",
      "trace_config": {
        "included_categories": ["disabled-by-default-memory-infra"],
        "excluded_categories": ["*"],
        "memory_dump_config": {
          "triggers": [
            { "mode": "light", "periodic_interval_ms": 50 },
            { "mode": "detailed", "periodic_interval_ms": 1000 }
          ]
        }
      }
    }

    $ chrome --no-sandbox --trace-config-file=/tmp/trace.config

On Android, the config file has to be pushed to a fixed file location:

    $ adb root
    $ adb push /tmp/trace.config /data/local/chrome-trace-config.json

    $ build/android/adb_run_chrome_public

    $ adb pull /sdcard/Download/trace.json  # After tracing.

Make sure that the "result_file" location is writable by the Chrome process on
Android (e.g. "/sdcard/Download/trace.json"). To ensure Chrome has permissions
to write to /sdcard, run the following, replacing "org.monyhar.chrome" with
the Chrome package you are tracing:

    $ adb shell pm grant org.monyhar.chrome android.permission.READ_EXTERNAL_STORAGE
    $ adb shell pm grant org.monyhar.chrome android.permission.WRITE_EXTERNAL_STORAGE

Note that startup tracing will be enabled upon every Chrome launch until you
delete the config file:

    $ adb shell rm /data/local/chrome-trace-config.json

## Related Pages

 * [General information about startup tracing](https://sites.google.com/a/monyhar.org/dev/developers/how-tos/trace-event-profiling-tool/recording-tracing-runs)
 * [Memory tracing with MemoryInfra](README.md)
