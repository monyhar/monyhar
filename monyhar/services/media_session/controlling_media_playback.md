# Controlling Media Playback

A media session is a single abstract representation of a single playing source
in the Chrome ecosystem. This could be something like an ARC++ app or a browser
tab.

Sessions request audio focus from the media session service and the service
maintains a connection to each media session on the platform. This can be used
to receive metadata and control media playback in a unified way on Chrome.

# Audio Focus

When audio focus is enabled all the different media sessions will request audio
focus. A media session can request three different types of audio focus:

* Gain - If a gain session acquires focus then all other sessions will stop
  playing and will not be resumed even when the session is destroyed. In most
  cases, this is the type that will be used.
* Gain Transient - If a transient session acquires focus then all other sessions
  will stop playing, but will be resumed when that session is destroyed. This is
  used for temporary playbacks.
* Gain Transient May Duck - If a ducking session acquires focus then all other
  sessions will continue playing but at a reduced volume. When that session is
  destroyed it will continue at the previous volume. This is used for short
  sounds e.g. notifications.

Each WebContents (tab) in Chrome has it's own media session and will request
Gain audio focus in most cases. If the media is only a few seconds then it will
request the ducking focus type.

In ARC++ each app can make audio focus requests using the Android Audio Manager
API. Each of these requests is a media session in Chrome and can be any one of
the three above audio focus types.

To reduce the impact of single session audio focus we currently have grouping
enabled. By default, all media sessions will have a unique "group id". All
browser media session have the same group id. When a session acquires focus it
will not pause any other session with the same group id. Likewise, if playback
is resumed automatically all sessions with the same group id will be resumed.
This allows all the browser tabs to share audio focus and play concurrently, but
still behave as individual media sessions. Ducking is unaffected by group ids.

# Active Media Session

The active media session is the session that would be controlled if there is a
user action (e.g. pressing the play/pause key on a keyboard). Part of the state
that the media session exposes is `is_controllable`. If this boolean is true
then the media session can be controlled by the user. The active media session
will be the top most controllable media session.

# Audio Focus Observer

The media session service can be accessed via Mojo and currently lives in the
browser process. Clients can implement [AudioFocusObserver](https://cs.monyhar.org/monyhar/src/services/media_session/public/mojom/media_session.mojom)
and add themselves as an observer using the [AudioFocusManager](https://cs.monyhar.org/monyhar/src/services/media_session/public/mojom/media_session.mojom)
mojo API. The observer will then receive notifications when the active media
session changes. This can be used to determine whether there is any current
media playback.

# Media Controller Manager

The media session service also exposes a [MediaControllerManager](https://cs.monyhar.org/monyhar/src/services/media_session/public/mojom/media_controller.mojom)
mojo API. This can be used to create a [MediaController](https://cs.monyhar.org/monyhar/src/services/media_session/public/mojom/media_controller.mojom)
instance. These can be used to control and observe a media session. This can
be an individual media session or the active media session.

If the controller is created using `CreateActiveMediaController` then it will
follow the active media session. This means you do not need to create a new
media controller if the active media session changes.

**There is also a MediaSession mojo API. This is used for session / service
communication and should not be used for control.**.

# Media Session Observer

There is also a [MediaSessionObserver](https://cs.monyhar.org/monyhar/src/services/media_session/public/mojom/media_session.mojom)
mojo API that clients can implement. They can then add themselves as an observer
using the `AddObserver` call on the `MediaController` api. When an observer is
added it will be flushed with all the latest information from the currently
active media session.

Again, this will automatically route to the active media session. If that
changes then we will flush all methods on the observer with the information from
the new active media session.

# Testing

There is a [MockMediaSession](https://cs.monyhar.org/monyhar/src/services/media_session/public/cpp/test/mock_media_session.h)
C++ class that can be used for simulating a media session in unit tests. The
[//services/media_session/public/cpp/test](https://cs.monyhar.org/monyhar/src/services/media_session/public/cpp/test/)
directory also contains a number of other useful test utilities.

# Current Media Session service integrations

The following clients are integrated with the media session service and expose
a media session and request audio focus:

* content::WebContents
* ARC++ apps (requires Android Pie)
* Assistant

Questions? - Feel free to reach out to beccahughes@monyhar.org.
