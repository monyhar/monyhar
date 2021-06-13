The render pass list data are recorded using run_benchmark with top
real world sites.

The CL that hacks into display.cc to do the recording needs to be
applied to monyhar:
https://monyhar-review.googlesource.com/c/monyhar/src/+/1962628

The command line to use on Windows to record top_real_world_desktop:
vpython tools\perf\run_benchmark --browser=release rendering.desktop
  --story-tag-filter=top_real_world_desktop

For each site, all frames are zipped into a single file, together with
one frame that with (nearly) full damage and maximum data size.
