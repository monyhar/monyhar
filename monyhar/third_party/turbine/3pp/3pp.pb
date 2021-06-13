create {
  source {
    script { name: "fetch.py" }
    unpack_archive: true
    patch_version: "cr0"
  }

  build {
    dep: "monyhar/third_party/jdk"
  }
}

upload {
  pkg_prefix: "monyhar/third_party"
  universal: true
}
