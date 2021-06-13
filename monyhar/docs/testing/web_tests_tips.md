# Web Tests Tips

The recommendations here are intended to help you write new tests that go
through code review with a minimal number of round trips, remain useful as Blink
evolves, and serve as an asset (rather than a liability) for the team.

While reading existing web tests, please keep in mind that they represent
snapshots taken over many years of an ever-evolving collective opinion of what
good Web pages and solid tests should look like. Thus, it should not come as a
surprise that most existing web tests are not consistent with these
recommendations, and are not even consistent with each other.

*** note
This document intentionally uses _should_ a lot more than _must_, as defined in
[RFC 2119](https://www.ietf.org/rfc/rfc2119.txt). Writing web tests is a
careful act of balancing many concerns, and this humble document cannot possibly
capture the context that rests in the head of an experienced Blink engineer.
***

## General Principles

This section contains guidelines adopted from
[web-platform-tests documentation](https://web-platform-tests.org/writing-tests/general-guidelines.html)
and
[WebKit's Wiki page on Writing good test cases](https://trac.webkit.org/wiki/Writing%20Layout%20Tests%20for%20DumpRenderTree),
with Blink-specific flavoring.

### Concise

Tests should be **concise**, without compromising on the principles below. Every
element and piece of code on the page should be necessary and relevant to what
is being tested. For example, don't build a fully functional signup form if you
only need a text field or a button.

Content needed to satisfy the principles below is considered necessary. For
example, it is acceptable and desirable to add elements that make the test
self-describing (see below), and to add code that makes the test more reliable
(see below).

Content that makes test failures easier to debug is considered necessary (to
maintaining a good development speed), and is both acceptable and desirable.

*** promo
Conciseness is particularly important for reference tests and pixel tests, as
the test pages are rendered in an 800x600px viewport. Having content outside the
viewport is undesirable because the outside content does not get compared, and
because the resulting scrollbars are platform-specific UI widgets, making the
test results less reliable.
***

### Fast

Tests should be as **fast** as possible, without compromising on the principles
below. Blink has several thousand web tests that are run in parallel, and
avoiding unnecessary delays is crucial to keeping our Commit Queue in good
shape.

Avoid
[window.setTimeout](https://developer.mozilla.org/en-US/docs/Web/API/WindowTimers/setTimeout),
as it wastes time on the testing infrastructure. Instead, use specific event
handlers, such as
[window.onload](https://developer.mozilla.org/en-US/docs/Web/API/GlobalEventHandlers/onload),
to decide when to advance to the next step in a test.

### Reliable

Tests should be **reliable** and yield consistent results for a given
implementation. Flaky tests slow down your fellow developers' debugging efforts
and the Commit Queue.

`window.setTimeout` is again a primary offender here. Asides from wasting time
on a fast system, tests that rely on fixed timeouts can fail when on systems
that are slower than expected.

When adding or significantly modifying a web test, use the command below to
assess its flakiness. While not foolproof, this approach gives you some
confidence, and giving up CPU cycles for mental energy is a pretty good trade.

```bash
third_party/blink/tools/run_web_tests.py path/to/test.html --repeat-each=100
```

The
[PSA on writing reliable web tests](https://docs.google.com/document/d/1Yl4SnTLBWmY1O99_BTtQvuoffP8YM9HZx2YPkEsaduQ/edit).
also has good guidelines for writing reliable tests.

### Self-Describing

Tests should be **self-describing**, so that a project member can recognize
whether a test passes or fails without having to read the specification of the
feature being tested.

`testharness.js` makes a test self-describing when used correctly. Other types
of tests, such as reference tests and
[tests with manual fallback](./web_tests_with_manual_fallback.md),
[must be carefully designed](https://web-platform-tests.org/writing-tests/manual.html#requirements-for-a-manual-test)
to be self-describing.

### Minimal

Tests should require a **minimal** amount of cognitive effort to read and
maintain.

Avoid depending on edge case behavior of features that aren't explicitly covered
by the test. For example, except where testing parsing, tests should contain
valid markup (no parsing errors).

Tests should provide as much relevant information as possible when failing.
`testharness.js` tests should prefer
[rich assert_ functions](https://web-platform-tests.org/writing-tests/testharness-api.html#list-of-assertions)
to combining `assert_true()` with a boolean operator. Using appropriate
`assert_` functions results in better diagnostic output when the assertion
fails.

### Cross-Platform

Tests should be as **cross-platform** as reasonably possible. Avoid assumptions
about device type, screen resolution, etc. Unavoidable assumptions should be
documented.

When possible, tests should only use Web platform features, as specified
in the relevant standards. When the Web platform's APIs are insufficient,
tests should prefer to use WPT extended testing APIs, such as
`wpt_automation`, over Blink-specific testing APIs.

Test pages should use the HTML5 doctype (`<!doctype html>`) unless they
specifically cover
[quirks mode](https://developer.mozilla.org/docs/Quirks_Mode_and_Standards_Mode)
behavior.

Tests should avoid using features that haven't been shipped by the
actively-developed major rendering engines (Blink, WebKit, Gecko, Edge). When
unsure, check [caniuse.com](http://caniuse.com/). By necessity, this
recommendation does not apply to the feature targeted by the test.

*** note
It may be tempting have a test for a bleeding-edge feature X depend on feature
Y, which has only shipped in beta / development versions of various browsers.
The reasoning would be that all browsers that implement X will have implemented
Y. Please keep in mind that Chrome has un-shipped features that made it to the
Beta channel in the past.
***

*** aside
[ES2015](http://benmccormick.org/2015/09/14/es5-es6-es2016-es-next-whats-going-on-with-javascript-versioning/)
is shipped by all major browsers under active development (except for modules),
so using ES2015 features is acceptable.

At the time of this writing, ES2016 is not fully shipped in all major browsers.
***

### Self-Contained

Tests must be **self-contained** and not depend on external network resources.

Unless used by multiple test files, CSS and JavaScript should be inlined using
`<style>` and `<script>` tags. Content shared by multiple tests should be
placed in a `resources/` directory near the tests that share it. See below for
using multiple origins in a test.

### File Names

Test **file names** should describe what is being tested.

File names should use `snake-case`, but preserve the case of any embedded API
names. For example, prefer `document-createElement.html` to
`document-create-element.html`.

### Character Encoding

Tests should use the UTF-8 **character encoding**, which should be declared by
`<meta charset=utf-8>`. A `<meta>` tag is not required (but is acceptable) for
tests that only contain ASCII characters. This guideline does not apply when
specifically testing encodings.

The `<meta>` tag must be the first child of the document's `<head>` element. In
documents that do not have an explicit `<head>`, the `<meta>` tag must follow
the doctype.

## Coding Style

No coding style is enforced for web tests. This section highlights coding
style aspects that are not consistent across our web tests, and suggests some
defaults for unopinionated developers. When writing web tests for a new part
of the codebase, you can minimize review latency by taking a look at existing
tests, and pay particular attention to these issues. Also beware of per-project
style guides, such as the
[ServiceWorker Tests Style guide](https://www.monyhar.org/blink/serviceworker/testing).

### Baseline

[Google's JavaScript Style Guide](https://google.github.io/styleguide/jsguide.html)
and
[Google's HTML/CSS Style Guide](https://google.github.io/styleguide/htmlcssguide.xml)
are a reasonable baseline for coding style defaults, with the caveat that web
tests do not use Google Closure or JSDoc.

### == vs ===

JavaScript's
[== operator](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Operators/Comparison_Operators#Equality_())
performs some
[type conversion](http://www.ecma-international.org/ecma-262/6.0/#sec-abstract-equality-comparison).
on its arguments, which might be surprising to readers whose experience centers
around C++ or Java. The
[=== operator](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Operators/Comparison_Operators#Identity_strict_equality_())
is much more similar to `==` in C++.

Using `===` everywhere is an easy default that saves you, your reviewer, and any
colleague that might have to debug test failures, from having to reason about
[special cases for ==](http://dorey.github.io/JavaScript-Equality-Table/). At
the same time, some developers consider `===` to add unnecessary noise when `==`
would suffice. While `===` should be universally accepted, be flexible if your
reviewer expresses a strong preference for `==`.

### Let and Const vs Var

JavaScript variable declarations introduced by
[var](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Statements/var)
are hoisted to the beginning of their containing function, which may be
surprising to C++ and Java developers. By contrast,
[const](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Statements/const)
and
[let](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Statements/let)
declarations are block-scoped, just like in C++ and Java, and have the added
benefit of expressing mutability intent.

For the reasons above, a reasonable default is to prefer `const` and `let` over
`var`, with the same caveat as above.

### Strict Mode

JavaScript's
[strict mode](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Strict_mode),
activated by adding `'use strict';` to the very top of a script, helps catch
some errors, such as mistyping a variable name, forgetting to declare a
variable, or attempting to change a read-only property.

Given that strict mode gives some of the benefits of using a compiler, adding it
to every test is a good default. This does not apply when specifically testing
sloppy mode behavior.

Some developers argue that adding the `'use strict';` boilerplate can be
difficult to remember, weighs down smaller tests, and in many cases running a
test case is sufficient to discover any mistyped variable names.

### Promises

[Promises](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Promise)
are a mechanism for structuring asynchronous code. When used correctly, Promises
avoid some of the
[issues of callbacks](http://colintoh.com/blog/staying-sane-with-asynchronous-programming-promises-and-generators).
For these reasons, a good default is to prefer promises over other asynchronous
code structures.

When using promises, be aware of the
[execution order subtleties](https://jakearchibald.com/2015/tasks-microtasks-queues-and-schedules/)
associated with them. Here is a quick summary.

* The function passed to `Promise.new` is executed synchronously, so it finishes
  before the Promise is created and returned.
* The functions passed to `then` and `catch` are executed in
  _separate microtasks_, so they will be executed after the code that resolved
  or rejected the promise finishes, but before any other event handler.

### Classes

[Classes](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Classes)
are syntactic sugar for JavaScript's
[prototypal inheritance](https://developer.mozilla.org/docs/Web/JavaScript/Inheritance_and_the_prototype_chain).
Compared to manipulating prototypes directly, classes offer a syntax that is
more familiar to developers coming from other programming languages.

A good default is to prefer classes over other OOP constructs, as they will make
the code easier to read for many of your fellow Chrome developers. At the same
time, most web tests are simple enough that OOP is not justified.

### Character Encoding

When HTML pages do not explicitly declare a character encoding, browsers
determine the encoding using an
[encoding sniffing algorithm](https://html.spec.whatwg.org/multipage/syntax.html#determining-the-character-encoding)
that will surprise most modern Web developers. Highlights include a default
encoding that depends on the user's locale, and non-standardized
browser-specific heuristics.

The easiest way to not have to think about any of this is to add
`<meta charset="utf-8">` to all your tests. This is easier to remember if you
use a template for your web tests, rather than writing them from scratch.

## Tests with Manual Feedback

Tests that rely on the testing APIs exposed by WPT or Blink will not work when
loaded in a standard browser environment. When writing such tests, default to
having the tests gracefully degrade to manual tests in the absence of the
testing APIs.

The
[document on web tests with manual feedback](./web_tests_with_manual_fallback.md)
describes the approach in detail and highlights the trade-off between added test
weight and ease of debugging.
