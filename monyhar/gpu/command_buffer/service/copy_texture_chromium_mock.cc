// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include "gpu/command_buffer/service/copy_texture_monyhar_mock.h"

namespace gpu {
namespace gles2 {

MockCopyTexImageResourceManager::MockCopyTexImageResourceManager(
    const gles2::FeatureInfo* feature_info)
    : CopyTexImageResourceManager(feature_info) {}
MockCopyTexImageResourceManager::~MockCopyTexImageResourceManager() = default;

MockCopyTextureResourceManager::MockCopyTextureResourceManager() = default;
MockCopyTextureResourceManager::~MockCopyTextureResourceManager() = default;

}  // namespace gles2
}  // namespace gpu
