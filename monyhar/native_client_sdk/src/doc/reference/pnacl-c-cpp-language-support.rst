.. include:: /migration/deprecation.inc

============================
PNaCl C/C++ Language Support
============================

.. contents::
   :local:
   :backlinks: none
   :depth: 3

Source language support
=======================

The currently supported languages are C and C++. The PNaCl toolchain is
based on recent Clang, which fully supports C++11 and most of C11. A
detailed status of the language support is available `here
<http://clang.llvm.org/cxx_status.html>`_.

For information on using languages other than C/C++, see the :ref:`FAQ
section on other languages <other_languages>`.

As for the standard libraries, the PNaCl toolchain is currently based on
``libc++``, and the ``newlib`` standard C library. ``libstdc++`` is also
supported but its use is discouraged; see :ref:`building_cpp_libraries`
for more details.

Versions
--------

Version information can be obtained:

* Clang/LLVM: run ``pnacl-clang -v``.
* ``newlib``: use the ``_NEWLIB_VERSION`` macro.
* ``libc++``: use the ``_LIBCPP_VERSION`` macro.
* ``libstdc++``: use the ``_GLIBCXX_VERSION`` macro.

Preprocessor definitions
------------------------

When compiling C/C++ code, the PNaCl toolchain defines the ``__pnacl__``
macro. In addition, ``__native_client__`` is defined for compatibility
with other NaCl toolchains.

.. _memory_model_and_atomics:

Memory Model and Atomics
========================

Memory Model for Concurrent Operations
--------------------------------------

The memory model offered by PNaCl relies on the same coding guidelines
as the C11/C++11 one: concurrent accesses must always occur through
atomic primitives (offered by :ref:`atomic intrinsics 
<bitcode_atomicintrinsics>`), and these accesses must always
occur with the same size for the same memory location. Visibility of
stores is provided on a happens-before basis that relates memory
locations to each other as the C11/C++11 standards do.

Non-atomic memory accesses may be reordered, separated, elided or fused
according to C and C++'s memory model before the pexe is created as well
as after its creation. Accessing atomic memory location through
non-atomic primitives is :ref:`Undefined Behavior <undefined_behavior>`.

As in C11/C++11 some atomic accesses may be implemented with locks on
certain platforms. The ``ATOMIC_*_LOCK_FREE`` macros will always be
``1``, signifying that all types are sometimes lock-free. The
``is_lock_free`` methods and ``atomic_is_lock_free`` will return the
current platform's implementation at translation time. These macros,
methods and functions are in the C11 header ``<stdatomic.h>`` and the
C++11 header ``<atomic>``.

The PNaCl toolchain supports concurrent memory accesses through legacy
GCC-style ``__sync_*`` builtins, as well as through C11/C++11 atomic
primitives and the underlying `GCCMM
<http://gcc.gnu.org/wiki/Atomic/GCCMM>`_ ``__atomic_*``
primitives. ``volatile`` memory accesses can also be used, though these
are discouraged. See `Volatile Memory Accesses`_.

PNaCl supports concurrency and parallelism with some restrictions:

* Threading is explicitly supported and has no restrictions over what
  prevalent implementations offer. See `Threading`_.

* ``volatile`` and atomic operations are address-free (operations on the
  same memory location via two different addresses work atomically), as
  intended by the C11/C++11 standards. This is critical in supporting
  synchronous "external modifications" such as mapping underlying memory
  at multiple locations.

* Inter-process communication through shared memory is currently not
  supported. See `Future Directions`_.

* Signal handling isn't supported, PNaCl therefore promotes all
  primitives to cross-thread (instead of single-thread). This may change
  at a later date. Note that using atomic operations which aren't
  lock-free may lead to deadlocks when handling asynchronous
  signals. See `Future Directions`_.

* Direct interaction with device memory isn't supported, and there is no
  intent to support it. The embedding sandbox's runtime can offer APIs
  to indirectly access devices.

Setting up the above mechanisms requires assistance from the embedding
sandbox's runtime (e.g. NaCl's Pepper APIs), but using them once setup
can be done through regular C/C++ code.

Atomic Memory Ordering Constraints
----------------------------------

Atomics follow the same ordering constraints as in regular C11/C++11,
but all accesses are promoted to sequential consistency (the strongest
memory ordering) at pexe creation time. We plan to support more of the
C11/C++11 memory orderings in the future.

Some additional restrictions, following the C11/C++11 standards:

