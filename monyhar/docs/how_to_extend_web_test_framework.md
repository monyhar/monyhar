# How to Extend the Web Test Framework

The Web Test Framework that Blink uses is a regression testing tool that is
multi-platform and it has a large amount of tools that help test varying types
of regression, such as pixel diffs, text diffs, etc. The framework is mainly
used by Blink, however it was made to be extensible so that other projects can
use it test different parts of chrome (such as Print Preview). This is a guide
to help people who want to actually the framework to test whatever they want.

[TOC]

## Background

Before you can start actually extending the framework, you should be familiar
with how to use it. See the [web tests documentation](testing/web_tests.md).

## How to Extend the Framework

There are two parts to actually extending framework to test a piece of software.
The first part is extending certain files in:
[/third_party/blink/tools/blinkpy/web_tests/](/third_party/blink/tools/blinkpy/web_tests/)
The code in `blinkpy/web_tests` is the web test framework itself

The second part is creating a driver (program) to actually communicate the
web test framework. This part is significantly more tricky and dependent on
what exactly exactly is being tested.

### Part 1

This part isn’t too difficult. There are basically two classes that need to be
extended (ideally, just inherited from). These classes are:

*   `Driver`. Located in `web_tests/port/driver.py`. Each instance of this is
    the class that will actually an instance of the program that produces the
    test data (program in Part 2).
*   `Port`. Located in `web_tests/port/base.py`. This class is responsible
    creating drivers with the correct settings, giving access to certain OS
    functionality to access expected files, etc.

#### Extending Driver

As said, Driver launches the program from Part 2. Said program will communicate
with the driver class to receive instructions and send back data. All of the
work for driver gets done in `Driver.run_test`. Everything else is a helper or
initialization function.

`run_test()` steps:

1.  On the very first call of this function, it will actually run the test
    program. On every subsequent call to this function, at the beginning it will
    verify that the process doesn’t need to be restarted, and if it does, it
    will create a new instance of the test program.
1.  It will then create a command to send the program
    *   This command generally consists of an html file path for the test
        program to navigate to.
    *   After creating it, the command is sent
1.  After the command has been sent, it will then wait for data from the
    program.
    *   It will actually wait for 2 blocks of data.
        *   The first part being text or audio data. This part is required (the
            program will always send something, even an empty string)
        *   The second block is optional and is image data and an image hash
            (md5) this block of data is used for pixel tests
1.  After it has received all the data, it will proceed to check if the program
    has timed out or crashed, and if so fail this instance of the test (it can
    be retried later if need be).

Luckily, `run_test()` most likely doesn’t need to be overridden unless extra
blocks of data need to be sent to/read from the test program. However, you do
need to know how it works because it will influence what functions you need to
override. Here are the ones you’re probably going to need to override

    cmd_line

This function creates a set of command line arguments to run the test program,
so the function will almost certainly need to be overridden.

It creates the command line to run the program. `Driver` uses `subprocess.popen`
to create the process, which takes the name of the test program and any options
it might need.

The first item in the list of arguments should be the path to test program using
this function:

    self._port._path_to_driver()

This is an absolute path to the test program. This is the bare minimum you need
to get the driver to launch the test program, however if you have options you
need to append, just append them to the list.

    start

If your program has any special startup needs, then this will be the place to
put it.

That’s mostly it. The Driver class has almost all the functionality you could
want, so there isn’t much to override here. If extra data needs to be read or
sent, extra data members should be added to `ContentBlock`.

#### Extending Port

This class is responsible for providing functionality such as where to look for
tests, where to store test results, what driver to run, what timeout to use,
what kind of files can be run, etc. It provides a lot of functionality, however
it isn’t really sufficient because it doesn’t account of platform specific
problems, therefore port itself shouldn’t be extend. Instead LinuxPort, WinPort,
and MacPort (and maybe the android port class) should be extended as they
provide platform specific overrides/extensions that implement most of the
important functionality. While there are many functions in Port, overriding one
function will affect most of the other ones to get the desired behavior. For
example, if `web_tests_dir()` is overridden, not only will the code look for
tests in that directory, but it will find the correct TestExpectations file, the
platform specific expected files, etc.

Here are some of the functions that most likely need to be overridden.

*   `driver_class`
    *   This should be overridden to allow the testing program to actually run.
        By default the code will run content_shell, which might or might not be
        what you want.
    *   It should be overridden to return the driver extension class created
        earlier. This function doesn’t return an instance on the driver, just
        the class itself.
*   `driver_name`
    *   This should return the name of the program test p. By default it returns
        ‘content_shell’, but you want to have it return the program you want to
        run, such as `chrome` or `browser_tests`.
*   `web_tests_dir`
    *   This tells the port where to look for all the and everything associated
        with them such as resources files.
    *   By default it returns the absolute path to the web tests directory.
    *   If you are planning on running something in the monyhar src/ directory,
        there are helper functions to allow you to return a path relative to the
        base of the monyhar src directory.

