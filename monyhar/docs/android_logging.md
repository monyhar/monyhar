# Logging

[TOC]


## Overview

Logging used to be done using Android's
[android.util.Log](https://developer.android.com/reference/android/util/Log.html).

A wrapper on that is now available:
[org.monyhar.base.Log](/base/android/java/src/org/monyhar/base/Log.java). It
is designed to write logs as belonging to logical groups going beyond single
classes, and to make it easy to switch logging on or off for individual groups.

Usage:

```java
private static final String TAG = "YourModuleTag";
...
Log.i(TAG, "Logged INFO message.");
Log.d(TAG, "Some DEBUG info: %s", data);
```

Output:

```
I/cr_YourModuleTag: ( 999): Logged INFO message
D/cr_YourModuleTag: ( 999): [MyClass.java:42] Some DEBUG info: data.toString
```

Here, **TAG** will be a feature or package name, "MediaRemote" or "NFC" for
example. In most cases, the class name is not needed. It will be prepended by
the "cr\_" prefix to make obvious which logs are coming from Chrome.

### Verbose and Debug logs have special handling

*   `Log.v` and `Log.d` Calls made using `org.monyhar.base.Log` are stripped
    out of production binaries using Proguard. There is no way to get those logs
    in release builds.

*   The file name and line number will be prepended to the log message.
    For higher priority logs, those are not added for performance concerns.

### An exception trace is printed when the exception is the last parameter

As with `android.util.Log`, putting a throwable as last parameter will dump the
corresponding stack trace:

```java
Log.i(TAG, "An error happened: %s", e)
```

```
I/cr_YourModuleTag: ( 999): An error happened: This is the exception's message
I/cr_YourModuleTag: ( 999): java.lang.Exception: This is the exception's message
I/cr_YourModuleTag: ( 999):     at foo.bar.MyClass.test(MyClass.java:42)
I/cr_YourModuleTag: ( 999):     ...
```

Having the exception as last parameter doesn't prevent it from being used for
string formatting.

## Logging Best Practices

### Rule #1: Never log user data or PII (Personal Identification Information)

This is a huge concern, because other applications can access the log and
extract a lot of data from your own by doing so. Even if JellyBean restricted
this, people are going to run your application on rooted devices and allow some
apps to access it. Also anyone with USB access to the device can use ADB to get
the full logcat and get the same data right now.

If you really need to print something, print a series of Xs instead
(e.g. "XXXXXX"), or print a truncated hash of the data instead. Truncation is
required to make it harder for an attacker to recover the full data through
rainbow tables and similar methods.

Similarly, avoid dumping API keys, cookies, IP addresses, URLs, page content,
etc...

### Rule #2: Do not build debug logs in production code

The log methods are removed in release builds using Proguard. Because log
messages might not be written, the cost of creating them should also be avoided.
This can be done using three complementary ways:

#### Use string formatting instead of concatenations

```java
// BAD
Log.d(TAG, "I " + preference + " writing logs.");

// BETTER
Log.d(TAG, "I %s writing logs.", preference);
```

Proguard removes the method call itself, but doesn't do anything about the
arguments. The method's arguments will still be computed and provided as
input. The first call above will always lead to the creation of a
`StringBuilder` and a few concatenations, while the second just passes the
arguments and won't need that.

#### Guard expensive calls

Sometimes the values to log aren't readily available and need to be computed
specially. This should be avoided when logging is disabled.

```java
static private final boolean DEBUG = false;  // debug toggle.
...
if (DEBUG) {
  Log.i(TAG, createThatExpensiveLogMessage(activity))
}
```

Because the variable is a `static final` that can be evaluated at compile
time, the Java compiler will optimize out all guarded calls from the
generated `.class` file. Changing it however requires editing each of the
files for which debug should be enabled and recompiling.

#### Annotate debug functions with the `@RemovableInRelease` annotation.

That annotation tells Proguard to assume that a given function has no side
effects, and is called only for its returned value. If this value is unused,
the call will be removed. If the function is not called at all, it will also
be removed. Since Proguard is already used to strip debug and verbose calls
out of release builds, this annotation allows it to have a deeper action by
removing also function calls used to generate the log call's arguments.

```java
/* If that function is only used in Log.d calls, proguard should
 * completely remove it from the release builds. */
@RemovableInRelease
private static String getSomeDebugLogString(Thing[] things) {
  StringBuilder sb = new StringBuilder(
      "Reporting " + thing.length + " things: ");
  for (Thing thing : things) {
    sb.append('\n').append(thing.id).append(' ').append(report.foo);
  }
  return sb.toString();
}

public void bar() {
  ...
  Log.d(TAG, getSomeDebugLogString(things)); /* The line is removed in
                                              *  release builds. */
}
```

Again, this is useful only if the input to that function are variables
already available in the scope. The idea is to move computations,
concatenations, etc. to a place where that can be removed when not needed,
without invading the main function's logic. It can then have a similar
effect as guarding with a static final property that would be enabled in
Debug and disabled in Release.

### Rule #3: Favor small log messages

This is still related to the global fixed-sized kernel buffer used to keep all
logs. Try to make your log information as terse as possible. This reduces the
risk of pushing interesting log data out of the buffer when something really
nasty happens. It's really better to have a single-line log message, than
several ones. I.e. don't use:

```java
Log.GROUP.d(TAG, "field1 = %s", value1);
Log.GROUP.d(TAG, "field2 = %s", value2);
Log.GROUP.d(TAG, "field3 = %s", value3);
```

Instead, write this as:

```java
Log.d(TAG, "field1 = %s, field2 = %s, field3 = %s", value1, value2, value3);
```

That doesn't seem to be much different if you count overall character counts,
but each independent log entry also implies a small, but non-trivial header, in
the kernel log buffer. And since every byte count, you can also try something
even shorter, as in:

```java
Log.d(TAG, "fields [%s,%s,%s]", value1, value2, value3);
```

## Filtering logs

Logcat allows filtering by specifying tags and the associated level:

```shell
adb logcat [TAG_EXPR:LEVEL]...
adb logcat cr_YourModuleTag:D *:S
```

This shows only logs having a level higher or equal to DEBUG for
`cr_YourModuleTag`, and SILENT (nothing is logged at this level or higher, so it
silences the tags) for everything else. You can persist a filter by setting an
environment variable:

```shell
export ANDROID_LOG_TAGS="cr_YourModuleTag:D *:S"
```

The syntax does not support tag expansion or regular expressions other than `*`
for all tags. Please use `grep` or a similar tool to refine your filters
further.

For more, see the [related page on developer.android.com]
(https://developer.android.com/tools/debugging/debugging-log.html#filteringOutput)

## Logs in JUnit tests

We use [robolectric](http://robolectric.org/) to run our JUnit tests. It
replaces some of the Android framework classes with "Shadow" classes
to ensure that we can run our code in a regular JVM. `android.util.Log` is one
of those replaced classes, and by default calling `Log` methods doesn't print
anything.

That default is not changed in the normal configuration, but if you need to
enable logging locally or for a specific test, just add those few lines to your
test:

```java
@Before
public void setUp() {
  ShadowLog.stream = System.out;
  // Your other setup here
}
```
