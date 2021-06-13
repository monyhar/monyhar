.. include:: /migration/deprecation.inc

######################################
Dynamic Linking and Loading with glibc
######################################

.. contents::
  :local:
  :backlinks: none
  :depth: 2

.. Note::
  :class: caution

  Portable Native Client currently only supports static linking, and the
  only C library available for it is newlib. This page is only valid for
  Native Client, though PNaCl will eventually support some form of
  dynamic linking.

This document describes how to create and deploy dynamically linked and loaded
applications with the glibc library in the Native Client SDK. Before reading
this document, we recommend reading :doc:`Building Native Client Modules
<building>`

.. _c_libraries:

C standard libraries: glibc and newlib
--------------------------------------

The Native Client SDK comes with two C standard libraries --- glibc and
newlib. These libraries are described in the table below.

+-----------------------------------------------------+----------+-------------+
| Library                                             | Linking  | License     |
+=====================================================+==========+=============+
|glibc                                                | dynamic  | GNU Lesser  |
|  The GNU implementation of the POSIX_ standard      | or static| General     |
|  runtime library for the C programming language.    |          | Public      |
|  Designed for portability and performance, glibc is |          | License     |
|  one of the most popular implementations of the C   |          | (LGPL)      |
|  library. It is comprised of a set of interdependent|          |             |
|  libraries including libc, libpthreads, libdl, and  |          |             |
|  others. For documentation, FAQs, and additional    |          |             |
|  information about glibc, see GLIBC_.               |          |             |
+-----------------------------------------------------+----------+-------------+
|newlib                                               | static   | Berkeley    |
|  newlib is a C library intended for use in embedded |          | Software    |
|  systems. Like glibc, newlib is a conglomeration of |          | Distribution|
|  several libraries. It is available for use under   |          | (BSD) type  |
|  BSD-type free software licenses, which generally   |          | free        |
|  makes it more suitable to link statically in       |          | software    |
|  commercial, closed-source applications. For        |          | licenses    |
|  documentation, FAQs, and additional information    |          |             |
|  about newlib, see newlib_.                         |          |             |
+-----------------------------------------------------+----------+-------------+


For proprietary (closed-source) applications, your options are to either
statically link to newlib, or dynamically link to glibc. We recommend
dynamically linking to glibc, for a couple of reasons:

* The glibc library is widely distributed (it's included in Linux
  distributions), and as such it's mature, hardened, and feature-rich. Your
  code is more likely to compile out-of-the-box with glibc.

* Dynamic loading can provide a big performance benefit for your application if
  you can structure the application to defer loading of code that's not needed
  for initial interaction with the user. It takes some work to put such code in
  shared libraries and to load the libraries at runtime, but the payoff is
  usually worth it. In future releases, Chrome may also support caching of
  common dynamically linked libraries such as libc.so between applications.
  This could significantly reduce download size and provide a further potential
  performance benefit (for example, the hello_world example would only require
  downloading a .nexe file that's on the order of 30KB, rather than a .nexe
  file and several libraries, which are on the order of 1.5MB).

Native Client support for dynamic linking and loading is based on glibc. Thus,
**if your Native Client application must dynamically link and load code (e.g.,
due to licensing considerations), we recommend that you use the glibc
library.**

.. Note::
  :class: note

  **Disclaimer:**

  * **None of the above constitutes legal advice, or a description of the legal
    obligations you need to fulfill in order to be compliant with the LGPL or
    newlib licenses. The above description is only a technical explanation of
    the differences between newlib and glibc, and the choice you must make
    between the two libraries.**



.. Note::
  :class: note

  **Notes:**

  * Static linking with glibc is rarely used. Use this feature with caution.

  * The standard C++ runtime in Native Client is provided by libstdc++; this
    library is independent from and layered on top of glibc. Because of
    licensing restrictions, libstdc++ must be statically linked for commercial
    uses, even if the rest of an application is dynamically linked.

SDK toolchains
--------------

The Native Client SDK contains multiple toolchains, which are differentiated by
:ref:`target architecture <target_architectures>` and C library:

=================== ========= ===============================
Target architecture C library Toolchain directory
=================== ========= ===============================
x86                 glibc     toolchain/<platform>_x86_glibc
ARM                 glibc     toolchain/<platform>_arm_glibc
x86                 newlib    toolchain/<platform>_pnacl
ARM                 newlib    toolchain/<platform>_pnacl
PNaCl               newlib    toolchain/<platform>_pnacl
=================== ========= ===============================

