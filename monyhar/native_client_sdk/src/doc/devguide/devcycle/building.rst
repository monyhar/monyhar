.. _devcycle-building:

.. include:: /migration/deprecation.inc

########
Building
########

.. contents:: Table Of Contents
  :local:
  :backlinks: none
  :depth: 2

Introduction
============

This document describes how to build Native Client modules. It is intended for
developers who have experience writing, compiling, and linking C and C++ code.
If you haven't read the Native Client :doc:`Technical Overview
<../../overview>` and :doc:`Tutorial <../tutorial/index>`, we recommend starting
with those.

.. _target_architectures:

Target architectures
--------------------

Portable Native Client (PNaCl) modules are written in C or C++ and compiled
into an executable file ending in a **.pexe** extension using the PNaCl
toolchain in the Native Client SDK. Chrome can load **pexe** files
embedded in web pages and execute them as part of a web application.

As explained in the Technical Overview, PNaCl modules are
operating-system-independent **and** processor-independent. The same **pexe**
will run on Windows, macOS, Linux, and Chrome OS and it will run on x86-32,
x86-64, ARM and MIPS processors.

Native Client also supports architecture-specific **nexe** files.
These **nexe** files are **also** operating-system-independent,
but they are **not** processor-independent. To support a wide variety of
devices you must compile separate versions of your Native Client module
for different processors on end-user machines. A
:ref:`manifest file <application_files>` will then specify which version
of the module to load based on the end-user's architecture. The SDK
includes a script for generating manifest files called ``create_nmf.py``.  This
script is located in the ``pepper_<version>/tools/`` directory, meaning under
your installed pepper bundle. For examples of how to compile modules for
multiple target architectures and how to generate manifest files, see the
Makefiles included with the SDK examples.

This section will mostly cover PNaCl, but also describes how to build
**nexe** applications.

C libraries
-----------

The PNaCl toolchain uses the newlib_ C library and can be used to build
portable **pexe** files (using ``pnacl-clang``) or **nexe** files (using, for
example, ``x86_64-nacl-clang``).  The Native Client SDK also has a
GCC-based toolchain for building **nexe** files which uses the glibc_ C library.
See :doc:`Dynamic Linking & Loading with glibc <dynamic-loading>` for
information about these libraries, including factors to help you decide which to
use.

.. _building_cpp_libraries:

C++ standard libraries
----------------------

The PNaCl SDK can use either LLVM's `libc++ <http://libcxx.llvm.org/>`_
(the current default) or GCC's `libstdc++
<http://gcc.gnu.org/libstdc++>`_ (deprecated). The
``-stdlib=[libc++|libstdc++]`` command line argument can be used to
choose which standard library to use.

The GCC-based toolchain only has support for GCC's `libstdc++
<http://gcc.gnu.org/libstdc++>`_.

C++11 library support is only complete in libc++ but other non-library language
features should work regardless of which standard library is used. The
``-std=gnu++11`` command line argument can be used to indicate which C++
language standard to use (``-std=c++11`` often doesn't work well because newlib
relies on some GNU extensions).

SDK toolchains
--------------

The Native Client SDK includes multiple toolchains. It has one PNaCl toolchain
and it has multiple GCC-based toolchains that are differentiated by target
architectures and C libraries. The single PNaCl toolchain is located
in a directory named ``pepper_<version>/toolchain/<OS_platform>_pnacl``,
and the GCC-based toolchains are located in directories named
``pepper_<version>/toolchain/<OS_platform>_<architecture>_<c_library>``.

The compilers, linkers, and other tools are located in the ``bin/``
subdirectory in each toolchain. For example, the tools in the Windows SDK
for PNaCl has a C++ compiler in ``toolchain/win_pnacl/bin/pnacl-clang++``.

SDK toolchains versus your hosted toolchain
-------------------------------------------

To build NaCl modules, you must use one of the Native Client toolchains
included in the SDK. The SDK toolchains use a variety of techniques to
ensure that your NaCl modules comply with the security constraints of
the Native Client sandbox.