- Atomic accesses must at least be naturally aligned.
- Some accesses may not actually be atomic on certain platforms,
  requiring an implementation that uses global locks.
- An atomic memory location must always be accessed with atomic
  primitives, and these primitives must always be of the same bit size
  for that location.
- Not all memory orderings are valid for all atomic operations.

Volatile Memory Accesses
------------------------

The C11/C++11 standards mandate that ``volatile`` accesses execute in
program order (but are not fences, so other memory operations can
reorder around them), are not necessarily atomic, and can’t be
elided. They can be separated into smaller width accesses.

Before any optimizations occur, the PNaCl toolchain transforms
``volatile`` loads and stores into sequentially consistent ``volatile``
atomic loads and stores, and applies regular compiler optimizations
along the above guidelines. This orders ``volatiles`` according to the
atomic rules, and means that fences (including ``__sync_synchronize``)
act in a better-defined manner. Regular memory accesses still do not
have ordering guarantees with ``volatile`` and atomic accesses, though
the internal representation of ``__sync_synchronize`` attempts to
prevent reordering of memory accesses to objects which may escape.

Relaxed ordering could be used instead, but for the first release it is
more conservative to apply sequential consistency. Future releases may
change what happens at compile-time, but already-released pexes will
continue using sequential consistency.

The PNaCl toolchain also requires that ``volatile`` accesses be at least
naturally aligned, and tries to guarantee this alignment.

The above guarantees ease the support of legacy (i.e. non-C11/C++11)
code, and combined with builtin fences these programs can do meaningful
cross-thread communication without changing code. They also better
reflect the original code's intent and guarantee better portability.

.. _language_support_threading:

Threading
=========

Threading is explicitly supported through C11/C++11's threading
libraries as well as POSIX threads.

Communication between threads should use atomic primitives as described
in `Memory Model and Atomics`_.

``setjmp`` and ``longjmp``
==========================

PNaCl and NaCl support ``setjmp`` and ``longjmp`` without any
restrictions beyond C's.

.. _exception_handling:

C++ Exception Handling
======================

PNaCl currently supports C++ exception handling through ``setjmp()`` and
``longjmp()``, which can be enabled with the ``--pnacl-exceptions=sjlj`` linker
flag (set with ``LDFLAGS`` when using Make). Exceptions are disabled by default
so that faster and smaller code is generated, and ``throw`` statements are
replaced with calls to ``abort()``. The usual ``-fno-exceptions`` flag is also
supported, though the default is ``-fexceptions``. PNaCl will support full
zero-cost exception handling in the future.

.. note:: When using webports_ or other prebuilt static libraries, you don't
          need to recompile because the exception handling support is
          implemented at link time (when all the static libraries are put
          together with your application).

.. _webports: https://monyhar.googlesource.com/webports

NaCl supports full zero-cost C++ exception handling.

Inline Assembly
===============

Inline assembly isn't supported by PNaCl because it isn't portable. The
one current exception is the common compiler barrier idiom
``asm("":::"memory")``, which gets transformed to a sequentially
consistent memory barrier (equivalent to ``__sync_synchronize()``). In
PNaCl this barrier is only guaranteed to order ``volatile`` and atomic
memory accesses, though in practice the implementation attempts to also
prevent reordering of memory accesses to objects which may escape.

PNaCl supports :ref:`Portable SIMD Vectors <portable_simd_vectors>`,
which are traditionally expressed through target-specific intrinsics or
inline assembly.

NaCl supports a fairly wide subset of inline assembly through GCC's
inline assembly syntax, with the restriction that the sandboxing model
for the target architecture has to be respected.

.. _portable_simd_vectors:

Portable SIMD Vectors
=====================

SIMD vectors aren't part of the C/C++ standards and are traditionally
very hardware-specific. Portable Native Client offers a portable version
of SIMD vector datatypes and operations which map well to modern
architectures and offer performance which matches or approaches
hardware-specific uses.

SIMD vector support was added to Portable Native Client for version 37 of Chrome
and more features, including performance enhancements, have been added in
subsequent releases, see the :ref:`Release Notes <sdk-release-notes>` for more
details.

Hand-Coding Vector Extensions
-----------------------------

The initial vector support in Portable Native Client adds `LLVM vectors
<http://clang.llvm.org/docs/LanguageExtensions.html#vectors-and-extended-vectors>`_
and `GCC vectors
<http://gcc.gnu.org/onlinedocs/gcc/Vector-Extensions.html>`_ since these
are well supported by different hardware platforms and don't require any
new compiler intrinsics.

