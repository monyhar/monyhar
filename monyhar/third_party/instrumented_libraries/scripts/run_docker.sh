#!/bin/bash
# Copyright 2018 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

SUDO=
CHROMIUM_SRC_DIR=$(realpath $(dirname $(dirname $(dirname $(dirname "${BASH_SOURCE[0]}")))))

if [ ! $(getent group docker | grep $USER) ]; then
  echo "You're not in the 'docker' group, so we need to run docker with sudo."
  SUDO=sudo
fi

$SUDO docker run --rm --shm-size=2g --privileged --cap-add=all -ti --workdir "$CHROMIUM_SRC_DIR" -e HOST_UID=$UID -v "$CHROMIUM_SRC_DIR:$CHROMIUM_SRC_DIR" trusty-monyhar "$@"
