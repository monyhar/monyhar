.. _devcycle-debugging:

.. include:: /migration/deprecation.inc

#########
Debugging
#########

This document describes tools and techniques you can use to debug, monitor,
and measure your application's performance.

.. contents:: Table Of Contents
  :local:
  :backlinks: none
  :depth: 3

Diagnostic information
======================

Viewing process statistics with the task manager
------------------------------------------------

You can use Chrome's Task Manager to display information about a Native Client
application:

#. Open the Task Manager by clicking the menu icon |menu-icon| and choosing
   **Tools > Task manager**.
#. When the Task Manager window appears, verify that the columns displaying
   memory information are visible. If they are not, right click in the header
   row and select the memory items from the popup menu that appears.

A browser window running a Native Client application has at least two processes
associated with it: a process for the app's top level (the render process
managing the page including its HTML and JavaScript) and one or more
processes for each instance of a Native Client module embedded in the page
(each process running native code from one nexe or pexe file). The top-level
process appears with the application's icon and begins with the text "Tab:". 
A Native Client process appears with a Chrome extension icon (a jigsaw puzzle
piece |puzzle|) and begins with the text "Native Client module:" followed by the
URL of its manifest file.

From the Task Manager you can view the changing memory allocations of all the
processes associated with a Native Client application. Each process has its own
memory footprint. You can also see the rendering rate displayed as frames per
second (FPS). Note that the computation of render frames can be performed in
any process, but the rendering itself is always done in the top level
application process, so look for the rendering rate there.

Controlling the level of Native Client error and warning messages
-----------------------------------------------------------------

Native Client prints warning and error messages to stdout and stderr. You can
increase the amount of Native Client's diagnostic output by setting the
following `environment variables
<http://en.wikipedia.org/wiki/Environment_variable>`_:

* ``NACL_PLUGIN_DEBUG=1``
* ``NACL_SRPC_DEBUG=[1-255]`` (use a higher number for more verbose debug
  output)
* ``NACLVERBOSITY=[1-255]``

Basic debugging
===============

Writing messages to the JavaScript console
------------------------------------------

You can send messages from your C/C++ code to JavaScript using the 
``PostMessage()`` call in the :doc:`Pepper messaging system 
<../coding/message-system>`. When the JavaScript code receives a message, its 
message event handler can call `console.log() 
<https://developer.mozilla.org/en/DOM/console.log>`_ to write the message to the
JavaScript `console </devtools/docs/console-api>`_ in Chrome's Developer Tools.

Debugging with printf
---------------------

Your C/C++ code can perform inline printf debugging to stdout and stderr by
calling fprintf() directly, or by using cover functions like these:

.. naclcode::

  #include <stdio.h>
  void logmsg(const char* pMsg){
    fprintf(stdout,"logmsg: %s\n",pMsg);
  }
  void errormsg(const char* pMsg){
    fprintf(stderr,"logerr: %s\n",pMsg);
  }

.. _using-chromes-stdout-and-stderr:

Using Chrome's stdout and stderr Streams
----------------------------------------

By default stdout and stderr will appear in Chrome's stdout and stderr stream
but they can also be redirected to log files. (See the next section.) On Mac and
Linux, launching Chrome from a terminal makes stderr and stdout appear in that
terminal. If you launch Chrome this way, be sure it doesn't attach to an existing
instance. One simple way to do this is to pass a new directory to chrome as your
user data directory (``chrome --user-data-dir=<newdir>``). 

.. _redirecting-output-to-log:

Redirecting output to log files
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

You can redirect stdout and stderr to output files by setting these environment
variables:

* ``NACL_EXE_STDOUT=c:\nacl_stdout.log``
* ``NACL_EXE_STDERR=c:\nacl_stderr.log``

There is another variable, ``NACLLOG``, that you can use to redirect Native
Client's internally-generated messages. This variable is set to stderr by
default; you can redirect these messages to an output file by setting the
variable as follows:

* ``NACLLOG=c:\nacl.log``

.. Note::
  :class: note

  **Note:** If you set the ``NACL_EXE_STDOUT``, ``NACL_EXE_STDERR``, or
  ``NACLLOG`` variables to redirect output to a file, you must run Chrome with
  the ``--no-sandbox`` flag.  You must also be careful that each variable points
  to a different file.