The rest of the functions can definitely be overridden for your projects
specific needs, however these are the bare minimum needed to get it running.
There are also functions you can override to make certain actions that aren’t on
by default always take place. For example, the web test framework always
checks for system dependencies unless you pass in a switch. If you want them
disabled for your project, just override `check_sys_deps` to always return OK.
This way you don’t need to pass in so many switches.

As said earlier, you should override LinuxPort, MacPort, and/or WinPort. You
should create a class that implements the platform independent overrides (such
as `driver_class`) and then create a separate class for each platform specific
port of your program that inherits from the class with the independent overrides
and the platform port you want. For example, you might want to have a different
timeout for your project, but on Windows the timeout needs to be vastly
different than the others. In this case you can just create a default override
that every class uses except your Windows port. In that port you can just
override the function again to provide the specific timeout you need. This way
you don’t need to maintain the same function on each platform if they all do the
same thing.

For `Driver` and `Port` that’s basically it unless you need to make many odd
modifications. Lots of functionality is already there so you shouldn’t really
need to do much.

### Part 2

This is the part where you create the program that your driver class launches.
This part is very application dependent, so it will not be a guide on how
implement certain features, just what should be implemented and the order in
which events should occur and some guidelines about what to do/not do. For a
good example of how to implement your test program, look at MockDRT in
`mock_drt.pyin` the same directory as `base.py` and `driver.py`. It goes through
all the steps described below and is very clear and concise. It is written in
python, but your driver can be anything that can be run by `subprocess.popen`
and has stdout, stdin, stderr.

#### Goals

Your goal for this part of the project is to create a program (or extend a
program) to interface with the web test framework. The web test framework
will communicate with this program to tell it what to do and it will accept data
from this program to perform the regression testing or create new base line
files.

#### Structure

This is how your code should be laid out.

1.  Initialization
    *   The creation of any directories or the launching of any programs should
        be done here and should be done once.
    *   After the program is initialized, “#READY\n” should be sent to progress
        the `run_test()` in the driver.
1.  Infinite Loop (!)
    *   After initialization, your program needs to actually wait for input,
        then process that input to carry out the test. In the context of web
        testing, the `content_shell` needs to wait for an html file to navigate
        to, render it, then convert that rendering to a PNG. It does this
        constantly, until a signal/message is sent to indicate that no more
        tests should be processed
    *   Details:
        *   The first thing you need is your test file path and any other
            additional information about the test that is required (this is sent
            during the write() step in `run_tests()` is `driver.py`. This
            information will be passed through stdin and is just one large
            string, with each part of the command being split with apostrophes
            (ex: “/path’foo” is path to the test file, then foo is some setting
            that your program might need).
        *   After that, your program should act on this input, how it does this
            is dependent on your program, however in `content_shell`, this would
            be the part where it navigates to the test file, then renders it.
            After the program acts on the input, it needs to send some text to
            the driver code to indicate that it has acted on the input. This
            text will indicate something that you want to test. For example, if
            you want to make sure you program always prints “foo” you should
            send it to the driver. If the program every prints “bar” (or
            anything else), that would indicate a failure and the test will
            fail.
        *   Then you need to send any image data in the same manner as you did
            for step 2.
        *   Cleanup everything related to processing the input from step i, then
            go back to step 1.
            *   This is where the ‘infinite’ loop part comes in, your program
            should constantly accept input from the driver until the driver
            indicates that there are no more tests to run. The driver does this
            by closing stdin, which will cause std::cin to go into a bad state.
            However, you can also modify the driver to send a special string
            such as ‘QUIT’ to exit the while loop.

That’s basically what the skeleton of your program should be.

### Details

This is information about how to do some specific things, such as sending data
to the web test framework.

*   Content Blocks
    *   The web test framework accepts output from your program in blocks of
        data through stdout. Therefore, printing to stdout is really sending
        data to the web test framework.
    *   Structure of block
        *   “Header: Data\n”
            *   Header indicates what type of data will be sent through. A list
                of valid headers is listed in `Driver.py`.
            *   Data is the data that you actually want to send. For pixel
                tests, you want to send the actual PNG data here.
            *   The newline is needed to indicate the end of a header.
        * End of a content block
            *   To indicate the end of a a content block and cause the driver to
                progress, you need to write “#EOF\n” to stdout (mandatory) and
                to stderr for certain types of content, such as image data.
        * Multiple headers per block
            *   Some blocks require different sets of data. For PNGs, not only
                is the PNG needed, but so is a hash of the bitmap used to create
                the PNG.
            *   In this case this is how your output should look.
                *   “Content-type: image/png\n”
                *   “ActualHash: hashData\n”
                *   “Content-Length: lengthOfPng\n”
                *   “pngdata”
                    *   This part doesn’t need a header specifying that you are
                        sending png data, just send it
                *   “#EOF\n” on both stdout and stderr
            *   To see the structure of the data required, look at the
                `read_block` functions in Driver.py
