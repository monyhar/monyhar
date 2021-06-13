// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chromecast.shell;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Build;
import android.util.SparseIntArray;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAudioManager;

import org.monyhar.chromecast.base.Controller;
import org.monyhar.chromecast.base.Observable;
import org.monyhar.chromecast.base.ReactiveRecorder;
import org.monyhar.testing.local.LocalRobolectricTestRunner;

/**
 * Tests for CastAudioManager.
 */
@RunWith(LocalRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CastAudioManagerTest {
    // An example request that can be provided to requestAudioFocus().
    private static CastAudioFocusRequest buildFocusRequest() {
        return new CastAudioFocusRequest.Builder()
                .setFocusGain(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(new AudioAttributes.Builder()
                                            .setUsage(AudioAttributes.USAGE_MEDIA)
                                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                            .build())
                .build();
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N_MR1)
    public void testAudioFocusScopeDeactivatesWhenRequestGranted() {
        CastAudioManager audioManager =
                CastAudioManager.getAudioManager(RuntimeEnvironment.application);
        ShadowAudioManager shadowAudioManager = Shadows.shadowOf(audioManager.getInternal());
        Controller<CastAudioFocusRequest> requestAudioFocusState = new Controller<>();
        Observable<CastAudioManager.AudioFocusLoss> lostAudioFocusState =
                audioManager.requestAudioFocusWhen(requestAudioFocusState);
        ReactiveRecorder lostAudioFocusRecorder = ReactiveRecorder.record(lostAudioFocusState);
        lostAudioFocusRecorder.verify().opened(CastAudioManager.AudioFocusLoss.NOT_REQUESTED).end();
        requestAudioFocusState.set(buildFocusRequest());
        lostAudioFocusRecorder.verify().closed(CastAudioManager.AudioFocusLoss.NOT_REQUESTED).end();
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N_MR1)
    public void testAudioFocusLostWhenFocusRequestStateIsReset() {
        CastAudioManager audioManager =
                CastAudioManager.getAudioManager(RuntimeEnvironment.application);
        ShadowAudioManager shadowAudioManager = Shadows.shadowOf(audioManager.getInternal());
        Controller<CastAudioFocusRequest> requestAudioFocusState = new Controller<>();
        Observable<CastAudioManager.AudioFocusLoss> lostAudioFocusState =
                audioManager.requestAudioFocusWhen(requestAudioFocusState);
        ReactiveRecorder lostAudioFocusRecorder = ReactiveRecorder.record(lostAudioFocusState);
        lostAudioFocusRecorder.verify().opened(CastAudioManager.AudioFocusLoss.NOT_REQUESTED).end();
        requestAudioFocusState.set(buildFocusRequest());
        shadowAudioManager.getLastAudioFocusRequest().listener.onAudioFocusChange(
                AudioManager.AUDIOFOCUS_GAIN);
        lostAudioFocusRecorder.verify().closed(CastAudioManager.AudioFocusLoss.NOT_REQUESTED).end();
        requestAudioFocusState.reset();
        lostAudioFocusRecorder.verify().opened(CastAudioManager.AudioFocusLoss.NORMAL).end();
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N_MR1)
    public void testAudioFocusScopeActivatedWhenAudioFocusIsLostButRequestStillActive() {
        CastAudioManager audioManager =
                CastAudioManager.getAudioManager(RuntimeEnvironment.application);
        ShadowAudioManager shadowAudioManager = Shadows.shadowOf(audioManager.getInternal());
        Controller<CastAudioFocusRequest> requestAudioFocusState = new Controller<>();
        Observable<CastAudioManager.AudioFocusLoss> lostAudioFocusState =
                audioManager.requestAudioFocusWhen(requestAudioFocusState);
        ReactiveRecorder lostAudioFocusRecorder = ReactiveRecorder.record(lostAudioFocusState);
        lostAudioFocusRecorder.verify().opened(CastAudioManager.AudioFocusLoss.NOT_REQUESTED).end();
        requestAudioFocusState.set(buildFocusRequest());
        AudioManager.OnAudioFocusChangeListener listener =
                shadowAudioManager.getLastAudioFocusRequest().listener;
        listener.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);
        lostAudioFocusRecorder.verify().closed(CastAudioManager.AudioFocusLoss.NOT_REQUESTED).end();
        listener.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS);
        lostAudioFocusRecorder.verify().opened(CastAudioManager.AudioFocusLoss.NORMAL).end();
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N_MR1)
    public void testAudioFocusScopeWhenAudioFocusIsLostAndRegained() {
        CastAudioManager audioManager =
                CastAudioManager.getAudioManager(RuntimeEnvironment.application);
        ShadowAudioManager shadowAudioManager = Shadows.shadowOf(audioManager.getInternal());
        Controller<CastAudioFocusRequest> requestAudioFocusState = new Controller<>();
        Observable<CastAudioManager.AudioFocusLoss> lostAudioFocusState =
                audioManager.requestAudioFocusWhen(requestAudioFocusState);
        ReactiveRecorder lostAudioFocusRecorder = ReactiveRecorder.record(lostAudioFocusState);
        lostAudioFocusRecorder.verify().opened(CastAudioManager.AudioFocusLoss.NOT_REQUESTED).end();
        requestAudioFocusState.set(buildFocusRequest());
        AudioManager.OnAudioFocusChangeListener listener =
                shadowAudioManager.getLastAudioFocusRequest().listener;
        listener.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);
        lostAudioFocusRecorder.verify().closed(CastAudioManager.AudioFocusLoss.NOT_REQUESTED).end();
        listener.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS);
        lostAudioFocusRecorder.verify().opened(CastAudioManager.AudioFocusLoss.NORMAL).end();
        listener.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);
        lostAudioFocusRecorder.verify().closed(CastAudioManager.AudioFocusLoss.NORMAL).end();
    }

    @Test
    public void testAudioFocusNotGainedIfRequestNotActivated() {
        CastAudioManager audioManager =
                CastAudioManager.getAudioManager(RuntimeEnvironment.application);
        ShadowAudioManager shadowAudioManager = Shadows.shadowOf(audioManager.getInternal());
        Controller<CastAudioFocusRequest> requestAudioFocusState = new Controller<>();
        Observable<CastAudioManager.AudioFocusLoss> lostAudioFocusState =
                audioManager.requestAudioFocusWhen(requestAudioFocusState);
        ReactiveRecorder lostAudioFocusRecorder = ReactiveRecorder.record(lostAudioFocusState);
        lostAudioFocusRecorder.verify().opened(CastAudioManager.AudioFocusLoss.NOT_REQUESTED).end();
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N_MR1)
    public void testNoAudioFocusLossIfRequestGrantedImmediately() {
        CastAudioManager audioManager =
                CastAudioManager.getAudioManager(RuntimeEnvironment.application);
        ShadowAudioManager shadowAudioManager = Shadows.shadowOf(audioManager.getInternal());
        Controller<CastAudioFocusRequest> requestAudioFocusState = new Controller<>();
        requestAudioFocusState.set(buildFocusRequest());
        Observable<CastAudioManager.AudioFocusLoss> lostAudioFocusState =
                audioManager.requestAudioFocusWhen(requestAudioFocusState);
        ReactiveRecorder lostAudioFocusRecorder = ReactiveRecorder.record(lostAudioFocusState);
        lostAudioFocusRecorder.verify().end();
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N_MR1)
    public void testTransientAudioFocusLoss() {
        CastAudioManager audioManager =
                CastAudioManager.getAudioManager(RuntimeEnvironment.application);
        ShadowAudioManager shadowAudioManager = Shadows.shadowOf(audioManager.getInternal());
        Controller<CastAudioFocusRequest> requestAudioFocusState = new Controller<>();
        requestAudioFocusState.set(buildFocusRequest());
        Observable<CastAudioManager.AudioFocusLoss> lostAudioFocusState =
                audioManager.requestAudioFocusWhen(requestAudioFocusState);
        ReactiveRecorder lostAudioFocusRecorder = ReactiveRecorder.record(lostAudioFocusState);
        AudioManager.OnAudioFocusChangeListener listener =
                shadowAudioManager.getLastAudioFocusRequest().listener;
        listener.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT);
        lostAudioFocusRecorder.verify().opened(CastAudioManager.AudioFocusLoss.TRANSIENT).end();
        listener.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);
        lostAudioFocusRecorder.verify().closed(CastAudioManager.AudioFocusLoss.TRANSIENT).end();
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.N_MR1)
    public void testTransientCanDuckAudioFocusLoss() {
        CastAudioManager audioManager =
                CastAudioManager.getAudioManager(RuntimeEnvironment.application);
        ShadowAudioManager shadowAudioManager = Shadows.shadowOf(audioManager.getInternal());
        Controller<CastAudioFocusRequest> requestAudioFocusState = new Controller<>();
        requestAudioFocusState.set(buildFocusRequest());
        Observable<CastAudioManager.AudioFocusLoss> lostAudioFocusState =
                audioManager.requestAudioFocusWhen(requestAudioFocusState);
        ReactiveRecorder lostAudioFocusRecorder = ReactiveRecorder.record(lostAudioFocusState);
        AudioManager.OnAudioFocusChangeListener listener =
                shadowAudioManager.getLastAudioFocusRequest().listener;
        listener.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK);
        lostAudioFocusRecorder.verify()
                .opened(CastAudioManager.AudioFocusLoss.TRANSIENT_CAN_DUCK)
                .end();
        listener.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);
        lostAudioFocusRecorder.verify()
                .closed(CastAudioManager.AudioFocusLoss.TRANSIENT_CAN_DUCK)
                .end();
    }

    // Simulate the AudioManager mute behavior on Android L. The isStreamMute() method is present,
    // but can only be used through reflection. Mute requests are cumulative, so a stream only
    // unmutes once a equal number of setStreamMute(t, true) setStreamMute(t, false) requests have
    // been received.
    private static class LollipopAudioManager extends AudioManager {
        // Stores the number of total standing mute requests per stream.
        private final SparseIntArray mMuteState = new SparseIntArray();
        private boolean mCanCallStreamMute = true;

        public void setCanCallStreamMute(boolean able) {
            mCanCallStreamMute = able;
        }

        @Override
        public boolean isStreamMute(int streamType) {
            if (!mCanCallStreamMute) {
                throw new RuntimeException("isStreamMute() disabled for testing");
            }
            return mMuteState.get(streamType, 0) > 0;
        }

        @Override
        public void setStreamMute(int streamType, boolean muteState) {
            int delta = muteState ? 1 : -1;
            int currentMuteCount = mMuteState.get(streamType, 0);
            int newMuteCount = currentMuteCount + delta;
            assert newMuteCount >= 0;
            mMuteState.put(streamType, newMuteCount);
        }
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP)
    public void testReleaseStreamMuteWithNoMute() {
        AudioManager fakeAudioManager = new LollipopAudioManager();
        CastAudioManager audioManager = new CastAudioManager(fakeAudioManager);
        audioManager.releaseStreamMuteIfNecessary(AudioManager.STREAM_MUSIC);
        assertFalse(fakeAudioManager.isStreamMute(AudioManager.STREAM_MUSIC));
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP)
    public void testReleaseStreamMuteWithMute() {
        AudioManager fakeAudioManager = new LollipopAudioManager();
        CastAudioManager audioManager = new CastAudioManager(fakeAudioManager);
        fakeAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        assertTrue(fakeAudioManager.isStreamMute(AudioManager.STREAM_MUSIC));
        audioManager.releaseStreamMuteIfNecessary(AudioManager.STREAM_MUSIC);
        assertFalse(fakeAudioManager.isStreamMute(AudioManager.STREAM_MUSIC));
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP)
    public void testHandleExceptionFromIsStreamMute() {
        LollipopAudioManager fakeAudioManager = new LollipopAudioManager();
        fakeAudioManager.setCanCallStreamMute(false);
        CastAudioManager audioManager = new CastAudioManager(fakeAudioManager);
        // This should not crash even if isStreamMute() throws an exception.
        audioManager.releaseStreamMuteIfNecessary(AudioManager.STREAM_MUSIC);
    }
}