Logging calls to Pepper interfaces
----------------------------------

You can log all Pepper calls your module makes by passing the following flags
to Chrome on startup::

  --vmodule=ppb*=4 --enable-logging=stderr


The ``vmodule`` flag tells Chrome to log all calls to C Pepper interfaces that
begin with "ppb" (that is, the interfaces that are implemented by the browser
and that your module calls). The ``enable-logging`` flag tells Chrome to log
the calls to stderr.

.. _visual_studio:

Debugging with Visual Studio
----------------------------

If you develop on a Windows platform you can use the :doc:`Native Client Visual
Studio add-in <vs-addin>` to write and debug your code. The add-in defines new
project platforms that let you run your module in two different modes: As a
Pepper plugin and as a Native Client module. When running as a Pepper plugin
you can use the built-in Visual Studio debugger. When running as a Native
Client module Visual Studio will launch an instance of nacl-gdb for you and
link it to the running code.

.. _using_gdb:

Debugging with nacl-gdb
-----------------------

The Native Client SDK includes a command-line debugger that you can use to
debug Native Client modules. The debugger is based on the GNU debugger `gdb
<http://www.gnu.org/software/gdb/>`_, and is located at
``pepper_<version>/toolchain/<platform>_x86_newlib/bin/x86_64-nacl-gdb`` (where
*<platform>* is the platform of your development machine: ``win``, ``mac``, or
``linux``).

Note that this same copy of GDB can be used to debug any NaCl program,
whether built using newlib or glibc for x86-32, x86-64 or ARM.  In the SDK,
``i686-nacl-gdb`` is an alias for ``x86_64-nacl-gdb``, and the ``newlib``
and ``glibc`` toolchains both contain the same version of GDB.

.. _debugging_pnacl_pexes:

Debugging PNaCl pexes (Pepper 35 or later)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you want to use GDB to debug a program that is compiled with the PNaCl
toolchain, you must have a copy of the pexe from **before** running
``pnacl-finalize``. The ``pnacl-finalize`` tool converts LLVM bitcode
to the stable PNaCl bitcode format, but it also strips out debug
metadata, which we need for debugging. In this section we'll give the
LLVM bitcode file a ``.bc`` file extension, and the PNaCl bitcode file
a ``.pexe`` file extension. The actual extension should not matter, but
it helps distinguish between the two types of files.

**Note** unlike the finalized copy of the pexe, the non-finalized debug copy
is not considered stable. This means that a debug copy of the PNaCl
application created by a Pepper N SDK is only guaranteed to run
with a matching Chrome version N. If the version of the debug bitcode pexe
does not match that of Chrome then the translation process may fail, and
you will see an error message in the JavaScript console.

Also, make sure you are passing the ``-g`` :ref:`compile option <compile_flags>`
to ``pnacl-clang`` to enable generating debugging info.  You might also want to
omit ``-O2`` from the compile-time and link-time options, otherwise GDB not
might be able to print variables' values when debugging (this is more of a
problem with the PNaCl/LLVM toolchain than with GCC).

Once you have built a non-stable debug copy of the pexe, list the URL of
that copy in your application's manifest file:

.. naclcode::

  {
    "program": {
      "portable": {
        "pnacl-translate": {
          "url": "release_version.pexe",
          "optlevel": 2
        },
        "pnacl-debug": {
          "url": "debug_version.bc",
          "optlevel": 0
        }
      }
    }
  }

Copy the ``debug_version.bc`` and ``nmf`` files to the location that
your local web server serves files from.

When you run Chrome with ``--enable-nacl-debug``, Chrome will translate
and run the ``debug_version.bc`` instead of ``release_version.pexe``.
Once the debug version is loaded, you are ready to :ref:`run nacl-gdb
<running_nacl_gdb>`

Whether you publish the NMF file containing the debug URL to the
release web server, is up to you. One reason to avoid publishing the
debug URL is that it is only guaranteed to work for the Chrome version
that matches the SDK version. Developers who may have left the
``--enable-nacl-debug`` flag turned on may end up loading the debug
copy of your application (which may or may not work, depending on
their version of Chrome).

.. _debugging_pexes_via_nexes:

