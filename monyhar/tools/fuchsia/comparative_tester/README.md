# Comparative Testing Scripts for Fuchsia

The collection of python scripts inside of `//tools/fuchsia/comparative_tester`
exist to facilitate the automated building, deployment, and execution of tests
on Linux and Fuchsia devices on the same LAN as the hosting PC which will run
the scripts, as well as generating comparisons and valuable statistical data and
displaying it in an easily viewed form.

## Test Support
The automated test building and launching should currently work for any test
target within the base `monyhar/src` directory. Work is also underway to
support executing Catapult tests automatically, with the same stats collection
capabilities.

## Usage
The general usage pattern for these scripts is as follows:
1. Check `target_spec.py`. Make sure that the output directories are where you
      want them, and that you are running the monyhar test targets that you're
      interested in collecting results from. Also check to make sure that the
      specifications for the Linux and Fuchsia devices are appropriate for your
      specific network and OS configuration.
2. Execute `comparative_tester.py`, with any flags necessary to collect the data
      of interest.
3. Run `generate_perf_report.py`. This should require no extra configuration on
      your part.
4. View your results by loading `display_perf_results.html` in any web browser
      and giving it the result JSONs in the output directory you specified.

### test_results.py
(_Non-Invokable_)

This file is used internally to parse test output and return objects that can be
manipulated easily by the rest of the code.

### target_spec.py
(_Non-Invokable_)

This file contains constant definitions used by other files in this folder to
represent what tests to run, where the output will live, where the test binaries
live, and so on. To add more tests for automatic building and deploying, they
should be added here.

### comparative_tester.py
_Invocation_: `comparative_tester.py --do-config --do-build --is-debug --num-repetitions=1`

This is where tests are actually executed. It has four flags of note:
  * `--do-config`: makes the test script to generate an args.gn file for
        the output directory, and over-writes any existing `args.gn` file. This
        option is off by default, and no files will be generated or changed.
      * `--is-debug`: requires the do_config flag to be set as well. Makes
            the args.gn files that the script will generate have the
            `is_debug = true` line. This option is off by default, and will
            cause the line `is_debug = false` to appear in the `args.gn` file
  * `--do-build`: makes the test script build the targets specified inside of
        `target_spec.py`. By default, this is off, and the targets will not be
        rebuilt for the test cases.
  * `--num-repetitions`: tells the script how many times to run each test in the
        battery of tests specified in `target_spec.py`. By default, this is one,
        so tests will only be executed one time each.
  * `--gtest_filter`: Works just like it does on the tests. Positive filters go
        before the '-' and negative ones after. All individual filters are
        separated from each other via a ':'. Support globbing.
      * Ex: `--gtest_filter=TestFoo.Case1:TestBar.*-TestBaz.*:TestQuux.Case2`
            filters out all cases in TestBaz, Case2 of TestQuux, and only runs
            TestFoo.Case1, and all cases in TestBar.

More complex configuration options are present in `target_spec.py.`

### generate_perf_report.py
_Invocation_: `generate_perf_report.py`
This script takes no command line flags, but works off of many of the same
fields inside of `target_spec.py` that `comparative_tester.py` does, namely
fields instructing it where the raw data lives, and where to place the generated
statistics when it's finished generating them. It generates the mean, standard
deviation, and coefficient of variance for each target, test, and individual
line scraped from the test output, and writes them to appropriately named JSONs.

### display_perf_results.html and .js
The HTML file is just a thin shim around `display_perf_results.js` that can be
opened in any web browser to view the data. Due to the isolation between
javascript interpreters and the host's filesystem, the web page must be manually
given the final JSON files that you want to display, at which point it will draw
a table full of the data, and in a more human readable format. It accepts
multiple files at a time.