In the directories listed above, <platform> is the platform of your development
machine (i.e., win, mac, or linux). For example, in the Windows SDK, the x86
toolchain that uses glibc is in ``toolchain/win_x86_glibc``.

.. Note::
  :class: note

  **Note:** The PNaCl toolchain is currently restricted to newlib.

To use the glibc library and dynamic linking in your application, you **must**
use a glibc toolchain.  Note that you must build all code in your application
with one toolchain. Code from multiple toolchains cannot be mixed.

Specifying and delivering shared libraries
------------------------------------------

One significant difference between newlib and glibc applications is that glibc
applications must explicitly list and deploy the shared libraries that they
use.

In a desktop environment, when the user launches a dynamically linked
application, the operating system's program loader determines the set of
libraries the application requires by reading explicit inter-module
dependencies from executable file headers, and loads the required libraries
into the address space of the application process. Typically the required
libraries will have been installed on the system as a part of the application's
installation process. Often the desktop application developer doesn't know or
think about the libraries that are required by an application, as those details
are taken care of by the user's operating system.

In the Native Client sandbox, dynamic linking can't rely in the same way on the
operating system or the local file system. Instead, the application developer
must identify the set of libraries that are required by an application, list
those libraries in a Native Client :ref:`manifest file <manifest_file>`, and
deploy the libraries along with the application. Instructions for how to build
a dynamically linked Native Client application, generate a Native Client
manifest (.nmf) file, and deploy an application are provided below.

Building a dynamically linked application
=========================================

Applications built with the glibc toolchain will by dynamically linked by
default. Application that load shared libraries at runtime using ``dlopen()``
must link with the libdl library (``-ldl``).

Like other gcc-based toolchains building a dynamic library for NaCl is normally
done by linking with the ``-shared`` flag and compiling with the ``-fPIC`` flag.
The SDK build system will do this automatically when the ``SO_RULE`` Makefile
rule is used.

The Native Client SDK includes an example that demonstrates how to build a
shared library, and how to use the ``dlopen()`` interface to load that library
at runtime (after the application is already running). Many applications load
and link shared libraries at launch rather than at runtime, and hence do not
use the ``dlopen()`` interface. The SDK example is nevertheless instructive, as
it demonstrates how to build Native Client modules (.nexe files) and shared
libraries (.so files) with the x86 glibc toolchain, and how to generate a
Native Client manifest file for glibc applications.

The SDK example, located in ``examples/tutorial/dlopen``, includes three C++
files:

eightball.cc
  This file implements the function ``Magic8Ball()``, which is used to provide
  whimsical answers to user questions. This file is compiled into a shared
  library called ``libeightball.so``. This library gets included in the
  .nmf file and is therefore directly loadable with ``dlopen()``.

reverse.cc
  This file implements the function ``Reverse()``, which returns reversed
  copies of strings that are passed to it. This file is compiled into a shared
  library called ``libreverse.so``. This library is **not** included in the
  .nmf file and is loaded via an http mount using the :ref:`nacl_io library
  <nacl_io>`.

dlopen.cc
  This file implements the Native Client module, which loads the two shared
  libraries and handles communcation with with JavaScript. The file is compiled
  into a Native Client executable (.nexe).

Run ``make`` in the dlopen directory to see the commands the Makefile executes
to build x86 32-bit and 64-bit .nexe and .so files, and to generate a .nmf
file. These commands are described below.

.. Note::
  :class: note

  **Note:** The Makefiles for most of the examples in the SDK build the
  examples using multiple toolchains (x86 newlib, x86 glibc, ARM newlib, ARM
  glibc, and PNaCl).  With a few exceptions (listed in the :ref:`Release Notes
  <sdk-release-notes>`), running "make" in each example's directory builds
  multiple versions of the example using the SDK toolchains. The dlopen example
  is one of those exceptions – it is only built with the x86 glibc toolchain,
  as that is currently the only toolchain that supports glibc and thus dynamic
  linking and loading. Take a look at the example Makefiles and the generated
  .nmf files for details on how to build dynamically linked applications.

.. _dynamic_loading_manifest:

Generating a Native Client manifest file for a dynamically linked application
=============================================================================

The Native Client manifest file specifies the name of the executable to run
and must also specify any shared libraries that the application directly
depends on. For indirect dependencies (such as libraries opened via
``dlopen()``) it is also convenient to list libraries in the manifest file.
However it is possile to load arbitrary shared libraries at runtime that
are not mentioned in the manifest by using the `nacl_io library <nacl_io>`_
to mount a filesystem that contains the shared libraries which will then
allow ``dlopen()`` to access them.