During development, you have another choice: You can build modules using a
*standard* toolchain, such as the hosted toolchain on your development
machine. This can be Visual Studio's standard compiler, XCode, LLVM, or
GNU-based compilers on your development machine. These standard toolchains
will not produce executables that comply with the Native Client sandbox
security constraints. They are also not portable across operating systems
and not portable across different processors. However, using a standard
toolchain allows you to develop modules in your favorite IDE and use
your favorite debugging and profiling tools. The drawback is that modules
compiled in this manner can only run as Pepper (PPAPI) plugins in Chrome.
To publish and distribute Native Client modules as part of a web
application, you must eventually use a toolchain in the Native
Client SDK.

.. Note::
  :class: note

  In the future, additional tools will be available to compile Native Client
  modules written in other programming languages, such as C#. But this
  document covers only compiling C and C++ code, using the toolchains
  provided in the SDK.


The PNaCl toolchain
===================

The PNaCl toolchain contains modified versions of the tools in the
LLVM toolchain, as well as linkers and other tools from binutils.
To determine which version of LLVM or binutils the tools are based upon,
run the tool with the ``--version`` command line flag. These tools
are used to compile and link applications into **.pexe** files. The toolchain
also contains a tool to translate a **pexe** file into a
architecture-specific **.nexe** (e.g., for debugging purposes).

Some of the useful tools include:

``pnacl-abicheck``
  Checks that the **pexe** follows the PNaCl ABI rules.
``pnacl-ar``
  Creates archives (i.e., static libraries)
``pnacl-bcdis``
  Object dumper for PNaCl bitcode files.
``pnacl-clang``
  C compiler and compiler driver
``pnacl-clang++``
  C++ compiler and compiler driver
``pnacl-compress``
  Compresses a finalized **pexe** file for deployment.
``pnacl-dis``
  Disassembler for both **pexe** files and **nexe** files
``pnacl-finalize``
  Finalizes **pexe** files for deployment
``pnacl-ld``
  Bitcode linker
``pnacl-nm``
  Lists symbols in bitcode files, native code, and libraries
``pnacl-ranlib``
  Generates a symbol table for archives (i.e., static libraries)
``pnacl-translate``
  Translates a **pexe** to a native architecture, outside of the browser

For the full list of tools, see the
``pepper_<version>/toolchain/<platform>_pnacl/bin`` directory.

Using the PNaCl tools to compile, link, debug, and deploy
=========================================================

To build an application with the PNaCl SDK toolchain, you must compile
your code, link it, test and debug it, and then deploy it. This section goes
over some examples of how to use the tools.

Compile
-------

To compile a simple application consisting of ``file1.cc`` and ``file2.cc`` into
``hello_world.pexe`` use the ``pnacl-clang++`` tool

.. naclcode::
  :prettyprint: 0

  nacl_sdk/pepper_<version>/toolchain/win_pnacl/bin/pnacl-clang++ \
    file1.cc file2.cc -Inacl_sdk/pepper_<version>/include \
    -Lnacl_sdk/pepper_<version>/lib/pnacl/Release -o hello_world.pexe \
    -g -O2 -lppapi_cpp -lppapi

The typical application consists of many files. In that case,
each file can be compiled separately so that only files that are
affected by a change need to be recompiled. To compile an individual
file from your application, you must use either the ``pnacl-clang`` C
compiler, or the ``pnacl-clang++`` C++ compiler. The compiler produces
separate bitcode files. For example:

.. naclcode::
  :prettyprint: 0

  nacl_sdk/pepper_<version>/toolchain/win_pnacl/bin/pnacl-clang++ \
    hello_world.cc -Inacl_sdk/pepper_<version>/include -c \
    -o hello_world.o -g -O0

For a description of each command line flag, run ``pnacl-clang --help``.
For convenience, here is a description of some of the flags used in
the example.

.. _compile_flags:

``-c``
  indicates that ``pnacl-clang++`` should only compile an individual file,
  rather than continue the build process and link together the
  full application.

