#!/bin/bash -e

# Copyright 2015 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

CHROME_APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MEMORY_INSPECTOR_DIR="$(cd "${CHROME_APP_DIR}/.." && pwd)"
TEMPLATE_DIR="${CHROME_APP_DIR}/template"

OUT_DIR="${1:-${CHROME_APP_DIR}/out}"
APP_FOLDER_NAME="memory_inspector_app"
APP_DIR="${OUT_DIR}/${APP_FOLDER_NAME}"
SANDBOX_DIR="${APP_DIR}/sandbox"
ZIP_FILE="${APP_DIR}.zip"

# NaCl Python prebuilts URL.
NACL_PREBUILTS_BASE_URL="https://gsdview.appspot.com/naclports/builds/pepper_40/trunk-154-geacd680/publish/python/pnacl"

# Memory Inspector prebuilts bucket and destination folder.
MEMORY_INSPECTOR_PREBUILTS_BUCKET="monyhar-telemetry"
MEMORY_INSPECTOR_PREBUILTS_DIR="${MEMORY_INSPECTOR_DIR}/prebuilts"

# Memory Inspector dependencies and destination folder in the sandbox
# filesystem.
MEMORY_INSPECTOR_DEPS=( \
  "memory_inspector/" "classification_rules/" "prebuilts/" "start_web_ui")
MEMORY_INSPECTOR_DEPS_FOLDER="memory_inspector"


# Delete existing app folder and zip file.
rm "${APP_DIR}" -rf
rm "${ZIP_FILE}" -f

# Create a new app folder.
mkdir -p "${APP_DIR}"

# Link all files from the template/ folder.
ln -s -t "${APP_DIR}" "${TEMPLATE_DIR}"/*

# Download NaCl Python prebuilt files.
wget "${NACL_PREBUILTS_BASE_URL}/naclprocess.js" -P "${APP_DIR}" --no-verbose
wget "${NACL_PREBUILTS_BASE_URL}/python.nmf" -P "${SANDBOX_DIR}" --no-verbose
wget "${NACL_PREBUILTS_BASE_URL}/python.pexe" -P "${SANDBOX_DIR}" --no-verbose
wget "${NACL_PREBUILTS_BASE_URL}/pydata_pnacl.tar" -P "${SANDBOX_DIR}" \
  --no-verbose

# Download Memory Inspector prebuilt files.
download_from_google_storage --directory "${MEMORY_INSPECTOR_PREBUILTS_DIR}" \
  --bucket "${MEMORY_INSPECTOR_PREBUILTS_BUCKET}" --no_auth

# Add Memory Inspector dependencies to pydata_pnacl.tar.
tmp_dir="$(mktemp -d)"
(
  cd "${tmp_dir}"
  ln -s "${MEMORY_INSPECTOR_DIR}" "${MEMORY_INSPECTOR_DEPS_FOLDER}"
  for dependency in "${MEMORY_INSPECTOR_DEPS[@]}"; do
    tar --owner="memory-inspector:1002" --group="memory-inspector:1002" \
        --append --dereference --file "${SANDBOX_DIR}/pydata_pnacl.tar" \
        "${MEMORY_INSPECTOR_DEPS_FOLDER}/${dependency}"
  done
)
rm "${tmp_dir}" -rf
echo "Chrome app directory: ${APP_DIR}"

# Zip the app.
(
  cd "${OUT_DIR}"
  zip "${ZIP_FILE}" "${APP_FOLDER_NAME}" -r -q
)
echo "Chrome app zip file: ${ZIP_FILE}"

