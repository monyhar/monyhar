// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import org.monyhar.base.ActivityState;
import org.monyhar.base.ApplicationStatus;
import org.monyhar.base.TraceEvent;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.base.library_loader.LibraryLoader;
import org.monyhar.chrome.browser.app.ChromeActivity;

/**
 * Notifies the native power mode arbiter of Chrome app activity changes.
 */
public class ChromePowerModeVoter
        implements ApplicationStatus.ActivityStateListener, ViewTreeObserver.OnDrawListener {
    @SuppressLint("StaticFieldLeak")
    private static ChromePowerModeVoter sInstance;

    private boolean mOnDrawListenerAdded;

    public static ChromePowerModeVoter getInstance() {
        if (sInstance == null) {
            sInstance = new ChromePowerModeVoter();
        }
        return sInstance;
    }

    @Override
    public void onActivityStateChange(Activity activity, @ActivityState int newState) {
        if (!(activity instanceof ChromeActivity)) return;

        boolean active = newState == ActivityState.STARTED || newState == ActivityState.RESUMED;

        // When the main ChromeActivity becomes active, add a ViewTreeObserver to observe draws.
        Window window = activity.getWindow();
        if (window != null) {
            View rootView = window.getDecorView().getRootView();
            ViewTreeObserver treeObserver = rootView.getViewTreeObserver();
            if (active && !mOnDrawListenerAdded) {
                treeObserver.addOnDrawListener(this);
                mOnDrawListenerAdded = true;
            } else if (!active && mOnDrawListenerAdded) {
                treeObserver.removeOnDrawListener(this);
                mOnDrawListenerAdded = false;
            }
        }

        // Tell the arbiter about state changes of the main activity.
        if (!LibraryLoader.getInstance().isInitialized()) return;
        ChromePowerModeVoterJni.get().onChromeActivityStateChange(active);
    }

    @Override
    public void onDraw() {
        TraceEvent.instant("ChromePowerModeVoter.onDraw");
        if (!LibraryLoader.getInstance().isInitialized()) return;
        ChromePowerModeVoterJni.get().onViewTreeDraw();
    }

    public Runnable getTouchEventCallback() {
        return () -> {
            onCoordinatorTouchEvent();
        };
    }

    private void onCoordinatorTouchEvent() {
        TraceEvent.instant("ChromePowerModeVoter.onCoordinatorTouchEvent");
        if (!LibraryLoader.getInstance().isInitialized()) return;
        ChromePowerModeVoterJni.get().onCoordinatorTouchEvent();
    }

    @NativeMethods
    interface Natives {
        void onChromeActivityStateChange(boolean active);
        void onViewTreeDraw();
        void onCoordinatorTouchEvent();
    }
}
