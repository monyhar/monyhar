# Metrics for Understanding Browser Resize Behaviour
## Motivation
Providing a smooth experience during an interactive resize of a browser window
is remarkably difficult. Each resize operation involves the browser process
(which receives the event), the renderer process (which needs to update the
page, relayout etc.), and the gpu process (which updates the content on
screen). There are various trade-offs made in this process to make the
user-experience better. Having some data to model the resize-behaviour of users
can help us improve the various heuristics we make, and fine-tune them to
improve the user experience. It also makes it easier to write tests to match
the user behaviour and watch for regressions/improvements etc.

## Possible Improvements in Chrome
* The amount of gutter allowed can be adjusted.
  * If user is doing quick-drags to resize, then perhaps the amount of gutter is
  the wrong metric to optimize for.
* Maybe some different UX? For example, scale the content instead of
live-updates for small incremental changes if we can tell with high confidence
that more resize events are on the way, etc.
https://crbug.com/837247

---
## BrowserWindow.Resize.Duration
* units: milliseconds
* owners: sadrul@monyhar.org, mustash-team@google.com
* added: 2018-01-01
* expires: 2018-08-31
* os: windows
* tags: input, interactive

Duration of an interactive resize from start to end. Measured only on Windows.

---
## BrowserWindow.Resize.StepBoundsChange
* units: pixels
* owners: sadrul@monyhar.org
* added: 2018-01-01
* expires: 2018-08-31
* os: windows
* tags: input, interactive

Size changed between two consecutive steps during browser-window resize.
Measured only on Windows.

---
## BrowserWindow.Resize.StepCount
* units: steps
* owners: sadrul@monyhar.org
* added: 2018-01-01
* expires: 2018-08-31
* os: windows
* tags: input, interactive

Number of intermediate resize-steps taken to complete the resize from start to
end. Measured only on Windows.

---
## BrowserWindow.Resize.StepInterval
* units: milliseconds
* owners: sadrul@monyhar.org
* added: 2018-01-01
* expires: 2018-08-31
* os: windows
* tags: input, interactive

Time-interval between two consecutive steps during browser-window resize. An
interactive resize can have many number of small steps. This measures the
interval between two steps. 'Duration' measures the interval between the first
and last steps. Note that a high interval is not necessarily bad (e.g. a user
could pause in the middle of the resize). Measured only on Windows.

---