Debugging PNaCl pexes (with older Pepper toolchains)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you want to use GDB to debug a program that is compiled with the PNaCl
toolchain, you must convert the ``pexe`` file to a ``nexe``.  (You can skip
this step if you are using the GCC toolchain, or if you are using
pepper 35 or later.)

* Firstly, make sure you are passing the ``-g`` :ref:`compile option
  <compile_flags>` to ``pnacl-clang`` to enable generating debugging info.
  You might also want to omit ``-O2`` from the compile-time and link-time
  options.

* Secondly, use ``pnacl-translate`` to convert your ``pexe`` to one or more

  ``nexe`` files.  For example:

  .. naclcode::
    :prettyprint: 0

    nacl_sdk/pepper_<version>/toolchain/win_pnacl/bin/pnacl-translate \
      --allow-llvm-bitcode-input hello_world.pexe -arch x86-32 \
      -o hello_world_x86_32.nexe
    nacl_sdk/pepper_<version>/toolchain/win_pnacl/bin/pnacl-translate \
      --allow-llvm-bitcode-input hello_world.pexe -arch x86-64 \
      -o hello_world_x86_64.nexe

  For this, use the non-finalized ``pexe`` file produced by
  ``pnacl-clang``, not the ``pexe`` file produced by ``pnacl-finalize``.
  The latter ``pexe`` has debugging info stripped out.  The option
  ``--allow-llvm-bitcode-input`` tells ``pnacl-translate`` to accept a
  non-finalized ``pexe``.

* Replace the ``nmf`` :ref:`manifest file <manifest_file>` that points to
  your ``pexe`` file with one that points to the ``nexe`` files.  For the
  example ``nexe`` filenames above, the new ``nmf`` file would contain:

  .. naclcode::
    :prettyprint: 0

    {
      "program": {
        "x86-32": {"url": "hello_world_x86_32.nexe"},
        "x86-64": {"url": "hello_world_x86_64.nexe"},
      }
    }

* Change the ``<embed>`` HTML element to use
  ``type="application/x-nacl"`` rather than
  ``type="application/x-pnacl"``.

* Copy the ``nexe`` and ``nmf`` files to the location that your local web
  server serves files from.

.. Note::
  :class: note

  **Note:** If you know whether Chrome is using the x86-32 or x86-64
  version of the NaCl sandbox on your system, you can translate the
  ``pexe`` once to a single x86-32 or x86-64 ``nexe``.  Otherwise, you
  might find it easier to translate the ``pexe`` to both ``nexe``
  formats as described above.

.. _running_nacl_gdb:

Running nacl-gdb
~~~~~~~~~~~~~~~~

Before you start using nacl-gdb, make sure you can :doc:`build <building>` your
module and :doc:`run <running>` your application normally. This will verify
that you have created all the required :doc:`application parts
<../coding/application-structure>` (.html, .nmf, and .nexe files, shared
libraries, etc.), that your server can access those resources, and that you've
configured Chrome correctly to run your application.  The instructions below
assume that you are using a :ref:`local server <web_server>` to run your
application; one benefit of doing it this way is that you can check the web
server output to confirm that your application is loading the correct
resources. However, some people prefer to run their application as an unpacked
extension, as described in :doc:`Running Native Client Applications <running>`.

Follow the instructions below to debug your module with nacl-gdb:

#. Compile your module with the ``-g`` flag so that your .nexe retains symbols
   and other debugging information (see the :ref:`recommended compile flags
   <compile_flags>`).
#. Launch a local web server (e.g., the :ref:`web server <web_server>` included
   in the SDK).