``-o <output_file>``
  indicates the **output** filename.

``-g``
  tells the compiler to include debug information in the result.
  This debug information can be used during development, and then **stripped**
  before actually deploying the application to keep the application's
  download size small.

``-On``
  sets the optimization level to n. Use ``-O0`` when debugging, and ``-O2`` or
  ``-O3`` for deployment.

  The main difference between ``-O2`` and ``-O3`` is whether the compiler
  performs optimizations that involve a space-speed tradeoff. It could be the
  case that ``-O3`` optimizations are not desirable due to increased **pexe**
  download size; you should make your own performance measurements to determine
  which level of optimization is right for you. When looking at code size, note
  that what you generally care about is not the size of the **pexe** produced by
  ``pnacl-clang``, but the size of the compressed **pexe** that you upload to
  the server or to the Chrome Web Store. Optimizations that increase the size of
  an uncompressed **pexe** may not increase the size of the compressed **pexe**
  very much. You should also verify how optimization level affects on-device
  translation time, this can be tested locally with ``pnacl-translate``.

``-I<directory>``
  adds a directory to the search path for **include** files. The SDK has
  Pepper (PPAPI) headers located at ``nacl_sdk/pepper_<version>/
  include``, so add that directory when compiling to be able to include the
  headers.

``-mllvm -inline-threshold=n``
  change how much inlining is performed by LLVM (the default is 225, a smaller
  value will result in less inlining being performed). The right number to
  choose is application-specific, you'll therefore want to experiment with the
  value that you pass in: you'll be trading off potential performance with
  **pexe** size and on-device translation speed.

Create a static library
-----------------------

The ``pnacl-ar`` and ``pnacl-ranlib`` tools allow you to create a
**static** library from a set of bitcode files, which can later be linked
into the full application.

.. naclcode::
  :prettyprint: 0

  nacl_sdk/pepper_<version>/toolchain/win_pnacl/bin/pnacl-ar cr \
    libfoo.a foo1.o foo2.o foo3.o

  nacl_sdk/pepper_<version>/toolchain/win_pnacl/bin/pnacl-ranlib libfoo.a


Link the application
--------------------

The ``pnacl-clang++`` tool is used to compile applications, but it can
also be used link together compiled bitcode and libraries into a
full application.

.. naclcode::
  :prettyprint: 0

  nacl_sdk/pepper_<version>/toolchain/win_pnacl/bin/pnacl-clang++ \
    -o hello_world.pexe hello_world.o -Lnacl_sdk/pepper_<version>/lib/pnacl/Debug \
    -lfoo -lppapi_cpp -lppapi

This links the hello world bitcode with the ``foo`` library in the example
as well as the *Debug* version of the Pepper libraries which are located
in ``nacl_sdk/pepper_<version>/lib/pnacl/Debug``. If you wish to link
against the *Release* version of the Pepper libraries, change the
``-Lnacl_sdk/pepper_<version>/lib/pnacl/Debug`` to
``-Lnacl_sdk/pepper_<version>/lib/pnacl/Release``.

In a release build you'll want to pass ``-O2`` to the compiler *as well as to
the linker* to enable link-time optimizations. This reduces the size and
increases the performance of the final **pexe**, and leads to faster downloads
and on-device translation.

.. naclcode::
  :prettyprint: 0

  nacl_sdk/pepper_<version>/toolchain/win_pnacl/bin/pnacl-clang++ \
    -o hello_world.pexe hello_world.o -Lnacl_sdk/pepper_<version>/lib/pnacl/Release \
    -lfoo -lppapi_cpp -lppapi -O2

By default the link step will turn all C++ exceptions into calls to ``abort()``
to reduce the size of the final **pexe** as well as making it translate and run
faster. If you want to use C++ exceptions you should use the
``--pnacl-exceptions=sjlj`` linker flag as explained in the :ref:`exception
handling <exception_handling>` section of the C++ language support reference.


Finalizing the **pexe** for deployment
--------------------------------------

