// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef THIRD_PARTY_BLINK_RENDERER_MODULES_WEBGL_WEBGL_UNOWNED_TEXTURE_H_
#define THIRD_PARTY_BLINK_RENDERER_MODULES_WEBGL_WEBGL_UNOWNED_TEXTURE_H_

#include "third_party/blink/renderer/modules/webgl/webgl_texture.h"

namespace blink {

// This class exists to prevent a double-freeing of a texture resource.
// It is also necessary for WebXR's Camera Access feature to be able to
// provide a camera image textures until it's decided how to best expose
// the texture to the WebXR API.
// TODO(https://bugs.monyhar.org/p/monyhar/issues/detail?id=1104340).
class WebGLUnownedTexture final : public WebGLTexture {
 public:
  // The provided GLuint must have been created in the same
  // WebGLRenderingContextBase that is provided. Garbage collection of
  // this texture will not result in any GL calls being issued.
  explicit WebGLUnownedTexture(WebGLRenderingContextBase* ctx,
                               GLuint texture,
                               GLenum target);
  ~WebGLUnownedTexture() override;

 private:
  void DeleteObjectImpl(gpu::gles2::GLES2Interface*) override;
};

}  // namespace blink

#endif  // THIRD_PARTY_BLINK_RENDERER_MODULES_WEBGL_WEBGL_UNOWNED_TEXTURE_H_
