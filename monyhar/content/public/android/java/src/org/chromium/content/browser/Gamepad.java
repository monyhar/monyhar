// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content.browser;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.monyhar.base.UserData;
import org.monyhar.content.browser.webcontents.WebContentsImpl;
import org.monyhar.content.browser.webcontents.WebContentsImpl.UserDataFactory;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.device.gamepad.GamepadList;

/**
 * Encapsulates component class {@link GamepadList} for use in content, with regards
 * to its state according to content being attached to/detached from window.
 */
class Gamepad implements WindowEventObserver, UserData {
    private final Context mContext;

    private static final class UserDataFactoryLazyHolder {
        private static final UserDataFactory<Gamepad> INSTANCE = Gamepad::new;
    }

    public static Gamepad from(WebContents webContents) {
        return ((WebContentsImpl) webContents)
                .getOrSetUserData(Gamepad.class, UserDataFactoryLazyHolder.INSTANCE);
    }

    public Gamepad(WebContents webContents) {
        mContext = ((WebContentsImpl) webContents).getContext();
        WindowEventObserverManager.from(webContents).addObserver(this);
    }

    // WindowEventObserver

    @Override
    public void onAttachedToWindow() {
        GamepadList.onAttachedToWindow(mContext);
    }

    @Override
    public void onDetachedFromWindow() {
        GamepadList.onDetachedFromWindow();
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return GamepadList.onGenericMotionEvent(event);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return GamepadList.dispatchKeyEvent(event);
    }
}
