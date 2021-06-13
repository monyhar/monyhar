# Content Suggestions UI: Architecture and Package Overview

## Introduction

This document describes the architecture for the content suggestions UI. See the
[internal project page](https://goto.google.com/chrome-content-suggestions) for
more info about the project. This document covers the general principles and
some aspects of the implementation, to be seen both as explanation of our
solution and guidelines for future developments.


## Goals

- **Make development easier.** Code should be well-factored. Test coverage
  should be ubiquitous, and writing tests shouldn't be burdensome. Support for
  obsolete features should be easy to remove.

- **Allow for radical UI changes.** The core architecture of the package should
  be structured to allow for flexibility and experimentation in the UI. This
  means it generally shouldn't be tied to any particular UI surface, and
  specifically that it is flexible enough to accomodate both the current NTP and
  its evolutions.


## Principles

- **Decoupling.** Components should not depend on other components explicitly.
  Where items interact, they should do so through interfaces or other
  abstractions that prevent tight coupling.

- **Encapsulation.** A complement to decoupling is encapsulation. Components
  should expose little specifics about their internal state. Public APIs should
  be as small as possible. Architectural commonalities (for example, the use of
  a common interface for ViewHolders) will mean that the essential interfaces
  for complex components can be both small and common across many
  implementations. Overall the combination of decoupling and encapsulation means
  that components of the package can be rearranged or removed without impacting
  the others.

- **Separation of Layers.** Components should operate at a specific layer in the
  adapter/view holder system, and their interactions with components in other
  layers should be well defined.


## Core Anatomy

### The RecyclerView / Adapter / ViewHolder pattern

The UI is conceptually a list of views, and as such we are using the standard
system component for rendering long and/or complex lists: the
[RecyclerView][rv_doc]. It comes with a couple of classes that work together to
provide and update data, display views and recycle them when they move out of
the viewport.

Summary of how we use that pattern for suggestions:

- **RecyclerView:** The list itself. It asks the Adapter for data for a given
  position, decides when to display it and when to reuse existing views to
  display new data. It receives user interactions, so behaviours such as
  swipe-to-dismiss or snap scrolling are implemented at the level of the
  RecyclerView.

- **Adapter:** It holds the data and is the RecyclerView's feeding mechanism.
  For a given position requested by the RecyclerView, it returns the associated
  data, or creates ViewHolders for a given data type. Another responsibility of
  the Adapter is being a controller in the system by forwarding notifications
  between ViewHolders and the RecyclerView, requesting view updates, etc.

- **ViewHolder:** They hold views and allow efficiently updating the data they
  display. There is one for each view created, and as views enter and exit the
  viewport, the RecyclerView requests them to update the view they hold for the
  data retrieved from the Adapter.

For more info, check out [this tutorial][detailed tutorial] that gives more
explanations.

A specificity of our usage of this pattern is that our data is organised as a
tree rather than as a flat list (see the next section for more info on that), so
the Adapter also has the role of making that tree appear flat for the
RecyclerView.

[rv_doc]: https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html
[detailed tutorial]: http://willowtreeapps.com/ideas/android-fundamentals-working-with-the-recyclerview-adapter-and-viewholder-pattern/


### Representation of the data: the node tree

#### Problem

- RecyclerView.Adapter exposes items as a single list.
- The Cards UI has nested structure: the UI has a list of card sections, each
  section has a list of cards, etc.
- There are dependencies between nearby items: e.g. a status card is shown if
  the list of suggestion cards is empty.
- We want to avoid tight coupling: A single adapter coupling the logic for
  different UI components together, a list of items coupling the model
  (SnippetArticle) to the controller, etc.
- Triggering model changes in parts of the UI is complicated, since item
  offsets need to be adjusted globally.

#### Solution

Build a tree of adapter-like nodes.

- Each node represents any number of items:
  * A single node can represent a homogenous list of items.
  * An "optional" node can represent zero or one item (allowing toggling its
    visibility).
- Inner nodes dispatch methods to their children.
- Child nodes notify their parent about model changes. Offsets can be adjusted
  while bubbling changes up the hierarchy.
- Polymorphism allows each node to represent / manage its own items however it
  wants.

Making modification to the TreeNode:

- ChildNode silently swallows notifications before its parent is assigned.
  This allows constructing tree or parts thereof without sending spurious
  notifications during adapter initialization.
- Attaching a child to a node sets its parent and notifies about the number of
  items inserted.
- Detaching a child notifies about the number of items removed and clears the
  parent.
- The number of items is cached and updated when notifications are sent to the
  parent, meaning that a node is _required_ to send notifications any time its
  number of items changes.

As a result of this design, tree nodes can be added or removed depending on the
current setup and the experiments enabled. Since nothing is hardcoded, only the
initialisation changes. Nodes are specialised and are concerned only with their
own functioning and don't need to care about their neighbours.


### Interactions with the rest of Chrome

To make the package easily testable and coherent with our principles,
interactions with the rest of Chrome goes through a set of interfaces. They are
implemented by objects passed around during the object's creation. See their
javadoc and the unit tests for more info.

- [`SuggestionsUiDelegate`](SuggestionsUiDelegate.java)
- [`SuggestionsNavigationDelegate`](SuggestionsNavigationDelegate.java)
- [`SuggestionsMetrics`](SuggestionsMetrics.java)
- [`SuggestionsRanker`](SuggestionsRanker.java)
- [`ContextMenuManager.Delegate`](../ntp/ContextMenuManager.java)


## Appendix

### Sample operations

#### 1. Inserting an item

Context: A node is notified that it should be inserted. This is simply mixing
the standard RecyclerView pattern usage from the system framework with our data
tree.

Sample code path: [`SigninPromo.SigninObserver#onSignedOut()`][cs_link_1]

- A Node wants to insert a new child item.
- The Node notifies its parent of the range of indices to be inserted
- Parent maps the range of indices received from the node to is own range and
  propagates the notification upwards, repeating this until it reaches the root
  node, which is the Adapter.
- The Adapter notifies the RecyclerView that it has new data about a range of
  positions where items should be inserted.
- The RecyclerView requests from the Adapter the view type of the data at that
  position.
- The Adapter propagates the request down the tree, the leaf for that position
  eventually returns a value
- If the RecyclerView does not already have a ViewHolder eligible to be recycled
  for the returned type, it asks the Adapter to create a new one.
- The RecyclerView asks the Adapter to bind the data at the considered position
  to the ViewHolder it allocated for it.
- The Adapter transfers the ViewHolder down the tree to the leaf associated to
  that position
- The leaf node updates the view holder with the data to be displayed.
- The RecyclerView performs the associated canned animation, attaches the view
  and displays it.

[cs_link_1]: https://cs.monyhar.org/monyhar/src/chrome/android/java/src/org/monyhar/chrome/browser/ntp/cards/SignInPromo.java?l=174&rcl=da4b23b1d2a82705f7f4fdfb6c9c8de00341c0af

#### 2. Modifying an existing item

Context: A node is notified that it needs to update some of the data that is
already displayed. In this we also rely on the RecyclerView mechanism of partial
updates that is supported in the framework, but our convention is to use
callbacks as notification payload.

Sample code path: [`TileGrid#onTileOfflineBadgeVisibilityChanged()`][cs_link_2]

- A Node wants to update the view associated to a currently bound item.
- The Node notifies its parent that a change happened at a specific position,
  using a callback as payload.
- The notification bubbles up to the Adapter, which notifies the RecyclerView.
- The RecyclerView calls back to the Adapter with the ViewHolder to modify and
  the payload it received.
- The Adapter runs the callback, passing the ViewHolder as argument.

[cs_link_2]: https://cs.monyhar.org/monyhar/src/chrome/android/java/src/org/monyhar/chrome/browser/suggestions/TileGrid.java?l=78&rcl=da4b23b1d2a82705f7f4fdfb6c9c8de00341c0af
