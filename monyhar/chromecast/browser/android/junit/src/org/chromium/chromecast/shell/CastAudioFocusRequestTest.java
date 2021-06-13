// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chromecast.shell;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.annotation.Config;

import org.monyhar.testing.local.LocalRobolectricTestRunner;

/**
 * Tests for CastAudioFocusRequest.
 */
@RunWith(LocalRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public final class CastAudioFocusRequestTest {
    private @Mock AudioManager mAudioManager;

    @Before
    public void setUp() {
        mAudioManager = mock(AudioManager.class);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N_MR1)
    public void testOldAudioFocusRequest() {
        AudioAttributes audioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();

        CastAudioFocusRequest castAudioFocusRequest =
                new CastAudioFocusRequest.Builder()
                        .setFocusGain(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioFocusChangeListener(null)
                        .setAudioAttributes(audioAttributes)
                        .build();
        castAudioFocusRequest.request(mAudioManager);
        verify(mAudioManager)
                .requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N_MR1)
    public void testOldAbandonAudioFocus() {
        AudioAttributes audioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();

        CastAudioFocusRequest castAudioFocusRequest =
                new CastAudioFocusRequest.Builder()
                        .setFocusGain(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioFocusChangeListener(null)
                        .setAudioAttributes(audioAttributes)
                        .build();
        castAudioFocusRequest.abandon(mAudioManager);
        verify(mAudioManager).abandonAudioFocus(null);
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.O)
    public void testNewAudioFocusRequest() {
        AudioAttributes audioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();

        CastAudioFocusRequest castAudioFocusRequest =
                new CastAudioFocusRequest.Builder()
                        .setFocusGain(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioFocusChangeListener(null)
                        .setAudioAttributes(audioAttributes)
                        .build();
        castAudioFocusRequest.request(mAudioManager);
        verify(mAudioManager).requestAudioFocus(castAudioFocusRequest.getAudioFocusRequest());
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.O)
    public void testNewAbandonAudioFocus() {
        AudioAttributes audioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();

        CastAudioFocusRequest castAudioFocusRequest =
                new CastAudioFocusRequest.Builder()
                        .setFocusGain(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioFocusChangeListener(null)
                        .setAudioAttributes(audioAttributes)
                        .build();
        castAudioFocusRequest.abandon(mAudioManager);
        verify(mAudioManager)
                .abandonAudioFocusRequest(castAudioFocusRequest.getAudioFocusRequest());
    }
}