Typically you would run the application to test it and debug it if needed before
deploying. See the :doc:`running <running>` documentation for how to run a PNaCl
application, and see the :doc:`debugging <debugging>` documentation for
debugging techniques and workflow. After testing a PNaCl application, you must
**finalize** it. The ``pnacl-finalize`` tool handles this.

.. naclcode::
  :prettyprint: 0

  nacl_sdk/pepper_<version>/toolchain/win_pnacl/bin/pnacl-finalize \
    hello_world.pexe -o hello_world.final.pexe

Prior to finalization, the application **pexe** is stored in a binary
format that is subject to change.  After finalization, the application
**pexe** is **rewritten** into a different binary format that is **stable**
and will be supported by future versions of PNaCl. The finalization step
also helps minimize the size of your application for distribution by
stripping out debug information and other metadata.

Once the application is finalized, be sure to adjust the manifest file to
refer to the final version of the application before deployment.
The ``create_nmf.py`` tool helps generate an ``.nmf`` file, but ``.nmf``
files can also be written by hand.


.. _pnacl_compress:

Compressing the **pexe** for deployment
---------------------------------------

Size compression is an optional step for deployment, and reduces the size of the
**pexe** file that must be transmitted over the wire, resulting in faster
download speed. The tool ``pnacl-compress`` applies compression strategies that
are already built into the **stable** binary format of a **pexe**
application. As such, compressed **pexe** files do not need any extra time to be
decompressed on the client's side. All costs are upfront when you call
``pnacl-compress``.

Currently, this tool will compress **pexe** files by about 25%. However,
it is somewhat slow (can take from seconds to minutes on large
appications). Hence, this step is optional.

.. naclcode::
  :prettyprint: 0

  nacl_sdk/pepper_<version>/toolchain/win_pnacl/bin/pnacl-compress \
    hello_world.final.pexe

``pnacl-compress`` must be called after a **pexe** file has been finalized for
deployment (via ``pnacl-finalize``). Alternatively, you can apply this step as
part of the finalizing step by adding the ``--compress`` flag to the
``pnacl-finalize`` command line.

This compression step doesn't replace the gzip compression performed web servers
configured for HTTP compression: both compressions are complementary. You'll
want to configure your web server to gzip **pexe** files: the gzipped version of
a compressed **pexe** file is smaller than the corresponding uncompressed
**pexe** file by 7.5% to 10%.

.. _pnacl-bcdis:

Object dumping of PNaCl bitcode files
=====================================

Sometimes you may be interesting in the contents of a PNaCl bitcode file.  The
tool ``pnacl-bcdis`` object dumps the contents of a PNaCl bitcode file.  For a
description of the output produced by this tool, see
:doc:`/reference/pnacl-bitcode-manual`.

.. naclcode::
 :prettyprint: 0

  nacl_sdk/pepper_<version>/toolchain/win_pnacl/bin/pnacl-bcdis \
    hello_world.final.pexe

The output is the corresponding contents of the given **pexe**.

The GNU-based toolchains
========================

Besides the PNaCl toolchain, the Native Client SDK also includes modified
versions of the tools in the standard GNU toolchain, including the GCC
compilers and the linkers and other tools from binutils. These tools only
support building **nexe** files. Run the tool with the ``--version``
command line flag to determine the current version of the tools.

Each tool in the toolchain is prefixed with the name of the target
architecture. In the toolchain for the ARM target architecture, each
tool's name is preceded by the prefix "arm-nacl-". In the toolchains for
the x86 target architecture, there are actually two versions of each
tool---one to build Native Client modules for the x86-32
target architecture, and one to build modules for the x86-64 target
architecture. "i686-nacl-" is the prefix for tools used to build
32-bit **.nexes**, and "x86_64-nacl-" is the prefix for tools used to
build 64-bit **.nexes**.

