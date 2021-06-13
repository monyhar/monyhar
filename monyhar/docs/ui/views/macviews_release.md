# MacViews Release Plan

## Handy Links

* [Phase 1 bugs](https://bugs.monyhar.org/p/monyhar/issues/list?can=2&q=Proj%3DMacViews+label%3Aphase1&colspec=ID+Pri+M+Stars+ReleaseBlock+Component+Status+Owner+Summary+OS+Modified&x=m&y=releaseblock&cells=ids)
* [Phase 2 bugs](https://bugs.monyhar.org/p/monyhar/issues/list?can=2&q=Proj%3DMacViews+label%3Aphase2&colspec=ID+Pri+M+Stars+ReleaseBlock+Component+Status+Owner+Summary+OS+Modified&x=m&y=releaseblock&cells=ids)
* [Other MacViews bugs](https://bugs.monyhar.org/p/monyhar/issues/list?can=2&q=Proj%3DMacViews+-label%3APhase1+-label%3APhase2&colspec=ID+Pri+M+Stars+ReleaseBlock+Component+Status+Owner+Summary+OS+Modified&x=m&y=releaseblock&cells=ids)
* [Catalog of monyhar dialogs](https://docs.google.com/spreadsheets/d/1rChQOblJDsXevMxpUpvaPqK3QIMPdmd2iAvJtdeOeeY/edit#gid=0)

## Phase 1: Controls

Implement Shiny Modern L&F for individual controls, most-commonly-used first. In
rough order:

1. Buttons
2. Editboxes
3. Comboboxes
4. Radiobuttons/checkboxes
5. Menubuttons
6. Treeviews
7. Tableviews

This phase overlaps with phase 2.

## Phase 2: WebUI Cocoa Dialogs, Rubberband

Once enough controls are done, wire up the Views versions of WebUI-styled Cocoa
dialogs, behind a new `MacViewsWebUIDialogs` feature. The WebUI-styled Cocoa
dialogs are:

1. Collected/blocked cookies UI
2. Device permissions
3. Extension install
4. HTTP auth
5. One-click signin
6. Site permissions bubble
7. "Card unmask prompt" (TODO(ellyjones): what is this?)
8. Page info dialog

Once all of these dialogs are converted and tested behind the feature, we can
ship to canary and dev channels and watch for any performance or crash rate
regressions. Doing all the WebUI-style dialogs at once will avoid having three
separate dialog UIs.

This phase also includes implementation of rubber-band overscroll and fling
scrolling. This technology exists already in the renderer compositor - it needs
to be transplanted to the ui compositor.

## Phase 3: The Other Dialogs

Once WebUI dialogs are converted en masse, we can convert other dialogs to Views
individually, and ship them without a flag flip or field trial. Cocoa dialogs
that are in native Cocoa style will gradually migrate to Views dialogs that are
in the Shiny Modern style.

## Phase 4: Omnibox & Top Chrome

At this point, all dialogs are in Shiny Modern, but the rest of the browser
chrome is still Cocoa.

Implement Cocoa L&F for any controls still needed for omnibox and top chrome.
TODO(ellyjones): which controls are these?
Implement Views versions of the omnibox and top chrome behind a new flag
`mac-views-browser-chrome`.
Get UI review of the new versions of the omnibox and top chrome.
Make the Views versions the default.

## Phase 5: `mac_views_browser=1`
At this point, all user-visible UI is done via Views, and we need to switch the
entire browser to a Views-only build:

Check for performance regressions against `mac_views_browser=0`.
Check for stability regressions against `mac_views_browser=0`.
Check for a11y regressions through manual testing.
TODO(ellyjones): Figure out how feasible automated a11y regression testing is.
Switch `mac_views_browser` to 1 for Canary.
Cross fingers.
Watch metrics carefully.
If there's no surprising metrics changes or public outcry, keep
mac_views_browser=1 for dev, then beta, then stable.

## Phase 6: delete Cocoa
Since much of the Cocoa code is dead and we are no longer building with
`mac_views_browser=0`, remove dead Cocoa UI code.