Vector types can be used through the ``vector_size`` attribute:

.. naclcode::

  #define VECTOR_BYTES 16
  typedef int v4s __attribute__((vector_size(VECTOR_BYTES)));
  v4s a = {1,2,3,4};
  v4s b = {5,6,7,8};
  v4s c, d, e;
  c = a + b;  /* c = {6,8,10,12} */
  d = b >> a; /* d = {2,1,0,0} */

Vector comparisons are represented as a bitmask as wide as the compared
elements of all ``0`` or all ``1``:

.. naclcode::

  typedef int v4s __attribute__((vector_size(16)));
  v4s snip(v4s in) {
    v4s limit = {32,64,128,256};
    v4s mask = in > limit;
    v4s ret = in & mask;
    return ret;
  }

Vector datatypes are currently expected to be 128-bit wide with one of the
following element types, and they're expected to be aligned to the underlying
element's bit width (loads and store will otherwise be broken up into scalar
accesses to prevent faults):

============  ============  ================ ======================
Type          Num Elements  Vector Bit Width Expected Bit Alignment
============  ============  ================ ======================
``uint8_t``   16            128              8
``int8_t``    16            128              8
``uint16_t``  8             128              16
``int16_t``   8             128              16
``uint32_t``  4             128              32
``int32_t``   4             128              32
``float``     4             128              32
============  ============  ================ ======================

64-bit integers and double-precision floating point will be supported in
a future release, as will 256-bit and 512-bit vectors.

Vector element bit width alignment can be stated explicitly (this is assumed by
PNaCl, but not necessarily by other compilers), and smaller alignments can also
be specified:

.. naclcode::

  typedef int v4s_element   __attribute__((vector_size(16), aligned(4)));
  typedef int v4s_unaligned __attribute__((vector_size(16), aligned(1)));


The following operators are supported on vectors:

+----------------------------------------------+
| unary ``+``, ``-``                           |
+----------------------------------------------+
| ``++``, ``--``                               |
+----------------------------------------------+
| ``+``, ``-``, ``*``, ``/``, ``%``            |
+----------------------------------------------+
| ``&``, ``|``, ``^``, ``~``                   |
+----------------------------------------------+
| ``>>``, ``<<``                               |
+----------------------------------------------+
| ``!``, ``&&``, ``||``                        |
+----------------------------------------------+
| ``==``, ``!=``, ``>``, ``<``, ``>=``, ``<=`` |
+----------------------------------------------+
| ``=``                                        |
+----------------------------------------------+

C-style casts can be used to convert one vector type to another without
modifying the underlying bits. ``__builtin_convertvector`` can be used
to convert from one type to another provided both types have the same
number of elements, truncating when converting from floating-point to
integer.

.. naclcode::

  typedef unsigned v4u __attribute__((vector_size(16)));
  typedef float v4f __attribute__((vector_size(16)));
  v4u a = {0x3f19999a,0x40000000,0x40490fdb,0x66ff0c30};
  v4f b = (v4f) a; /* b = {0.6,2,3.14159,6.02214e+23}  */
  v4u c = __builtin_convertvector(b, v4u); /* c = {0,2,3,0} */

It is also possible to use array-style indexing into vectors to extract
individual elements using ``[]``.

.. naclcode::

  typedef unsigned v4u __attribute__((vector_size(16)));
  template<typename T>
  void print(const T v) {
    for (size_t i = 0; i != sizeof(v) / sizeof(v[0]); ++i)
      std::cout << v[i] << ' ';
    std::cout << std::endl;
  }

Vector shuffles (often called permutation or swizzle) operations are
supported through ``__builtin_shufflevector``. The builtin has two
vector arguments of the same element type, followed by a list of
constant integers that specify the element indices of the first two
vectors that should be extracted and returned in a new vector. These
element indices are numbered sequentially starting with the first
vector, continuing into the second vector. Thus, if ``vec1`` is a
4-element vector, index ``5`` would refer to the second element of
``vec2``. An index of ``-1`` can be used to indicate that the
corresponding element in the returned vector is a don’t care and can be
optimized by the backend.

The result of ``__builtin_shufflevector`` is a vector with the same
element type as ``vec1`` / ``vec2`` but that has an element count equal
to the number of indices specified.

