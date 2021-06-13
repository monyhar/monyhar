.. include:: /migration/deprecation.inc

================
Design Documents
================

This is a list of design documents for Native Client.  This list
generally covers designs that were implemented.  It does not cover
PPAPI (Pepper).

Dynamic loading and linking:

* `Dynamic loading: Options for supporting dynamic loading, and how they interact with dynamic libraries <http://code.google.com/p/nativeclient/wiki/DynamicLoadingOptions>`_ (2010)

Handling faults (hardware exceptions) in untrusted code:

* `NaCl untrusted fault handling:  guide to the implementation <https://docs.google.com/a/monyhar.org/document/d/1T2KQitbOBz_ALQtr4ONcZcSNCIKNla3DI7t6dMcx5AE/edit>`_

Sandbox security on Windows:

* `Native Client's NTDLL patch on x86-64 Windows <https://src.monyhar.org/viewvc/native_client/trunk/src/native_client/documentation/windows_ntdll_patch.txt?revision=HEAD>`_ (2012)

Debugging using GDB:

* `Providing a GDB debug stub integrated into native_client <https://docs.google.com/a/monyhar.org/document/d/1OtVmgJFC7X7aa57DnyiL4V10vAVax_vcRJp4Mw86lIU/edit>`_ (2012).  This was the main design doc for NaCl's GDB debug stub.
* `Native Client Support for Debugging, Crash Reporting and Hardware Exception Handling -- high level design <https://docs.google.com/a/google.com/document/d/1tu2FEA4EKhBH669iUgRZBDBcEd6jzNQ-0OVn9JI4_qk/edit>`_ (Jan 2012)
* `NaCl: three kinds of crash handling <https://docs.google.com/a/monyhar.org/document/d/19qkl5R4lg-AIDf648Ml-gLRq6eZscjvvdMNWkVu2wLk/edit>`_ (2012).  This is an earlier document.  It contains notes on trusted vs. untrusted crash handling, vs. GDB support.

PNaCl:

* `Stability of the PNaCl bitcode ABI <https://docs.google.com/a/google.com/document/d/1xUlWyXnaRnIUBnmKdOBkgq2O9OqfvaRBLaz82pNdKt0/edit>`_ (2013).  This is an overview of ABI stability issues and the features of LLVM IR that PNaCl is removing.
* `Incrementally simplifying the PNaCl bitcode format <https://docs.google.com/a/monyhar.org/document/d/1HvZJVwS9KeTc0jUvoQjbLapRbStHk3mZ0rPDUHNN96Y/edit>`_ (2013)
* `SJLJ EH: C++ exception handling in PNaCl using setjmp()+longjmp() <https://docs.google.com/a/monyhar.org/document/d/1Bub1bV_IIDZDhdld-zTULE2Sv0KNbOXk33KOW8o0aR4/edit>`_ (2013)

Security hardening:

* `Hiding PNaCl's x86-64 sandbox base address <https://docs.google.com/a/monyhar.org/document/d/1eskaI4353XdsJQFJLRnZzb_YIESQx4gNRzf31dqXVG8/edit>`_ (2013).  This was part of the security hardening we did for enabling PNaCl on the open web.

MIPS support:

* `Design for the NaCl MIPS sandbox <https://code.google.com/p/nativeclient/issues/attachmentText?id=2275&aid=22750018000&name=native-client-mips-0.4.txt>`_ (2012)

Cleanup work:

* `Removing NaCl's dependency on Chromium <https://docs.google.com/a/monyhar.org/document/d/1lycqf4yPMC84011yvuyO_50V8c8COQ8dAe5rNvbeB9o/edit>`_ (2012)

DEPS rolls:

* `Semi-automated NaCl DEPS rolls: Updates to nacl_revision field in Chromium's DEPS file <https://docs.google.com/a/monyhar.org/document/d/1jHoLo9I3CCS1_-4KlIq1OiEMv9cmMuXES2Z9JVpmPtY/edit>`_ (2013).  This is a description of current practice rather than a design doc.

Obsolete (not implemented)
==========================

PNaCl multi-threading support:  The following proposals do not reflect what was implemented in PNaCl in the end.  They are listed here for historical reference.

* `Multi-threading support for a first release of PNaCl <https://docs.google.com/a/monyhar.org/document/d/1HcRiGOaaPLk7pQrGnjXceoM7Px3IwOjjwdiVvJVQNr4/edit>`_ (2013): Proposal for mutex_v2/cond_v2 IRT interfaces.
* `Explicit vs. implicit atomicity guarantees in PNaCl <https://docs.google.com/a/monyhar.org/document/d/1HcRiGOaaPLk7pQrGnjXceoM7Px3IwOjjwdiVvJVQNr4/edit>`_ (2013): Discussion about how to handle atomic memory operations.