#. Launch Chrome with these three required flags: ``--enable-nacl --enable-nacl-debug --no-sandbox``.

   You may also want to use some of the optional flags listed below. A typical
   command looks like this::

     chrome --enable-nacl --enable-nacl-debug --no-sandbox --disable-hang-monitor localhost:5103

   **Required flags:**

   ``--enable-nacl``
     Enables Native Client for all applications, including those that are
     launched outside the Chrome Web Store.

   ``--enable-nacl-debug``
     Turns on the Native Client debug stub, opens TCP port 4014, and pauses
     Chrome to let the debugger connect.

   ``--no-sandbox``
     Turns off the Chrome sandbox (not the Native Client sandbox). This enables
     the stdout and stderr streams, and lets the debugger connect.

   **Optional flags:**

   ``--disable-hang-monitor``
     Prevents Chrome from displaying a warning when a tab is unresponsive.

   ``--user-data-dir=<directory>``
     Specifies the `user data directory
     <http://www.monyhar.org/user-experience/user-data-directory>`_ from which
     Chrome should load its state.  You can specify a different user data
     directory so that changes you make to Chrome in your debugging session do
     not affect your personal Chrome data (history, cookies, bookmarks, themes,
     and settings).

   ``--nacl-debug-mask=<nmf_url_mask1,nmf_url_mask2,...>``
     Specifies a set of debug mask patterns. This allows you to selectively
     choose to debug certain applications and not debug others. For example, if
     you only want to debug the NMF files for your applications at
     ``https://example.com/app``, and no other NaCl applications found on the
     web, specify ``--nacl-debug-mask=https://example.com/app/*.nmf``.  This
     helps prevent accidentally debugging other NaCl applications if you like
     to leave the ``--enable-nacl-debug`` flag turned on.  The pattern language
     for the mask follows `chrome extension match patterns
     </extensions/match_patterns>`_.  The pattern set can be inverted by
     prefixing the pattern set with the ``!`` character.

   ``<URL>``
     Specifies the URL Chrome should open when it launches. The local server
     that comes with the SDK listens on port 5103 by default, so the URL when
     you're debugging is typically ``localhost:5103`` (assuming that your
     application's page is called index.html and that you run the local server
     in the directory where that page is located).

#. Navigate to your application's page in Chrome. (You don't need to do this if
   you specified a URL when you launched Chrome in the previous step.) Chrome
   will start loading the application, then pause and wait until you start
   nacl-gdb and run the ``continue`` command.

#. Go to the directory with your source code, and run nacl-gdb from there. For
   example::

     cd nacl_sdk/pepper_<version>/examples/demo/drive
     nacl_sdk/pepper_<version>/toolchain/win_x86_newlib/bin/x86_64-nacl-gdb

   The debugger will start and show you a gdb prompt::

     (gdb)

#. Run the debugging command lines.

   **For PNaCl**::
   
     (gdb) target remote localhost:4014
     (gdb) remote get nexe <path-to-save-translated-nexe-with-debug-info>
     (gdb) file <path-to-save-translated-nexe-with-debug-info>
     (gdb) remote get irt <path-to-save-NaCl-integrated-runtime>
     (gdb) nacl-irt <path-to-saved-NaCl-integrated-runtime>

   **For NaCl**::
   
     (gdb) target remote localhost:4014
     (gdb) nacl-manifest <path-to-your-.nmf-file>
     (gdb) remote get irt <path-to-save-NaCl-integrated-runtime>
     (gdb) nacl-irt <path-to-saved-NaCl-integrated-runtime>

