.. _arm-32-bit-sandbox:

.. include:: /migration/deprecation.inc

==================
ARM 32-bit Sandbox
==================

Native Client for ARM is a sandboxing technology for running
programs---even malicious ones---safely, on computers that use 32-bit
ARM processors. The ARM sandbox is an extension of earlier work on
Native Client for x86 processors. Security is provided with a low
performance overhead of about 10% over regular ARM code, and as you'll
see in this document the sandbox model is beautifully simple, meaning
that the trusted codebase is much easier to validate.

As an implementation detail, the Native Client 32-bit ARM sandbox is
currently used by Portable Native Client to execute code on 32-bit ARM
machines in a safe manner. The portable bitcode contained in a **pexe**
is translated to a 32-bit ARM **nexe** before execution. This may change
at a point in time: Portable Native Client doesn't necessarily need this
sandbox to execute code on ARM. Note that the Portable Native Client
compiler itself is also untrusted: it too runs in the ARM sandbox
described in this document.

On this page, we describe how Native Client works on 32-bit ARM. We
assume no prior knowledge about the internals of Native Client, on x86
or any other architecture, but we do assume some familiarity with
assembly languages in general.

.. contents::
   :local:
   :backlinks: none
   :depth: 3

An Introduction to the ARM Architecture
=======================================

In this section, we summarize the relevant parts of the ARM processor
architecture.

About ARM and ARMv7-A
---------------------

ARM is one of the older commercial "RISC" processor designs, dating back
to the early 1980s. Today, it is used primarily in embedded systems:
everything from toys, to home automation, to automobiles. However, its
most visible use is in cellular phones, tablets and some
laptops.

Through the years, there have been many revisions of the ARM
architecture, written as ARMv\ *X* for some version *X*. Native Client
specifically targets the ARMv7-A architecture commonly used in high-end
phones and smartbooks. This revision, defined in the mid-2000s, adds a
number of useful instructions, and specifies some portions of the system
that used to be left to individual chip manufacturers. Critically,
ARMv7-A specifies the "eXecute Never" bit, or *XN*. This pagetable
attribute lets us mark memory as non-executable. Our security relies on
the presence of this feature.

ARMv8 adds a new 64-bit instruction set architecture called A64, while
also enhancing the 32-bit A32 ISA. For Native Client's purposes the A32
ISA is equivalent to the ARMv7 ARM ISA, albeit with a few new
instructions. This document only discussed the 32-bit A32 instruction
set: A64 would require a different sandboxing model.

ARM Programmer's Model
----------------------

While modern ARM chips support several instruction encodings, 32-bit
Native Client on ARM focuses on a single one: a fixed-width encoding
where every instruction is 32-bits wide called A32 (previously, and
confusingly, called simply ARM). Thumb, Thumb2 (now confusingly called
T32), Jazelle, ThumbEE and such aren't supported by Native Client. This
dramatically simplifies some of our analyses, as we'll see later. Nearly
every instruction can be conditionally executed based on the contents of
a dedicated condition code register.

ARM processors have 16 general-purpose registers used for integer and
memory operations, written ``r0`` through ``r15``. Of these, two have
special roles baked in to the hardware:

* ``r14`` is the Link Register. The ARM *call* instruction
  (*branch-with-link*) doesn't use the stack directly. Instead, it
  stashes the return address in ``r14``. In other circumstances, ``r14``
  can be (and is!) used as a general-purpose register. When ``r14`` is
  playing its Link Register role, it's referred to as ``lr``.
* ``r15`` is the Program Counter. While it can be read and written like
  any other register, setting it to a new value will cause execution to
  jump to a new address. Using it in some circumstances is also
  undefined by the ARM architecture. Because of this, ``r15`` is never
  used for anything else, and is referred to as ``pc``.

Other registers are given roles by convention. The only important
registers to Native Client are ``r9`` and ``r13``, which are used as the
Thread Pointer location and Stack Pointer. When playing this role,
they're referred to as ``tp`` and ``sp``.

Like other RISC-inspired designs, ARM programs use explicit *load* and
*store* instructions to access memory. All other instructions operate
only on registers, or on registers and small constants called
immediates. Because both instructions and data words are 32-bits, we
can't simply embed a 32-bit number into an instruction. ARM programs use
three methods to work around this, all of which Native Client exploits:

1. Many instructions can encode a modified immediate, which is an 8-bit
   number rotated right by an even number of bits.
2. The ``movw`` and ``movt`` instructions can be used to set the top and
   bottom 16-bits of a register, and can therefore encode any 32-bit
   immediate.
