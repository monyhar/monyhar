// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.tools.audio_focus_grabber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * The main activity of AudioFocusGrabber. It starts the background service,
 * and responds to UI button controls.
 */
public class AudioFocusGrabberActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_focus_grabber_activity);
    }

    public void onButtonClicked(View view) {
        Intent intent = new Intent(this, AudioFocusGrabberListenerService.class);
        switch (view.getId()) {
            case R.id.button_gain:
                intent.setAction(AudioFocusGrabberListenerService.ACTION_GAIN);
                break;
            case R.id.button_transient_pause:
                intent.setAction(AudioFocusGrabberListenerService.ACTION_TRANSIENT_PAUSE);
                break;
            case R.id.button_transient_duck:
                intent.setAction(AudioFocusGrabberListenerService.ACTION_TRANSIENT_DUCK);
                break;
            case R.id.button_show_notification:
                intent.setAction(AudioFocusGrabberListenerService.ACTION_SHOW_NOTIFICATION);
                break;
            case R.id.button_hide_notification:
                intent.setAction(AudioFocusGrabberListenerService.ACTION_HIDE_NOTIFICATION);
                break;
            default:
                break;
        }
        startService(intent);
    }

}