These prefixes conform to gcc naming standards and make it easy to use tools
like autoconf. As an example, you can use ``i686-nacl-gcc`` to compile 32-bit
**.nexes**, and ``x86_64-nacl-gcc`` to compile 64-bit **.nexes**. Note that you
can typically override a tool's default target architecture with command line
flags, e.g., you can specify ``x86_64-nacl-gcc -m32`` to compile a 32-bit
**.nexe**.

The GNU-based SDK toolchains include the following tools:

* <prefix>addr2line
* <prefix>ar
* <prefix>as
* <prefix>c++
* <prefix>c++filt
* <prefix>cpp
* <prefix>g++
* <prefix>gcc
* <prefix>gcc-4.4.3
* <prefix>gccbug
* <prefix>gcov
* <prefix>gprof
* <prefix>ld
* <prefix>nm
* <prefix>objcopy
* <prefix>objdump
* <prefix>ranlib
* <prefix>readelf
* <prefix>size
* <prefix>strings
* <prefix>strip


Compiling
---------

Compiling files with the GNU-based toolchain is similar to compiling
files with the PNaCl-based toolchain, except that the output is
architecture specific.

For example, assuming you're developing on a Windows machine, targeting the x86
architecture you can compile a 32-bit **.nexe** for the hello_world example with
the following command:

.. naclcode::
  :prettyprint: 0

  nacl_sdk/pepper_<version>/toolchain/win_x86_glibc/bin/i686-nacl-gcc \
    hello_world.c -Inacl_sdk/pepper_<version>/include \
    -Lnacl_sdk/pepper_<version>/lib/glibc/Release -o hello_world_x86_32.nexe \
    -m32 -g -O2 -lppapi

To compile a 64-bit **.nexe**, you can run the same command but use -m64 instead
of -m32. Alternatively, you could also use the version of the compiler that
targets the x86-64 architecture, i.e., ``x86_64-nacl-gcc``.

You should name executable modules with a **.nexe** filename extension,
regardless of what platform you're using.

Creating libraries and Linking
------------------------------

Creating libraries and linking with the GNU-based toolchain is similar
to doing the same with the PNaCl toolchain.  The relevant tools
for creating **static** libraries are ``<prefix>ar`` and ``<prefix>ranlib``.
Linking can be done with ``<prefix>g++``. See the
:doc:`Dynamic Linking & Loading with glibc <dynamic-loading>`
section on how to create **shared** libraries.


Finalizing a **nexe** for deployment
------------------------------------

Unlike the PNaCl toolchain, no separate finalization step is required
for **nexe** files. The **nexe** files are always in a **stable** format.
However, the **nexe** file may contain debug information and symbol information
which may make the **nexe** file larger than needed for distribution.
To minimize the size of the distributed file, you can run the
``<prefix>strip`` tool to strip out debug information.


Using make
==========

This document doesn't cover how to use ``make``, but if you want to use
``make`` to build your Native Client module, you can base your Makefile on the
ones in the SDK examples.

The Makefiles for the SDK examples build most of the examples in multiple
configurations (using PNaCl vs NaCl, using different C libraries,
targeting different architectures, and using different levels of optimization).
To select a specific toolchain, set the **environment variable**
``TOOLCHAIN`` to either ``pnacl``, ``clang-newlib``, ``glibc``, or ``host``.
To select a specific level of optimization set the **environment
variable** ``CONFIG`` to either ``Debug``, or ``Release``. Running
``make`` in each example's directory does **one** of the following,
depending on the setting of the environment variables.

* If ``TOOLCHAIN=pnacl`` creates a subdirectory called ``pnacl``;

  * builds a **.pexe** (architecture-independent Native Client executable) using
    the newlib library
  * generates a Native Client manifest (.nmf) file for the pnacl version of the
    example

* If ``TOOLCHAIN=clang-newlib`` creates a subdirectory called ``clang-newlib``;

  * builds **.nexes** for the x86-32, x86-64, and ARM architectures using the
    nacl-clang toolchain and the newlib C library
  * generates a Native Client manifest (.nmf) file for the clang-newlib version
    of the example

