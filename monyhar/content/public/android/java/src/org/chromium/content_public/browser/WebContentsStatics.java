// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content_public.browser;

import org.monyhar.content.browser.framehost.RenderFrameHostDelegate;
import org.monyhar.content.browser.framehost.RenderFrameHostImpl;

/**
 * Static public methods for WebContents.
 */
public class WebContentsStatics {
    /**
     * @return The WebContens associated witht the RenderFrameHost. This can be null.
     */
    public static WebContents fromRenderFrameHost(RenderFrameHost rfh) {
        RenderFrameHostDelegate delegate = ((RenderFrameHostImpl) rfh).getRenderFrameHostDelegate();
        if (delegate == null || !(delegate instanceof WebContents)) {
            return null;
        }
        return (WebContents) delegate;
    }
}