#. The command used for PNaCl and NaCl are described below:

   ``target remote localhost:4014``
     Tells the debugger how to connect to the debug stub in the Native Client
     application loader. This connection occurs through TCP port 4014 (note
     that this port is distinct from the port which the local web server uses
     to listen for incoming requests, typically port 5103). If you are
     debugging multiple applications at the same time, the loader may choose
     a port that is different from the default 4014 port. See the Chrome
     task manager for the debug port.

   ``remote get nexe <path>``
     This saves the application's main executable (nexe) to ``<path>``.
     For PNaCl, this provides a convenient way to access the nexe that is
     a **result** of translating your pexe. This can then be loaded with
     the ``file <path>`` command.

   ``nacl-manifest <path>``
     For NaCl (not PNaCl), this tells the debugger where to find your
     application's executable (.nexe) files. The application's manifest
     (.nmf) file lists your application's executable files, as well as any
     libraries that are linked with the application dynamically.

   ``remote get irt <path>``
     This saves the Native Client Integrated Runtime (IRT). Normally,
     the IRT is located in the same directory as the Chrome executable,
     or in a subdirectory named after the Chrome version. For example, if
     you're running Chrome canary on Windows, the path to the IRT typically
     looks something like ``C:/Users/<username>/AppData/Local/Google/Chrome
     SxS/Application/23.0.1247.1/nacl_irt_x86_64.nexe``.
     The ``remote get irt <path>`` saves that to the current working
     directory so that you do not need to find where exactly the IRT
     is stored.

   ``nacl-irt <path>``
     Tells the debugger where to find the Native Client Integrated Runtime
     (IRT). ``<path>`` can either be the location of the copy saved by
     ``remote get irt <path>`` or the copy that is installed alongside Chrome.

   A couple of notes on how to specify path names in the nacl-gdb commands
   above:

   * You can use a forward slash to separate directories on Linux, Mac, and
     Windows. If you use a backslash to separate directories on Windows, you
     must escape the backslash by using a double backslash "\\" between
     directories.
   * If any directories in the path have spaces in their name, you must put
     quotation marks around the path.

   As an example, here is a what these nacl-gdb commands might look like on
   Windows::

     target remote localhost:4014
     nacl-manifest "C:/nacl_sdk/pepper_<version>/examples/hello_world_gles/newlib/Debug/hello_world_gles.nmf"
     nacl-irt "C:/Users/<username>/AppData/Local/Google/Chrome SxS/Application/23.0.1247.1/nacl_irt_x86_64.nexe"

   To save yourself some typing, you can put put these nacl-gdb commands in a
   script file, and execute the file when you run nacl-gdb, like so::

     nacl_sdk/pepper_<version>/toolchain/win_x86_newlib/bin/x86_64-nacl-gdb -x <nacl-script-file>

   If nacl-gdb connects successfully to Chrome, it displays a message such as
   the one below, followed by a gdb prompt::

     0x000000000fc00200 in _start ()
     (gdb)

   If nacl-gdb can't connect to Chrome, it displays a message such as
   "``localhost:4014: A connection attempt failed``" or "``localhost:4014:
   Connection timed out.``" If you see a message like that, make sure that you
   have launched a web server, launched Chrome, and navigated to your
   application's page before starting nacl-gdb.

Once nacl-gdb connects to Chrome, you can run standard gdb commands to execute
your module and inspect its state. Some commonly used commands are listed
below.

``break <location>``
  set a breakpoint at <location>, e.g.::

    break hello_world.cc:79
    break hello_world::HelloWorldInstance::HandleMessage
    break Render

``continue``
  resume normal execution of the program

``next``
  execute the next source line, stepping over functions

``step``
  execute the next source line, stepping into functions

``print <expression>``
  print the value of <expression> (e.g., variables)

``backtrace``
  print a stack backtrace

``info breakpoints``
  print a table of all breakpoints

``delete <breakpoint>``
  delete the specified breakpoint (you can use the breakpoint number displayed
  by the info command)

``help <command>``
  print documentation for the specified gdb <command>

``quit``
  quit gdb

See the `gdb documentation
<http://sourceware.org/gdb/current/onlinedocs/gdb/#toc_Top>`_ for a
comprehensive list of gdb commands. Note that you can abbreviate most commands
to just their first letter (``b`` for break, ``c`` for continue, and so on).

To interrupt execution of your module, press <Ctrl-c>. When you're done
debugging, close the Chrome window and type ``q`` to quit gdb.

Debugging with other tools
==========================

If you cannot use the :ref:`Visual Studio add-in <visual_studio>`, or you want
to use a debugger other than nacl-gdb, you must manually build your module as a
Pepper plugin (sometimes referred to as a "`trusted
<http://www.monyhar.org/nativeclient/getting-started/getting-started-background-and-basics#TOC-Trusted-vs-Untrusted>`_"
or "in-process" plugin).  Pepper plugins (.DLL files on Windows; .so files on
Linux; .bundle files on Mac) are loaded directly in either the Chrome renderer
process or a separate plugin process, rather than in Native Client. Building a
module as a trusted Pepper plugin allows you to use standard debuggers and
development tools on your system, but when you're finished developing the
plugin, you need to port it to Native Client (i.e., build the module with one of
the toolchains in the NaCl SDK so that the module runs in Native Client).  For
details on this advanced development technique, see `Debugging a Trusted Plugin
<http://www.monyhar.org/nativeclient/how-tos/debugging-documentation/debugging-a-trusted-plugin>`_.
Note that starting with the ``pepper_22`` bundle, the NaCl SDK for Windows
includes pre-built libraries and library source code, making it much easier to
build a module into a .DLL.

.. |menu-icon| image:: /images/menu-icon.png
.. |puzzle| image:: /images/puzzle.png