In this example we demonstrate both loading directly from via the manifest
file (``libeightball.so``) and loading indirectly via a http mount
(``libreverse.so``).

Take a look at the manifest file in the dlopen example to see how
a glibc-style manifest file is structured. (Run ``make`` in the dlopen directory to
generate the manifest file if you haven't done so already.) Here is an excerpt
from ``dlopen.nmf``::

  {
    "files": {
      "libeightball.so": {
        "x86-64": {
          "url": "lib64/libeightball.so"
        },
        "x86-32": {
          "url": "lib32/libeightball.so"
        }
      },
      "libstdc++.so.6": {
        "x86-64": {
          "url": "lib64/libstdc++.so.6"
        },
        "x86-32": {
          "url": "lib32/libstdc++.so.6"
        }
      },
      "libppapi_cpp.so": {
        "x86-64": {
          "url": "lib64/libppapi_cpp.so"
        },
        "x86-32": {
          "url": "lib32/libppapi_cpp.so"
        }
      },
  ... etc.

In most cases, you can use the ``create_nmf.py`` script in the SDK to generate
a manifest file for your application. The script is located in the tools
directory (e.g. ``pepper_28/tools``).

The Makefile in the dlopen example generates the manifest automatically using
the ``NMF_RULE`` provided by the SDK build system. Running ``make V=1`` will
show the full command line which is used to generate the nmf::

  create_nmf.py -o dlopen.nmf glibc/Release/dlopen_x86_32.nexe \
     glibc/Release/dlopen_x86_64.nexe glibc/Release/libeightball_x86_32.so \
     glibc/Release/libeightball_x86_64.so  -s ./glibc/Release \
     -n libeightball_x86_32.so,libeightball.so \
     -n libeightball_x86_64.so,libeightball.so

Run python ``create_nmf.py --help`` to see a full description of the command-line
flags. A few of the important flags are described below.

``-s`` *directory*
  use *directory* to stage libraries (libraries are added to ``lib32`` and
  ``lib64`` subfolders)

``-L`` *directory*
  add *directory* to the library search path. The default search path
  already includes the toolchain and SDK libraries directories.

.. Note::
  :class: note

  **Note:** The ``create_nmf`` script can only automatically detect explicit
  shared library dependencies (for example, dependencies specified with the -l
  flag for the compiler/linker). If you want to include libraries that you
  intend to dlopen() at runtime you must explcitly list them in your call to
  ``create_nmf``.

As an alternative to using ``create_nmf``, it is possible to manually calculate
the list of shared library dependencies using tools such as ``objdump_``.

Deploying a dynamically linked application
==========================================

As described above, an application's manifest file must explicitly list all the
executable code modules that the application directly depends on, including
modules from the application itself (``.nexe`` and ``.so`` files), modules from
the Native Client SDK (e.g., ``libppapi_cpp.so``), and perhaps also modules from
`webports <https://monyhar.googlesource.com/webports>`_ or from `middleware 
systems <../../community/middleware>`_ that the application uses. You must
provide all of those modules as part of the application deployment process.

As explained in :doc:`Distributing Your Application <../distributing>`, there
are two basic ways to deploy a `Chrome app </apps>`_:

* **hosted application:** all modules are hosted together on a web server of
  your choice

* **packaged application:** all modules are packaged into one file, hosted in
  the Chrome Web Store, and downloaded to the user's machine

The web store documentation contains a handy guide to `help you choose which to
use <https://developer.chrome.com/webstore/choosing>`_.

You must deploy all the modules listed in your application's manifest file for
either the hosted application or the packaged application case. For hosted
applications, you must upload the modules to your web server. For packaged
applications, you must include the modules in the application's Chrome Web Store
.crx file. Modules should use URLs/names that are consistent with those in the
Native Client manifest file, and be named relative to the location of the
manifest file. Remember that some of the libraries named in the manifest file
may be located in directories you specified with the ``-L`` option to
``create_nmf.py``. You are free to rename/rearrange files and directories
referenced by the Native Client manifest file, so long as the modules are
available in the locations indicated by the manifest file. If you move or rename
modules, it may be easier to re-run ``create_nmf.py`` to generate a new manifest
file rather than edit the original manifest file. For hosted applications, you
can check for name mismatches during testing by watching the request log of the
web server hosting your test deployment.

Opening a shared library at runtime
===================================

Native Client supports a version of the POSIX standard ``dlopen()`` interface
for opening libraries explicitly, after an application is already running.
Calling ``dlopen()`` may cause a library download to occur, and automatically
loads all libraries that are required by the named library.

.. Note::
  :class: note

  **Caution:** Since ``dlopen()`` can potentially block, you must initially
  call ``dlopen()`` off your application's main thread. Initial calls to
  ``dlopen()`` from the main thread will always fail in the current
  implementation of Native Client.

The best practice for opening libraries with ``dlopen()`` is to use a worker
thread to pre-load libraries asynchronously during initialization of your
application, so that the libraries are available when they're needed. You can
call ``dlopen()`` a second time when you need to use a library -- per the
specification, subsequent calls to ``dlopen()`` return a handle to the
previously loaded library. Note that you should only call ``dlclose()`` to
close a library when you no longer need the library; otherwise, subsequent
calls to ``dlopen()`` could cause the library to be fetched again.

The dlopen example in the SDK demonstrates how to open a shared libraries
at runtime. To reiterate, the example includes three C++ files:

* ``eightball.cc``: this is the shared library that implements the function
  ``Magic8Ball()`` (this file is compiled into libeightball.so)
* ``reverse.cc``: this is the shared library that implements the function
  ``Reverse()`` (this file is compiled into libreverse.so)
* ``dlopen.cc``: this is the Native Client module that loads the shared libraries
  and makes calls to ``Magic8Ball()`` and ``Reverse()`` in response to requests
  from JavaScript.

When the Native Client module starts, it kicks off a worker thread that calls
``dlopen()`` to load the two shared libraries. Once the module has a handle to
the library, it fetches the addresses of the ``Magic8Ball()`` and ``Reverse()``
functions using ``dlsym()``. When a user types in a query and clicks the 'ASK!'
button, the module calls ``Magic8Ball()`` to generate an answer, and returns
the result to the user. Likewise when the user clicks the 'Reverse' button
it calls the ``Reverse()`` function to reverse the string.

Troubleshooting
===============

If your .nexe isn't loading, the best place to look for information that can
help you troubleshoot the JavaScript console and standard output from Chrome.
See :ref:`Debugging <devcycle-debugging>` for more information.

Here are a few common error messages and explanations of what they mean:

**/main.nexe: error while loading shared libraries: /main.nexe: failed to allocate code and data space for executable**
  The .nexe may not have been compiled correctly (e.g., the .nexe may be
  statically linked). Try cleaning and recompiling with the glibc toolchain.

**/main.nexe: error while loading shared libraries: libpthread.so.xxxx: cannot open shared object file: Permission denied**
  (xxxx is a version number, for example, 5055067a.) This error can result from
  having the wrong path in the .nmf file. Double-check that the path in the
  .nmf file is correct.

**/main.nexe: error while loading shared libraries: /main.nexe: cannot open shared object file: No such file or directory**
  If there are no obvious problems with your main.nexe entry in the .nmf file,
  check where main.nexe is being requested from. Use Chrome's Developer Tools:
  Click the menu icon |menu-icon|, select Tools > Developer Tools, click the
  Network tab, and look at the path in the Name column.

**NaCl module load failed: ELF executable text/rodata segment has wrong starting address**
  This error happens when using a newlib-style .nmf file instead of a
  glibc-style .nmf file. Make sure you build your application with the glic
  toolchain, and use the create_nmf.py script to generate your .nmf file.

**NativeClient: NaCl module load failed: Nexe crashed during startup**
  This error message indicates that a module crashed while being loaded. You
  can determine which module crashed by looking at the Network tab in Chrome's
  Developer Tools (see above). The module that crashed will be the last one
  that was loaded.

**/lib/main.nexe: error while loading shared libraries: /lib/main.nexe: only ET_DYN and ET_EXEC can be loaded**
  This error message indicates that there is an error with the .so files listed
  in the .nmf file -- either the files are the wrong type or kind, or an
  expected library is missing.

**undefined reference to 'dlopen' collect2: ld returned 1 exit status**
  This is a linker ordering problem that usually results from improper ordering
  of command line flags when linking. Reconfigure your command line string to
  list libraries after the -o flag.

.. |menu-icon| image:: /images/menu-icon.png
.. _objdump: http://en.wikipedia.org/wiki/Objdump
.. _GLIBC: http://www.gnu.org/software/libc/index.html
.. _POSIX: http://en.wikipedia.org/wiki/POSIX
.. _newlib: http://sourceware.org/newlib/