3. For values that can't be represented as modified immediates, ARM
   programs use ``pc``-relative loads to load data from inside the
   code---hidden in a place where it won't be executed such as "constant
   pools", just past the final return of a function.

We'll introduce more details of the ARM instruction set later, as we
walk through the system.

The Native Client Approach
==========================

Native Client runs an untrusted program, potentially from an unknown or
malicious source, inside a sandbox created by a trusted runtime. The
trusted runtime allows the untrusted program to "call-out" and perform
certain actions, such as drawing graphics, but prevents it from
accessing the operating system directly. This "call-out" facility,
called a trampoline, looks like a standard function call to the
untrusted program, but it allows control to escape from the sandbox in a
controlled way.

The untrusted program and trusted runtime inhabit the same process, or
virtual address space, maintained by the operating system. To keep the
trusted runtime behaving the way we expect, we must prevent the
untrusted program from accessing and modifying its internals. Since they
share a virtual address space, we can't rely on the operating system for
this. Instead, we isolate the untrusted program from the trusted
runtime.

Unlike modern operating systems, we use a cooperative isolation
method. Native Client can't run any off-the-shelf program compiled for
an off-the-shelf operating system. The program must be compiled to
comply with Native Client's rules. The details vary on each platform,
but in general, the untrusted program:

* Must not attempt to use certain forbidden instructions, such as system
  calls.
* Must not attempt to modify its own code without abiding by Native
  Client's code modification rules.
* Must not jump into the middle of an instruction group, or otherwise do
  tricky things to cause instructions to be interpreted multiple ways.
* Must use special, strictly-defined instruction sequences to perform
  permitted but potentially dangerous actions. We call these sequences
  pseudo-instructions.

We can't simply take the program's word that it complies with these
rules---we call it "untrusted" for a reason! Nor do we require it to be
produced by a special compiler; in practice, we don't trust our
compilers either. Instead, we apply a load-time validator that
disassembles the program. The validator either proves that the program
complies with our rules, or rejects it as unsafe. By keeping the rules
simple, we keep the validator simple, small, and fast. We like to put
our trust in small, simple things, and the validator is key to the
system's security.

.. Note::
  :class: note

  For the computationally-inclined, all our validators scale linearly in
  the size of the program.

NaCl/ARM: Pure Software Fault Isolation
---------------------------------------

In the original Native Client system for the x86, we used unusual
hardware features of that processor (the segment registers) to isolate
untrusted programs. This was simple and fast, but won't work on ARM,
which has nothing equivalent. Instead, we use pure software fault
isolation.

We use a fixed address space layout: the untrusted program gets the
lowest gigabyte, addresses ``0`` through ``0x3FFFFFFF``. The rest of the
address space holds the trusted runtime and the operating system. We
isolate the program by requiring every *load*, *store*, and *indirect
branch* (to an address in a register) to use a pseudo-instruction. The
pseudo-instructions ensure that the address stays within the
sandbox. The *indirect branch* pseudo-instruction, in turn, ensures that
such branches won't split up other pseudo-instructions.

At either side of the sandbox, we place small (8KiB) guard
regions. These are simply areas in the process's address space that are
mapped without read, write, or execute permissions, so any attempt to
access them for any reason---*load*, *store*, or *jump*---will cause a
fault.

Finally, we ban the use of certain instructions, notably direct system
calls. This is to ensure that the untrusted program can be run on any
operating system supported by Native Client, and to prevent access to
certain system features that might be used to subvert the sandbox. As a
side effect, it helps to prevent programs from exploiting buggy
operating system APIs.

Let's walk through the details, starting with the simplest part: *load*
and *store*.

*Load* and *Store*
^^^^^^^^^^^^^^^^^^

All access to memory must be through *load* and *store*
pseudo-instructions. These are simply a native *load* or *store*
instruction, preceded by a guard instruction.

Each *load* or *store* pseudo-instruction is similar to the *load* shown
below. We use abstract "placeholder" registers instead of specific
numbered registers for the sake of discussion. ``rA`` is the register
holding the address to load from. ``rD`` is the destination for the
loaded data.

.. naclcode::
  :prettyprint: 0

  bic    rA,  #0xC0000000
  ldr    rD,  [rA]

The first instruction, ``bic``, clears the top two bits of ``rA``. In
this case, that means that the value in ``rA`` is forced to an address
inside our sandbox, between ``0`` and ``0x3FFFFFFF``, inclusive.

