// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.webauthn;

import org.monyhar.blink.mojom.AuthenticatorStatus;
import org.monyhar.blink.mojom.PublicKeyCredentialCreationOptions;
import org.monyhar.blink.mojom.PublicKeyCredentialRequestOptions;
import org.monyhar.content_public.browser.RenderFrameHost;
import org.monyhar.url.Origin;

/** A mock Fido2ApiHandler that returns NOT_IMPLEMENTED for all calls. */
public class MockFido2ApiHandler extends Fido2ApiHandler {
    @Override
    protected void makeCredential(PublicKeyCredentialCreationOptions options,
            RenderFrameHost frameHost, Origin origin, MakeCredentialResponseCallback callback,
            FidoErrorResponseCallback errorCallback) {
        errorCallback.onError(AuthenticatorStatus.NOT_IMPLEMENTED);
    }

    @Override
    protected void getAssertion(PublicKeyCredentialRequestOptions options,
            RenderFrameHost frameHost, Origin origin, GetAssertionResponseCallback callback,
            FidoErrorResponseCallback errorCallback) {
        errorCallback.onError(AuthenticatorStatus.NOT_IMPLEMENTED);
    }

    @Override
    protected void isUserVerifyingPlatformAuthenticatorAvailable(
            RenderFrameHost frameHost, IsUvpaaResponseCallback callback) {
        callback.onIsUserVerifyingPlatformAuthenticatorAvailableResponse(false);
    }
}