* If ``TOOLCHAIN=glibc`` creates a subdirectory called ``glibc``;

  * builds **.nexes** for the x86-32, x86-64 and ARM architectures using the
    glibc library
  * generates a Native Client manifest (.nmf) file for the glibc version of the
    example

* If ``TOOLCHAIN=host`` creates a subdirectory called ``windows``, ``linux``,
  or ``mac`` (depending on your development machine);

  * builds a Pepper plugin (.dll for Windows, .so for Linux/Mac) using the
    hosted toolchain on your development machine
  * generates a Native Client manifest (.nmf) file for the host Pepper plugin
    version of the example


.. Note::
  :class: note

  The glibc library is not yet available for the ARM and PNaCl toolchains.

Here is how to build the examples with PNaCl in Release mode on Windows.
The resulting files for ``examples/api/audio`` will be in
``examples/api/audio/pnacl/Release``, and the directory layout is similar for
other examples.

.. naclcode::
  :prettyprint: 0

  set TOOLCHAIN=pnacl
  set CONFIG=Release
  make

Your Makefile can be simpler since you will not likely want to build so many
different configurations of your module. The example Makefiles define
numerous variables near the top (e.g., ``CFLAGS``) that make it easy
to customize the commands that are executed for your project and the options
for each command.

For details on how to use make, see the `GNU 'make' Manual
<http://www.gnu.org/software/make/manual/make.html>`_.

Libraries and header files provided with the SDK
================================================

The Native Client SDK includes modified versions of standard toolchain-support
libraries, such as libpthread and libc, plus the relevant header files.
The standard libraries are located under the ``/pepper_<version>`` directory
in the following locations:

* PNaCl toolchain: ``toolchain/<platform>_pnacl/usr/lib``
* x86 toolchains: ``toolchain/<platform>_x86_<c_library>/x86_64-nacl/lib32`` and
  ``/lib64`` (for the 32-bit and 64-bit target architectures, respectively)
* ARM toolchain: ``toolchain/<platform>_arm_<c_library>/arm-nacl/lib``

For example, on Windows, the libraries for the x86-64 architecture in the
glibc toolchain are in ``toolchain/win_x86_glibc/x86_64-nacl/lib64``.

The header files are in:

* PNaCl toolchain: ``toolchain/<platform>_pnacl/le32-nacl/include``
* clang newlib toolchains: ``toolchain/<platform>_pnacl/<arch>-nacl/include``
* x86 glibc toolchain: ``toolchain/<platform>_x86_glibc/x86_64-nacl/include``
* ARM glibc toolchain: ``toolchain/<platform>_arm_glibc/arm-nacl/include``

Many other libraries have been ported for use with Native Client; for more
information, see the `webports <https://monyhar.googlesource.com/webports>`_
project. If you port an open-source library for your own use, we recommend
adding it to webports.

Besides the standard libraries, the SDK includes Pepper libraries.
The PNaCl Pepper libraries are located in the the
``nacl_sdk/pepper_<version>/lib/pnacl/<Release or Debug>`` directory.
The GNU-based toolchain has Pepper libraries in
``nacl_sdk/pepper_<version>/lib/glibc_<arch>/<Release or Debug>``
and ``nacl_sdk/pepper_<version>/lib/clang-newlib_<arch>/<Release or Debug>``.
The libraries provided by the SDK allow the application to use Pepper,
as well as convenience libraries to simplify porting an application that
uses POSIX functions. Here are descriptions of the Pepper libraries provided
in the SDK.

.. _devcycle-building-nacl-io:

libppapi.a
  Implements the Pepper (PPAPI) C interface. Needed for all applications that
  use Pepper (even C++ applications).

libppapi_cpp.a
  Implements the Pepper (PPAPI) C++ interface. Needed by C++ applications that
  use Pepper.

libppapi_gles2.a
  Implements the Pepper (PPAPI) GLES interface. Needed by applications
  that use the 3D graphics API.

