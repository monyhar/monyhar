create {
  source {
    git {
      repo: "https://r8.googlesource.com/r8"
      tag_pattern: "%s-dev"
    }
    patch_dir: "patches"
  }

  build {
    dep: "monyhar/third_party/jdk"
    # gradle cannot be executed correctly under docker env
    no_docker_env: true
  }
}

upload {
  pkg_prefix: "monyhar/third_party"
  universal: true
}