.. naclcode::

  // identity operation - return 4-element vector v1.
  __builtin_shufflevector(v1, v1, 0, 1, 2, 3)

  // "Splat" element 0 of v1 into a 4-element result.
  __builtin_shufflevector(v1, v1, 0, 0, 0, 0)

  // Reverse 4-element vector v1.
  __builtin_shufflevector(v1, v1, 3, 2, 1, 0)

  // Concatenate every other element of 4-element vectors v1 and v2.
  __builtin_shufflevector(v1, v2, 0, 2, 4, 6)

  // Concatenate every other element of 8-element vectors v1 and v2.
  __builtin_shufflevector(v1, v2, 0, 2, 4, 6, 8, 10, 12, 14)

  // Shuffle v1 with some elements being undefined
  __builtin_shufflevector(v1, v1, 3, -1, 1, -1)

One common use of ``__builtin_shufflevector`` is to perform
vector-scalar operations:

.. naclcode::

  typedef int v4s __attribute__((vector_size(16)));
  v4s shift_right_by(v4s shift_me, int shift_amount) {
    v4s tmp = {shift_amount};
    return shift_me >> __builtin_shuffle_vector(tmp, tmp, 0, 0, 0, 0);
  }

Auto-Vectorization
------------------

Auto-vectorization is currently not enabled for Portable Native Client,
but will be in a future release.

Undefined Behavior
==================

The C and C++ languages expose some undefined behavior which is
discussed in :ref:`PNaCl Undefined Behavior <undefined_behavior>`.

.. _c_cpp_floating_point:

Floating-Point
==============

PNaCl exposes 32-bit and 64-bit floating point operations which are
mostly IEEE-754 compliant. There are a few caveats:

* Some :ref:`floating-point behavior is currently left as undefined
  <undefined_behavior_fp>`.
* The default rounding mode is round-to-nearest and other rounding modes
  are currently not usable, which isn't IEEE-754 compliant. PNaCl could
  support switching modes (the 4 modes exposed by C99 ``FLT_ROUNDS``
  macros).
* Signaling ``NaN`` never fault.
* Fast-math optimizations are currently supported before *pexe* creation
  time. A *pexe* loses all fast-math information when it is
  created. Fast-math translation could be enabled at a later date,
  potentially at a perf-function granularity. This wouldn't affect
  already-existing *pexe*; it would be an opt-in feature.

  * Fused-multiply-add have higher precision and often execute faster;
    PNaCl currently disallows them in the *pexe* because they aren't
    supported on all platforms and can't realistically be
    emulated. PNaCl could (but currently doesn't) only generate them in
    the backend if fast-math were specified and the hardware supports
    the operation.
  * Transcendentals aren't exposed by PNaCl's ABI; they are part of the
    math library that is included in the *pexe*. PNaCl could, but
    currently doesn't, use hardware support if fast-math were provided
    in the *pexe*.

Computed ``goto``
=================

PNaCl supports computed ``goto``, a non-standard GCC extension to C used
by some interpreters, by lowering them to ``switch`` statements. The
resulting use of ``switch`` might not be as fast as the original
indirect branches. If you are compiling a program that has a
compile-time option for using computed ``goto``, it's possible that the
program will run faster with the option turned off (e.g., if the program
does extra work to take advantage of computed ``goto``).

NaCl supports computed ``goto`` without any transformation.

Future Directions
=================

Inter-Process Communication
---------------------------

Inter-process communication through shared memory is currently not
supported by PNaCl/NaCl. When implemented, it may be limited to
operations which are lock-free on the current platform (``is_lock_free``
methods). It will rely on the address-free properly discussed in `Memory
Model for Concurrent Operations`_.

POSIX-style Signal Handling
---------------------------

POSIX-style signal handling really consists of two different features:

* **Hardware exception handling** (synchronous signals): The ability
  to catch hardware exceptions (such as memory access faults and
  division by zero) using a signal handler.

  PNaCl currently doesn't support hardware exception handling.

  NaCl supports hardware exception handling via the
  ``<nacl/nacl_exception.h>`` interface.

* **Asynchronous interruption of threads** (asynchronous signals): The
  ability to asynchronously interrupt the execution of a thread,
  forcing the thread to run a signal handler.

  A similar feature is **thread suspension**: The ability to
  asynchronously suspend and resume a thread and inspect or modify its
  execution state (such as register state).

  Neither PNaCl nor NaCl currently support asynchronous interruption
  or suspension of threads.

If PNaCl were to support either of these, the interaction of
``volatile`` and atomics with same-thread signal handling would need
to be carefully detailed.
