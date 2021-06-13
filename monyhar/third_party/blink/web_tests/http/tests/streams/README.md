# Streams Tests in Blink

Tests for ReadableStream and WritableStream can be found at
LayoutTests/external/wpt/streams. Only things specific to Blink are in this
directory.

* monyhar/ - Tests for functionality specific to Blink.
* resources/ - Contains rs-utils.js, which is used by stream-related tests in
  ../fetch.
* transferables/ - Contains tests for transferable streams. These will be
  upstreamed to wpt once they are standardised.