libnacl_io.a
  Provides a POSIX layer for NaCl. In particular, the library provides a
  virtual file system and support for sockets. The virtual file system
  allows a module to "mount" a given directory tree. Once a module has
  mounted a file system, it can use standard C library file operations:
  ``fopen``, ``fread``, ``fwrite``, ``fseek``, and ``fclose``.
  For more detail, see the header ``include/nacl_io/nacl_io.h``.
  For an example of how to use nacl_io, see ``examples/demo/nacl_io_demo``.

libppapi_simple.a
  Provides a familiar C programming environment by letting a module have a
  simple ``main()`` entry point.  The entry point is similar to the standard C
  ``main()`` function, complete with ``argc`` and ``argv[]`` parameters. For
  details see ``include/ppapi_simple/ps.h``. For an example of
  how to use ppapi_simple, ``see examples/tutorial/using_ppapi_simple``.


.. Note::
  :class: note

  * Since the Native Client toolchains use their own library and header search
    paths, the tools won't find third-party libraries you use in your
    non-Native-Client development. If you want to use a specific third-party
    library for Native Client development, look for it in `webports
    <https://monyhar.googlesource.com/webports>`_, or port the library yourself.
  * The order in which you list libraries in your build commands is important,
    since the linker searches and processes libraries in the order in which they
    are specified. See the ``\*_LDFLAGS`` variables in the Makefiles of the SDK
    examples for the order in which specific libraries should be listed.

Troubleshooting
===============

Some common problems, and how to fix them:

"Undefined reference" error
---------------------------

An "undefined reference" error may indicate incorrect link order and/or
missing libraries. For example, if you leave out ``-lppapi`` when
compiling Pepper applications you'll see a series of undefined
reference errors.

One common type of "undefined reference" error is with respect to certain
system calls, e.g., "undefined reference to 'mkdir'". For security reasons,
Native Client does not support a number of system calls. Depending on how
your code uses such system calls, you have a few options:

#. Link with the ``-lnosys`` flag to provide empty/always-fail versions of
   unsupported system calls. This will at least get you past the link stage.
#. Find and remove use of the unsupported system calls.
#. Create your own implementation of the unsupported system calls to do
   something useful for your application.

If your code uses mkdir or other file system calls, you might find the
:ref:`nacl_io <devcycle-building-nacl-io>` library useful.
The nacl_io library essentially does option (3) for you: It lets your
code use POSIX-like file system calls, and implements the calls using
various technologies (e.g., HTML5 file system, read-only filesystems that
use URL loaders, or an in-memory filesystem).

Can't find libraries containing necessary symbols
-------------------------------------------------

Here is one way to find the appropriate library for a given symbol:

.. naclcode::
  :prettyprint: 0

  nacl_sdk/pepper_<version>/toolchain/<platform>_pnacl/bin/pnacl-nm -o \
    nacl_sdk/pepper_<version>toolchain/<platform>_pnacl/usr/lib/*.a | \
    grep <MySymbolName>


PNaCl ABI Verification errors
-----------------------------

PNaCl has restrictions on what is supported in bitcode. There is a bitcode
ABI verifier which checks that the application conforms to the ABI restrictions,
before it is translated and run in the browser. However, it is best to
avoid runtime errors for users, so the verifier also runs on the developer's
machine at link time.

For example, the following program which uses 128-bit integers
would compile with NaCl GCC for the x86-64 target. However, it is not
portable and would not compile with NaCl GCC for the i686 target.
With PNaCl, it would fail to pass the ABI verifier:

.. naclcode::

  typedef unsigned int uint128_t __attribute__((mode(TI)));

  uint128_t foo(uint128_t x) {
    return x;
  }

With PNaCl you would get the following error at link time:

.. naclcode::

  Function foo has disallowed type: i128 (i128)
  LLVM ERROR: PNaCl ABI verification failed

When faced with a PNaCl ABI verification error, check the list of features
that are :ref:`not supported by PNaCl <when-to-use-nacl>`.
If the problem you face is not listed as restricted,
:ref:`let us know <help>`!

.. _glibc: http://www.gnu.org/software/libc/
.. _newlib: http://sourceware.org/newlib/
