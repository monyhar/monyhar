.. _cds2014_cpp:

.. include:: /migration/deprecation.inc

##################################
A Saga of Fire and Water - Codelab
##################################

Introduction
------------

.. include:: cpp_summary.inc

.. include:: ../nacldev/setup_app.inc

Get the Code!
-------------

Rather than start from nothing, for this codelab we've provided
you with a zip file containing a starting point.

Download the codelab::

  curl http://nacltools.storage.googleapis.com/cds2014/cds2014_cpp.zip -O

Unzip it::

  unzip cds2014_cpp.zip

Go into the codelab directory::

  cd cds2014_cpp

Create a new local git repo::

  git init

Add everything::

  git add .

Commit it::

  git commit -am "initial"

While working, you can see what you've changed by running::

  git diff


Fire is cool, let's burn some stuff...
--------------------------------------

Indulging your inner child, lets make some virtual fire!
Use the following shockingly intuitive incantation::

  make fire

You should now see a small popup window, smoldering away.
If you click, you can make more fire!
I think that's pretty cool, but then I selected
the institution of higher learning I attended based
on the integral role fire played in its campus life.

Water
-----

Remarkably, not everyone enjoys the primal illusion of fire.

Your task in this codelab is to transform the rising fire
effect you see before you into a beautiful, tranquil waterfall.
This will require digging into some C++ code.

Before you begin, you'll want to copy our fire program to a new name,
since you might decide later that you like fire better, I know I do::

  cp fire.cc water.cc
  git add water.cc
  git commit -am "adding water"

For this codelab, you'll only need to change `water.cc`.

The task of turning fire into water involves two key challenges:

  * Alter the red-yellow palette of fire into a blue-green one.
  * Reverse upward rising flame into downward falling water.
  * Seed the waterfall from the top instead of the bottom.

At this point you'll want to open up `water.cc` in the editor you
picked earlier.

I see a red door and I want it painted... blue
==============================================

While PPAPI's 2D graphics API uses multi-component RGB pixels,
our flame effect is actually monochrome. A single intensity
value is used in the flame simulation. This is then converted
to color based on a multi-color gradient.
To alter the color-scheme, locate this palette, and exchange
the red component (first) with the blue one (third).

Hint: Focus your energies on the CreatePalette function.

You can test you changes at any time with::

  make water

What goes up...
===============

Now there's the small matter of gravity.
While smoke, and well flame, rises, we want our water to go down.

The simulation of fire loops over each pixel,
bottom row to top row,
diffusing "fire stuff" behind the sweep.
You'll want to reverse this.

Hint: You'll need to change the y loop direction in the UpdateFlames function.

Up high, down low
=================

While you can now use the mouse to inject a trickle of water.
The small line of blue at the bottom isn't much of a waterfall.
Move it to the top to complete the effect.

Hint: You'll want to change the area that the UpdateCoals function mutates.


What you've learned
-------------------

In addition to learning a new appreciation for fire, you've also made water...
And while dusting off your C/C++ image manipulation skills,
you've discovered how easy it is to modify, build,
and run a NaCl application that uses PPAPI.

2D graphics is fun, but now you're ready to check out the wealth of
other
`PPAPI interfaces available
<https://src.monyhar.org/viewvc/chrome/trunk/src/ppapi/cpp/>`_.

While our in-browser environment is rapidly evolving
to become a complete development solution,
for the broadest range of development options, check out the
`NaCl SDK
<https://developer.chrome.com/native-client/cpp-api>`_.

Send us comments and feedback on the `native-client-discuss
<https://groups.google.com/forum/#!forum/native-client-discuss>`_ mailing list,
or ask questions using Stack Overflow's `google-nativeclient
<https://stackoverflow.com/questions/tagged/google-nativeclient>`_ tag.


I hope this codelab has lit a fire in you to go out there,
and bring an awesome C/C++ application to NaCl or PNaCl today!


.. include:: ../nacldev/cleanup_app.inc
