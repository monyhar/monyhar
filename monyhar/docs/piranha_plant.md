# Piranha Plant

Piranha Plant is the name of a project, started in November 2013, that aims to
deliver the future architecture of MediaStreams in Chromium.

Project members are listed in the
[group for the project](https://groups.google.com/a/monyhar.org/forum/#!members/piranha-plant).

The Piranha Plant is a monster plant that has appeared in many of the Super
Mario games. In the original Super Mario Bros, it hid in the green pipes and is
thus an apt name for the project as we are fighting "monsters in the plumbing."

![http://files.hypervisor.fr/img/super_mario_piranha_plant.png](http://files.hypervisor.fr/img/super_mario_piranha_plant.png)

[TOC]

## Background

When the MediaStream spec initially came to be, it was tightly coupled with
PeerConnection. The infrastructure for both of these was initially implemented
primarily in libjingle, and then used by Chromium. For this reason, the
MediaStream implementation in Chromium is still somewhat coupled with the
PeerConnection implementation, it still uses some libjingle interfaces on the
Chromium side, and progress is sometimes more difficult as changes need to land
in libjingle before changes can be made in Chromium.

Since the early days, the MediaStream spec has evolved so that PeerConnection is
just one destination for a MediaStream, multiple teams are or will be consuming
the MediaStream infrastructure, and we have a clearer vision of what the
architecture should look like now that the spec is relatively stable.

## Goals

1.  Document the idealized future design for MediaStreams in Chromium (MS) as
    well as the current state.
1.  Create and execute on a plan to incrementally implement the future design.
1.  Improve quality, maintainability and readability/understandability of the MS
    code.
1.  Make life easier for Chromium developers using MS.
1.  Balance concerns and priorities of the different teams that are or will be
    using MS in Chromium.
1.  Do the above without hurting our ability to produce the WebRTC.org
    deliverables, and without hurting interoperability between Chromium and
    other software built on the WebRTC.org deliverables.

## Deliverables

1.  Project code name: Piranha Plant.
1.  A [design document](https://www.monyhar.org/developers/design-documents/idealized-mediastream-design)
    for the idealized future design (work in progress).
1.  A document laying out a plan for incremental steps to achieve as much of the
    idealized design as is pragmatic. See below for current draft.
1.  A [master bug](https://crbug.com/323223) to collect all existing and
    currently planned work items:
1.  Sub-bugs of the master bug, for all currently known and planned work.
1.  A document describing changed and improved team policies to help us keep
    improving code quality (e.g. naming, improved directory structure, OWNERS
    files). Not started.

## Task List

Here are some upcoming tasks we need to work on to progress towards the
idealized design. Those currently being worked on have emails at the front:

*   General
    *   More restrictive OWNERS
    *   DEPS files to limit dependencies on libjingle
    *   Rename MediaStream{Manager, Dispatcher, DispatcherHandler} to
        CaptureDevice{...} since it is a bit confusing to use the MediaStream
        name here.
    *   Rename MediaStreamDependencyFactory to PeerConnectionDependencyFactory.
    *   Split up MediaStreamImpl.
    *   Change the RTCPeerConnectionHandler to only create the PeerConnection
        and related stuff when necessary.
*   Audio
    *   [xians] Add a Content API where given an audio WebMediaStreamTrack, you
        can register as a sink for that track.
    *   Move RendererMedia, the current local audio track sink interface, to
        //media and change as necessary.
    *   Put a Chrome-side adapter on the libjingle audio track interface.
    *   Move the APM from libjingle to Chrome, putting it behind an experimental
        flag to start with.
    *   Do format change notifications on the capture thread.
    *   Switch to a push model for received PeerConnection audio.
*   Video
    *   [perkj] Add a Chrome-side interface representing a sink for a video
        track.
    *   [perkj] Add a Content API where given a video WebMediaStreamTrack, you
        can register as a sink for that track.
    *   Add a Chrome-side adapter for libjingle’s video track interface, which
        may also need to change.
    *   Implement a Chrome-side VideoSource and constraints handling (currently
        in libjingle).