The second instruction, ``ldr``, uses the previously-sandboxed address
to load a value. This address might not be the address that the program
intended, and might cause an access to an unmapped memory location
within the sandbox: ``bic`` forces the address to be valid, by clearing
the top two bits. This is a no-op in a correct program.

This illustrates a common property of all Native Client systems: we aim
for safety, not correctness. A program using an invalid address in
``rA`` here is simply broken, so we are free to do whatever we want to
preserve safety. In this case the program might load an invalid (but
safe) value, or cause a segmentation fault limited to the untrusted
code.

Now, if we allowed arbitrary branches within the program, a malicious
program could set up carefully-crafted values in ``rA``, and then jump
straight to the ``ldr``. This is why we validate that programs never
split pseudo-instructions.

Alternative Sandboxing
""""""""""""""""""""""

.. naclcode::
  :prettyprint: 0

  tst    rA,  #0xC0000000
  ldreq  rD,  [rA]

The first instruction, ``tst``, performs a bitwise-\ ``AND`` of ``rA``
and the modified immediate literal, ``0xC0000000``. It sets the
condition flags based on the result, but does not write the result to a
register. In particular, it sets the ``Z`` condition flag if the result
was zero---if the two values had no set bits in common. In this case,
that means that the value in ``rA`` was an address inside our sandbox,
between ``0`` and ``0x3FFFFFFF``, inclusive.

The second instruction, ``ldreq``, is a conditional load if equal. As we
mentioned before, nearly all ARM instructions can be made
conditional. In assembly language, we simply stick the desired condition
on the end of the instruction's mnemonic name. Here, the condition is
``EQ``, which causes the instruction to execute only if the ``Z`` flag
is set.

Thus, when the pseudo-instruction executes, the ``tst`` sets ``Z`` if
(and only if) the value in ``rA`` is an address within the bounds of the
sandbox, and then the ``ldreq`` loads if (and only if) it was. If ``rA``
held an invalid address, the *load* does not execute, and ``rD`` is
unchanged.

.. Note::
  :class: note

  The ``tst``-based sequence is faster than the ``bic``-based sequence
  on modern ARM chips. It avoids a data dependency in the address
  register. This is why we keep both around. The ``tst``-based sequence
  unfortunately leaks information on some processors, and is therefore
  forbidden on certain processors. This effectively means that it cannot
  be used for regular Native Client **nexe** files, but can be used with
  Portable Native Client because the target processor is known at
  translation time from **pexe** to **nexe**.

Addressing Modes
""""""""""""""""

ARM has an unusually rich set of addressing modes. We allow all but one:
register-indexed, where two registers are added to determine the
address.

We permit simple *load* and *store*, as shown above. We also permit
displacement, pre-index, and post-index memory operations:

