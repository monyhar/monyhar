# Extension and App Types

Generally, browser extensions cut across websites and web apps, while apps
provide more isolated functionality. Read on for specifics.

**This is a technical discussion of extension types for Chromium developers.**
Extension developers should refer to http://developer.chrome.com/ for
documentation, usage guidelines and examples.

[TOC]

![Summary of extension types showing browser extensions, packaged/platform apps,
and hosted/bookmark apps](extension_types.png)

## Browser extensions

Browser extensions often provide an interactive toolbar icon, but can also run
without any UI. They may interact with the browser or tab contents, and can
request more extensive permissions than apps.

A browser extension can be identified by a `manifest.json` file without any key
named `app`, `export`, or `theme`.

## Themes

A theme is a special kind of extension that changes the way the browser looks.
Themes are packaged like regular extensions, but they don't contain JavaScript
or HTML code.

A theme can be identified by the presence of a `theme` key in `manifest.json`.

## Shared Modules

Shared modules are permissionless collections of resources that can be shared
between other extensions and apps.

A shared module can be identified by the presence of an `export` key in
`manifest.json`.

## Apps

### Platform app

Platform apps (*v2 packaged apps*) are standalone applications that mostly run
independently of the browser. Their windows look and feel like native
applications but simply host the app's pages.

Most apps, like Calculator and the Files app, create their window(s) and
initialize a UI in response to Chrome's `chrome.app.runtime.onLaunched` event.
Some apps don't show a window but work in the background instead. Platform apps
can connect to more device types than browser extensions have access to.

A platform app can be identified by the presence of an `app.background` key
in the manifest, which provides the script that runs when the app is
launched.

*Platform apps are deprecated on non-Chrome OS platforms.*

### Packaged app (legacy)

[Legacy (v1) packaged apps](https://developer.chrome.com/extensions/apps)
combined the appearance of a [hosted app](#Hosted-app) -- a windowed wrapper
around a website -- with the power of extension APIs. With the launch of
platform apps and the app-specific APIs, legacy packaged apps are deprecated.

A packaged app can be identified by the presence of an
`app.launch.local_path` key in `manifest.json`, which identifies the resource
in the .crx that's loaded when the app is launched.

*Packaged apps are deprecated everywhere.*

### Hosted app

A [hosted app](https://developer.chrome.com/webstore/hosted_apps) is mostly
metadata: a web URL to launch, a list of associated URLs, and a list of
permissions. Chrome asks for these permissions during the app's installation.
Some permissions allow the associated URL to bypass runtime permission prompts
of regular web features. Other than metadata in the manifest and icons, none of
a hosted app's resources come from the extension system.

A hosted app can declare a BackgroundContents, which outlives the browser and
can be scripted from all tabs running the hosted app. Specifying
`allow_js_access: false` is preferred, to allow multiple instances of the hosted
app to run in different processes.

A hosted app can be identified by the presence of an `app.launch.web_url` key in
`manifest.json`, which provides http/https URL that is loaded when the app is
launched.

*Hosted apps are deprecated on non-Chrome OS platforms.*

### Bookmark app

A bookmark app is a simplified hosted app that Chrome creates on demand. When
the user taps "More Tools > Add to desktop..." (or "Add to shelf" on Chrome OS)
in the Chrome menu, Chrome creates a barebones app whose manifest specifies the
current tab's URL. A shortcut to this URL appears in chrome://apps using the
site's favicon.

Chrome then creates a desktop shortcut that will open a browser window with
flags that specify the app and profile. Activating the icon launches the
"bookmarked" URL in a tab or a window.

A bookmark app's `manifest.json` identifies it as a hosted app. However, in the
C++ code, the `Extension` object will return true from its `from_bookmark()`
method.

### Progressive Web App (PWA)

When Progressive Web Apps are installed on desktop a bookmark app is created.
The bookmark app in this case will capture navigations to its scope and opens
them in a dedicated app window instead of the existing browser context.

## Ambiguity surrounding the term "Extension"

In the C++ code, all of the above flavors of extensions and apps are implemented
in terms of the `Extension` class type, and the `//extensions` module. This can
cause confusion, since it means that an app *is-an* `Extension`, although
`Extension::is_extension()` is false.

In code comments, "extension" may be used to refer to non-app extensions, also
known as *browser extensions*.

The three categories of apps are significantly different in terms of
implementation, capabilities, process model, and restrictions. It is usually
necessary to consider them as three separate cases, rather than lumping them
together.

## See also

* [`Manifest::Type` declaration](https://cs.monyhar.org/monyhar/src/extensions/common/manifest.h?gs=cpp%253Aextensions%253A%253Aclass-Manifest%253A%253Aenum-Type%2540monyhar%252F..%252F..%252Fextensions%252Fcommon%252Fmanifest.h%257Cdef&gsn=Type&ct=xref_usages)
* Extensions (3rd-party developer documentation)
    * [Extension APIs](https://developer.chrome.com/extensions/api_index)
    * [Extension manifest file format](
      https://developer.chrome.com/extensions/manifest)
* Apps (3rd-party developer documentation)
    * [Platform app APIs](https://developer.chrome.com/apps/api_index)
    * [Platform app manifest file format](
      https://developer.chrome.com/apps/manifest)
    * [Choosing an app type](https://developer.chrome.com/webstore/choosing)
    * Ancient article introducing the [motivation for apps (outdated)](
      https://developer.chrome.com/webstore/apps_vs_extensions)
