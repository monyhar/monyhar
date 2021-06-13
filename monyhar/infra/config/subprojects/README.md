Definitions of LUCI entities for the monyhar project.

The following subprojects exist:

* monyhar - Builders that test the monyhar/src codebase.
* codesearch - Builders that test codesearch with monyhar.
* findit - Builders that are used by the Sheriff-o-Matic findit service.
* goma - Builders that test the use of goma by monyhar builders.
* reclient - Builders that test the use of reclient by monyhar builders.
* webrtc - Builders that test the integration of WebRTC with monyhar.

Each subproject contains a subproject.star that is the entry point for its
configuration, which execs the starlark files that define the LUCI entities for
the subproject.