.. naclcode::
  :prettyprint: 0

  bic    rA,  #0xC0000000
  ldr    rD,  [rA, #1234]    ; This is fine.
  bic    rA,  #0xC0000000
  ldr    rD,  [rA, #1234]!   ; Also fine.
  bic    rA,  #0xC0000000
  ldr    rD,  [rA], #1234    ; Looking good.

In each case, we know ``rA`` points into the sandbox when the ``ldr``
executes. We allow adding an immediate displacement to ``rA`` to
determine the final address (as in the first two examples here) because
the largest immediate displacement is ±4095 bytes, while our guard pages
are 8192 bytes wide.

We also allow ARM's more unusual *load* and *store* instructions, such
as *load-multiple* and *store-multiple*, etc.

Conditional *Load* and *Store*
""""""""""""""""""""""""""""""

There's one problem with the pseudo-instructions shown above: they are
unconditional (assuming ``rA`` is valid). ARM compilers regularly use
conditional *load* and *store*, so we should support this in Native
Client. We do so by defining alternate, predictable
pseudo-instructions. Here is a conditional *store*
(*store-if-greater-than*) using this pseudo-instruction sequence:

.. naclcode::
  :prettyprint: 0

  bicgt  rA,  #0xC0000000 
  strgt  rX,  [rA, #123]

The Stack Pointer, Thread Pointer, and Program Counter
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Stack Pointer
"""""""""""""

In C-like languages, the stack is used to store return addresses during
function calls, as well as any local variables that won't fit in
registers. This makes stack operations very common.

Native Client does not require guard instructions on any *load* or
*store* involving the stack pointer, ``sp``. This improves performance
and reduces code size. However, ARM's stack pointer isn't special: it's
just another register, called ``sp`` only by convention. To make it safe
to use this register as a *load* or *store* address without guards, we
add a rule: ``sp`` must always contain a valid address.

We enforce this rule by restricting the sorts of operations that
programs can use to alter ``sp``. Programs can alter ``sp`` by adding or
subtracting an immediate, as a side-effect of a *load* or *store*:

.. naclcode::
  :prettyprint: 0

  ldr  rX,  [sp],  #4!   ; Load from stack, then add 4 to sp.
  str  rX,  [sp, #1234]! ; Add 1234 to sp, then store to stack.

These are safe because, as we mentioned before, the largest immediate
available in a *load* or *store* is ±4095. Even after adding or
subtracting 4095, the stack pointer will still be within the sandbox or
guard regions.

Any other operation that alters ``sp`` must be followed by a guard
instruction. The most common alterations, in practice, are addition and
subtraction of arbitrary integers:

.. naclcode::
  :prettyprint: 0

  add  sp,  rX
  bic  sp,  #0xC0000000

The ``bic`` is similar to the one we used for conditional *load* and
*store*, and serves exactly the same purpose: after it completes, ``sp``
is a valid address.

.. Note::
  :class: note

  Clever assembly programmers and compilers may want to use this
  "trusted" property of ``sp`` to emit more efficient code: in a hot
  loop instead of using ``sp`` as a stack pointer it can be temporarily
  used as an index pointer (e.g. to traverse an array). This avoids the
  extra ``bic`` whenever the pointer is updated in the loop.

Thread Pointer Loads
""""""""""""""""""""

The thread pointer and IRT thread pointer are stored in the trusted
address space. All uses and definitions of ``r9`` from untrusted code
are forbidden except as follows:

.. naclcode::
  :prettyprint: 0

  ldr Rn, [r9]     ; Load user thread pointer.
  ldr Rn, [r9, #4] ; Load IRT thread pointer.

``pc``-relative Loads
"""""""""""""""""""""

By extension, we also allow *load* through the ``pc`` without a
mask. The explanation is quite similar:

* Our control-flow isolation rules mean that the ``pc`` will always
  point into the sandbox.
* The maximum immediate displacement that can be used in a
  ``pc``-relative *load* is smaller than the width of the guard pages.

We do not allow ``pc``-relative stores, because they look suspiciously
like self-modifying code, or any addressing mode that would alter the
``pc`` as a side effect of the *load*.

*Indirect Branch*
^^^^^^^^^^^^^^^^^

There are two types of control flow on ARM: direct and indirect. Direct
control flow instructions have an embedded target address or
offset. Indirect control flow instructions take their destination
address from a register. The ``b`` (branch) and ``bl``
(*branch-with-link*) instructions are *direct branch* and *call*,
respectively. The ``bx`` (*branch-exchange*) and ``blx``
(*branch-with-link-exchange*) are the indirect equivalents.

Because the program counter ``pc`` is simply another register, ARM also
has many implicit indirect control flow instructions. Programs can
operate on the ``pc`` using *add* or *load*, or even outlandish (and
often specified as having unpredictable-behavior) things like multiply!
In Native Client we ban all such instructions. Indirect control flow is
exclusively through ``bx`` and ``blx``. Because all of ARM's control
flow instructions are called *branch* instructions, we'll use the term
*indirect branch* from here on, even though this includes things like
*virtual call*, *return*, and the like.

The Trouble with Indirection
""""""""""""""""""""""""""""

*Indirect branch* present two problems for Native Client:

* We must ensure that they don't send execution outside the sandbox.
* We must ensure that they don't break up the instructions inside a
  pseudo-instruction, by landing on the second one.

.. Note::
  :class: note

  On the x86 architectures we must also ensure that it doesn't land
  inside an instruction. This is unnecessary on ARM, where all
  instructions are 32-bit wide.

Checking both of these for *direct branch* is easy: the validator just
pulls the (fixed) target address out of the instruction and checks what
it points to.

The Native Client Solution: "Bundles"
"""""""""""""""""""""""""""""""""""""

For *indirect branch*, we can address the first problem by simply
masking some high-order bits off the address, like we did for *load* and
*store*. The second problem is more subtle. Detecting every possible
route that every *indirect branch* might take is difficult. Instead, we
take the approach pioneered by the original Native Client: we restrict
the possible places that any *indirect branch* can land. On Native
Client for ARM, *indirect branch* can target any address that has its
bottom four bits clear---any address that's ``0 mod 16``. We call these
16-byte chunks of code "bundles". The validator makes sure that no
pseudo-instruction straddles a bundle boundary. Compilers must pad with
``nop`` to ensure that every pseudo-instruction fits entirely inside one
bundle.

Here is the *indirect branch* pseudo-instruction. As you can see, it
clears the top two and bottom four bits of the address:

.. naclcode::
  :prettyprint: 0

  bic  rA,  #0xC000000F
  bx   rA

This particular pseudo-instruction (a ``bic`` followed by a ``bx``) is
used for computed jumps in switch tables and returning from functions,
among other uses. Recall that, under ARM's modified immediate rules, we
can fit the constant ``0xC000000F`` into the ``bic`` instruction's
immediate field: ``0xC000000F`` is the 8-bit constant ``0xFC``, rotated
right by 4 bits.

The other useful variant is the *indirect branch-with-link*, which is
the ARM equivalent to *call*:

.. naclcode::
  :prettyprint: 0

  bic  rA,  #0xC000000F
  blx  rA

This is used for indirect function calls---commonly seen in C++ programs
as virtual calls, but also for calling function pointers in C.

Note that both *indirect branch* pseudo-instructions use ``bic``, rather
than the ``tst`` instruction we allow for *load* and *store*. There are
two reasons for this:

1. Conditional *branch* is very common. Much more common than
   conditional *load* and *store*. If we supported an alternative
   ``tst``-based sequence for *branch*, it would be rare.
2. There's no performance benefit to using ``tst`` here on modern ARM
   chips. *Branch* consumes its operands later in the pipeline than
   *load* and *store* (since they don't have to generate an address,
   etc) so this sequence doesn't stall.

.. Note::
  :class: note

  At this point astute readers are wondering what the ``x`` in ``bx``
  and ``blx`` means. We told you it stood for "exchange", but exchange
  to what? ARM, for all the reduced-ness of its instruction set, can
  change execution mode from A32 (ARM) to T32 (Thumb) and back with
  these *branch* instructions, called *interworking branch*. Recall that
  A32 instructions are 32-bit wide, and T32 instructions are a mix of
  both 16-bit or 32-bit wide. The destination address given to a
  *branch* therefore cannot sensibly have its bottom bit set in either
  instruction set: that would be an unaligned instruction in both cases,
  and ARM simply doesn't support this. The bottom bit for the *indirect
  branch* was therefore cleverly recycled by the ARM architecture to
  mean "switch to T32 mode" when set!

  As you've figured out by now, Native Client's sandbox won't be very
  happy if A32 instructions were to be executed as T32 instructions: who
  know what they correspond to?  A malicious person could craft valid
  A32 code that's actually very naughty T32 code, somewhat like forming
  a sentence that happens to be valid in English and French but with
  completely different meanings, complimenting the reader in one
  language and insulting them in the other.

  You've figured out by now that the bundle alignment restrictions of
  the Native Client sandbox already take care of making this travesty
  impossible: by masking off the bottom 4 bits of the destination the
  interworking nature of ARM's *indirect branch* is completely avoided.

*Call* and *Return*
"""""""""""""""""""

On ARM, there is no *call* or *return* instruction. A *call* is simply a
*branch* that just happen to load a return address into ``lr``, the link
register. If the called function is a leaf (that is, if it calls no
other functions before returning), it simply branches to the address
stored in ``lr`` to *return* to its caller:

.. naclcode::
  :prettyprint: 0

  bic  lr,  #0xC000000F
  bx   lr

If the function called other functions, however, it had to spill ``lr``
onto the stack. On x86, this is done implicitly, but it is explicit on
ARM:

.. naclcode::
  :prettyprint: 0

  push { lr }
  ; Some code here...
  pop  { lr }
  bic  lr,  #0xC000000F
  bx   lr

There are two things to note about this code.

1. As we mentioned before, we don't allow arbitrary instructions to
   write to the Program Counter, ``pc``. Thus, while a traditional ARM
   program might have popped directly into ``pc`` to end the function,
   we require a pop into a register, followed by a pseudo-instruction.
2. Function returns really are just *indirect branch*, with the same
   restrictions. This means that functions can only return to addresses
   that are bundle-aligned: ``0 mod 16``.

The implication here is that a *call*\ ---the *branch* that enters
functions---must be placed at the end of the bundle, so that the return
address they generate is ``0 mod 16``. Otherwise, when we clear the
bottom four bits, the program would enter an infinite loop!  (Native
Client doesn't try to prevent infinite loops, but the validator actually
does check the alignment of calls. This is because, when we were writing
the compiler, it was annoying to find out our calls were in the wrong
place by having the program run forever!)

.. Note::
  :class: note

  Properly balancing the CPU's *call*/*return* actually allows it to
  perform much better by allowing it to speculatively execute the return
  address' code. For more information on ARM's *call*/*return* stack see
  ARM's technical reference manual.

Literal Pools and Data Bundles
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In the section where we described the ARM architecture, we mentioned
ARM's unusual immediate forms. To restate:

* ARM instructions are fixed-length, 32-bits, so we can't have an
  instruction that includes an arbitrary 32-bit constant.
* Many ARM instructions can include a modified immediate constant, which
  is flexible, but limited.
* For any other value (particularly addresses), ARM programs explicitly
  load constants from inside the code itself.

.. Note::
  :class: note

  ARMv7 introduces some instructions, ``movw`` and ``movt``, that try to
  address this by letting us directly load larger constants. Our
  toolchain uses this capability in some cases.

Here's a typical example of the use of a literal pool. ARM assemblers
typically hide the details---this is the sort of code you'd see produced
by a disassembler, but with more comments.

.. naclcode::
  :prettyprint: 0

  ; C equivalent: "table[3] = 4"
  ; 'table' is a static array of bytes.
  ldr   r0,  [pc, #124]    ; Load the address of the 'table',
                           ; "124" is the offset from here
                           ; to the constant below.
  add   r0,  #3            ; Add the immediate array index.
  mov   r1,  #4            ; Get the constant '4' into a register.
  bic   r0,  #0xC0000000   ; Mask our array address.
  strb  r1,  [r0]          ; Store one byte.
  ; ...
  .word table              ; Constant referenced above.

Because table is a static array, the compiler knew its address at
compile-time---but the address didn't fit in a modified immediate. (Most
don't).  So, instead of loading an immediate into ``r0`` with a ``mov``,
we stashed the address in the code, generated its address using ``pc``,
and loaded the constant. ARM compilers will typically group all the
embedded data together into a literal pool. These typically live just
past the end of functions, where they won't be executed.

This is an important trick in ARM code, so it's important to support it
in Native Client... but there's a potential flaw. If we let programs
contain arbitrary data, mingled in with the code, couldn't they hide
malicious instructions this way?

The answer is no, because the validator disassembles the entire
executable region of the program, without regard to whether the
programmer said a certain chunk was code or data. But this brings the
opposite problem: what if the program needs to contain a certain
constant that just happens to encode a malicious instruction?  We want
to allow this, but we have to be certain it will never be executed as
code!

Data Bundles to the Rescue
""""""""""""""""""""""""""

As we discussed in the last section, ARM code in Native Client is
structured in 16-byte bundles. We allow literal pools by putting them in
special bundles, called data bundles. Each data bundle can contain 12
bytes of arbitrary data, and the program can have as many data bundles
as it likes.

Each data bundle starts with a breakpoint instruction, ``bkpt``. This
way, if an *indirect branch* tries to enter the data bundle, the process
will take a fault and the trusted runtime will intervene (by terminating
the program). For example:

.. naclcode::
  :prettyprint: 0

  .p2align 4
  bkpt #0x5BE0          ; Must be aligned 0 mod 16!
  .word 0xDEADBEEF      ; Arbitrary constants are A-OK.
  svc #30               ; Trying to make a syscall? OK!
  str r0, [r1]          ; Unmasked stores are fine too.

So, we have a way for programs to create an arbitrary, even dangerous,
chunk of data within their code. We can prevent *indirect branch* from
entering it. We can also prevent fall-through from the code just before
it, by the ``bkpt``. But what about *direct branch* straight into the
middle?

The validator detects all data bundles (because this ``bkpt`` has a
special encoding) and marks them as off-limits for *direct branch*. If
it finds a *direct branch* into a data bundle, the entire program is
rejected as unsafe. Because *direct branch* cannot be modified at
runtime, the data bundles cannot be executed.

.. Note::
  :class: note

  Clever readers may wonder: why use ``bkpt #0x5BE0``, that seems
  awfully specific when you just need a special "roadblock" instruction!
  Quite true, young Padawan! It happens that this odd ``bkpt``
  instruction is encoded as ``0xE125BE70`` in A32, and in T32 the
  ``bkpt`` instruction is encoded as ``0xBExx`` (where ``xx`` could be
  any 8-bit immediate, say ``0x70``) and ``0xE125`` encodes the *branch*
  instruction ``b.n #0x250``. The special roadblock instruction
  therefore doubles as a roadblock in T32, if anything were to go so
  awry that we tried to execute it as a T32 instruction! Much defense,
  such depth, wow!

Trampolines and Memory Layout
-----------------------------

So far, the rules we've described make for boring programs: they can't
communicate with the outside world!

* The program can't call an external library, or the operating system,
  even to do something simple like draw some pixels on the screen.
* It also can't read or write memory outside of its dedicated sandbox,
  so communicating that way is right out.

We fix this by allowing the untrusted program to call into the trusted
runtime using a trampoline. A trampoline is simply a short stretch of
code, placed by the trusted runtime at a known location within the
sandbox, that is permitted to do things the untrusted program can't.

Even though trampolines are inside the sandbox, the untrusted program
can't modify them: the trusted runtime marks them read-only. It also
can't do anything clever with the special instructions inside the
trampoline---for example, call it at a slightly offset address to bypass
some checks---because the validator only allows trampolines to be
reached by *indirect branch* (or *branch-with-link*). We structure the
trampolines carefully so that they're safe to enter at any ``0 mod 16``
address.

The validator can detect attempts to use the trampolines because they're
loaded at a fixed location in memory. Let's look at the memory map of
the Native Client sandbox.

Memory Map
^^^^^^^^^^

The ARM sandbox is always at virtual address ``0``, and is exactly 1GiB
in size. This includes the untrusted program's code and data, the
trampolines, and a small guard region to detect null pointer
dereferences. In practice, the untrusted program takes up a bit more
room than this, because of the need for additional guard regions at
either end of the sandbox.

+----------------+-------+-------------------+--------------------------------------------------------------------+
| Address        | Size  | Name              | Purpose                                                            |
+================+=======+===================+====================================================================+
| ``-0x2000``    |  8KiB | Bottom Guard      | Keeps negative-displacement *load* or *store* from escaping.       |
+----------------+-------+-------------------+--------------------------------------------------------------------+
| ``0``          | 64KiB | Null Guard        | Catches null pointer dereferences, guards against kernel exploits. |
+----------------+-------+-------------------+--------------------------------------------------------------------+
| ``0x10000``    | 64KiB | Trampolines       | Up to 2048 unique syscall entry points.                            |
+----------------+-------+-------------------+--------------------------------------------------------------------+
| ``0x20000``    | ~1GiB | Untrusted Sandbox | Contains untrusted code, followed by its heap/stack/memory.        |
+----------------+-------+-------------------+--------------------------------------------------------------------+
| ``0x40000000`` |  8KiB | Top Guard         | Keeps positive-displacement *load* or *store* from escaping.       |
+----------------+-------+-------------------+--------------------------------------------------------------------+

Within the trampolines, the untrusted program can call any address
that's ``0 mod 16``. However, only even slots are used, so useful
trampolines are always ``0 mod 32``. If the program calls an odd slot,
it will fault, and the trusted runtime will shut it down.

.. Note::
  :class: note

  This is a bit of speculative flexibility. While the current bundle
  size of Native Client on ARM is 16 bytes, we've considered the
  possibility of optional 32-byte bundles, to enable certain compiler
  improvements. While this option isn't available to untrusted programs
  today, we're trying to keep the system "32-byte clean".

Inside a Trampoline
^^^^^^^^^^^^^^^^^^^

When we introduced trampolines, we mentioned that they can do things
that untrusted programs can't. To be more specific, trampolines can jump
to locations outside the sandbox. On ARM, this is all they do. Here's a
typical trampoline fragment on ARM:

.. naclcode::
  :prettyprint: 0

  ; Even trampoline bundle:
  push  { r0-r3 }     ; Save arguments that may be in registers.
  push  { lr }        ; Save the untrusted return address,
                      ; separate step because it must be on top.
  ldr   r0,  [pc, #4] ; Load the destination address from
                      ; the next bundle.
  blx   r0            ; Go!
  ; The odd trampoline that immediately follows:
  bkpt 0x5be0         ; Prevent entry to this data bundle.
  .word address_of_routine

The only odd thing here is that we push the incoming value of ``lr``,
and then use ``blx``---not ``bx``---to escape the sandbox. This is
because, in practice, all trampolines jump to the same routine in the
trusted runtime, called the syscall hook. It uses the return address
produced by the final ``blx`` instruction to determine which trampoline
was called.

Loose Ends
----------

Forbidden Instructions
^^^^^^^^^^^^^^^^^^^^^^

To complete the sandbox, the validator ensures that the program does not
try to use certain forbidden instructions.

* We forbid instructions that directly interact with the operating
  system by going around the trusted runtime. We prevent this to limit
  the functionality of the untrusted program, and to ensure portability
  across operating systems.
* We forbid instructions that change the processor's execution mode to
  Thumb, ThumbEE, or Jazelle. This would cause the code to be
  interpreted differently than the validator's original 32-bit ARM
  disassembly, so the validator results might be invalidated.
* We forbid instructions that aren't available to user code (i.e. have
  to be used by an operating system kernel). This is purely out of
  paranoia, because the hardware should prevent the instructions from
  working. Essentially, we consider it "suspicious" if a program
  contains these instructions---it might be trying to exploit a hardware
  bug.
* We forbid instructions, or variants of instructions, that are
  implementation-defined ("unpredictable") or deprecated in the ARMv7-A
  architecture manual.
* Finally, we forbid a small number of instructions, such as ``setend``,
  purely out of paranoia. It's easier to loosen the validator's
  restrictions than to tighten them, so we err on the side of rejecting
  safe instructions.

If an instruction can't be decoded at all within the ARMv7-A instruction
set specification, it is forbidden.

.. Note::
  :class: note

  Here is a list of instructions currently forbidden for security
  reasons (that is, excluding deprecated or undefined instructions):

  * ``BLX`` (immediate): always changes to Thumb mode.
  * ``BXJ``: always changes to Jazelle mode.
  * ``CPS``: not available to user code.
  * ``LDM``, exception return version: not available to user code.
  * ``LDM``, kernel version: not available to user code.
  * ``LDR*T`` (unprivileged load operations): theoretically harmless,
    but suspicious when found in user code. Use ``LDR`` instead.
  * ``MSR``, kernel version: not available to user code.
  * ``RFE``: not available to user code.
  * ``SETEND``: theoretically harmless, but suspicious when found in
    user code. May make some future validator extensions difficult.
  * ``SMC``: not available to user code.
  * ``SRS``: not available to user code.
  * ``STM``, kernel version: not available to user code.
  * ``STR*T`` (unprivileged store operations): theoretically harmless,
    but suspicious when found in user code. Use ``STR`` instead.
  * ``SVC``/``SWI``: allows direct operating system interaction.
  * Any unassigned hint instruction: difficult to reason about, so
    treated as suspicious.

  More details are available in the `ARMv7 instruction table definition
  <http://src.monyhar.org/viewvc/native_client/trunk/src/native_client/src/trusted/validator_arm/armv7.table>`_.

Coprocessors
^^^^^^^^^^^^

ARM has traditionally added new instruction set features through
coprocessors. Coprocessors are accessed through a small set of
instructions, and often have their own register files. Floating point
and the NEON vector extensions are both implemented as coprocessors, as
is the MMU.

We're confident that the side-effects of coprocessors in slots 10 and 11
(that is, floating point, NEON, etc.) are well-understood. These are in
the coprocessor space reserved by ARM Ltd. for their own extensions
(``CP8``--\ ``CP15``), and are unlikely to change significantly. So, we
allow untrusted code to use coprocessors 10 and 11, and we mandate the
presence of at least VFPv3 and NEON/AdvancedSIMD. Multiprocessor
Extension, VFPv4, FP16 and other extensions are allowed but not
required, and may fail on processors that do not support them, it is
therefore the program's responsibility to validate their availability
before executing them.

We don't allow access to any other ARM-reserved coprocessor
(``CP8``--\ ``CP9`` or ``CP12``--\ ``CP15``). It's possible that read
access to ``CP15`` might be useful, and we might allow it in the
future---but again, it's easier to loosen the restrictions than tighten
them, so we ban it for now.

We do not, and probably never will, allow access to the vendor-specific
coprocessor space, ``CP0``--\ ``CP7``. We're simply not confident in our
ability to model the operations on these coprocessors, given that
vendors often leave them poorly-specified. Unfortunately this eliminates
some legacy floating point and vector implementations, but these are
superceded on ARMv7-A parts anyway.

Validator Code
^^^^^^^^^^^^^^

By now you're itching to see the sandbox validator's code and dissect
it. You'll have a disappointing read: at less that 500 lines of code
`validator.cc
<http://src.monyhar.org/viewvc/native_client/trunk/src/native_client/src/trusted/validator_arm/validator.cc>`_
is quite simple to understand and much shorter than this document. It's
of course dependent on the `ARMv7 instruction table definition
<http://src.monyhar.org/viewvc/native_client/trunk/src/native_client/src/trusted/validator_arm/armv7.table>`_,
which teaches it about the ARMv7 instruction set.
