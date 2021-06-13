// Copyright 2013 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.content.browser;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import org.monyhar.base.ContextUtils;
import org.monyhar.base.TraceEvent;
import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.base.task.AsyncTask;
import org.monyhar.base.task.PostTask;
import org.monyhar.content_public.browser.UiThreadTaskTraits;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class is the Java counterpart to the C++ TtsPlatformImplAndroid class.
 * It implements the Android-native text-to-speech code to support the web
 * speech synthesis API.
 *
 * Threading model note: all calls from C++ must happen on the UI thread.
 * Callbacks from Android may happen on a different thread, so we always
 * use PostTask.runOrPostTask(UiThreadTaskTraits.DEFAULT, ...)  when calling back to C++.
 */
@JNINamespace("content")
class TtsPlatformImpl {
    private static class TtsVoice {
        private TtsVoice(String name, String language) {
            mName = name;
            mLanguage = language;
        }
        private final String mName;
        private final String mLanguage;
    }

    private static class PendingUtterance {
        private PendingUtterance(TtsPlatformImpl impl, int utteranceId, String text, String lang,
                float rate, float pitch, float volume) {
            mImpl = impl;
            mUtteranceId = utteranceId;
            mText = text;
            mLang = lang;
            mRate = rate;
            mPitch = pitch;
            mVolume = volume;
        }

        private void speak() {
            mImpl.speak(mUtteranceId, mText, mLang, mRate, mPitch, mVolume);
        }

        TtsPlatformImpl mImpl;
        int mUtteranceId;
        String mText;
        String mLang;
        float mRate;
        float mPitch;
        float mVolume;
    }

    private long mNativeTtsPlatformImplAndroid;
    private final TextToSpeech mTextToSpeech;
    private boolean mInitialized;
    private List<TtsVoice> mVoices;
    private String mCurrentLanguage;
    private PendingUtterance mPendingUtterance;

    private TtsPlatformImpl(long nativeTtsPlatformImplAndroid) {
        mInitialized = false;
        mNativeTtsPlatformImplAndroid = nativeTtsPlatformImplAndroid;
        mTextToSpeech = new TextToSpeech(ContextUtils.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                PostTask.runOrPostTask(UiThreadTaskTraits.DEFAULT, () -> initialize());
            }
        });
        addOnUtteranceProgressListener();
    }

    /**
     * Create a TtsPlatformImpl object, which is owned by TtsPlatformImplAndroid
     * on the C++ side.
     *  @param nativeTtsPlatformImplAndroid The C++ object that owns us.
     *
     */
    @CalledByNative
    private static TtsPlatformImpl create(long nativeTtsPlatformImplAndroid) {
        return new TtsPlatformImpl(nativeTtsPlatformImplAndroid);
    }

    /**
     * Called when our C++ counterpoint is deleted. Clear the handle to our
     * native C++ object, ensuring it's never called.
     */
    @CalledByNative
    private void destroy() {
        mNativeTtsPlatformImplAndroid = 0;
    }

    /**
     * @return true if our TextToSpeech object is initialized and we've
     * finished scanning the list of voices.
     */
    @CalledByNative
    private boolean isInitialized() {
        return mInitialized;
    }

    /**
     * @return the number of voices.
     */
    @CalledByNative
    private int getVoiceCount() {
        assert mInitialized;
        return mVoices.size();
    }

    /**
     * @return the name of the voice at a given index.
     */
    @CalledByNative
    private String getVoiceName(int voiceIndex) {
        assert mInitialized;
        return mVoices.get(voiceIndex).mName;
    }

    /**
     * @return the language of the voice at a given index.
     */
    @CalledByNative
    private String getVoiceLanguage(int voiceIndex) {
        assert mInitialized;
        return mVoices.get(voiceIndex).mLanguage;
    }

    /**
     * Attempt to start speaking an utterance. If it returns true, will call back on
     * start and end.
     *
     * @param utteranceId A unique id for this utterance so that callbacks can be tied
     *     to a particular utterance.
     * @param text The text to speak.
     * @param lang The language code for the text (e.g., "en-US").
     * @param rate The speech rate, in the units expected by Android TextToSpeech.
     * @param pitch The speech pitch, in the units expected by Android TextToSpeech.
     * @param volume The speech volume, in the units expected by Android TextToSpeech.
     * @return true on success.
     */
    @CalledByNative
    private boolean speak(
            int utteranceId, String text, String lang, float rate, float pitch, float volume) {
        if (!mInitialized) {
            mPendingUtterance =
                    new PendingUtterance(this, utteranceId, text, lang, rate, pitch, volume);
            return true;
        }
        if (mPendingUtterance != null) mPendingUtterance = null;

        if (!lang.equals(mCurrentLanguage)) {
            mTextToSpeech.setLanguage(new Locale(lang));
            mCurrentLanguage = lang;
        }

        mTextToSpeech.setSpeechRate(rate);
        mTextToSpeech.setPitch(pitch);

        int result = callSpeak(text, volume, utteranceId);
        return (result == TextToSpeech.SUCCESS);
    }

    /**
     * Stop the current utterance.
     */
    @CalledByNative
    private void stop() {
        if (mInitialized) mTextToSpeech.stop();
        if (mPendingUtterance != null) mPendingUtterance = null;
    }

    /**
     * Post a task to the UI thread to send the TTS "end" event.
     */
    private void sendEndEventOnUiThread(final String utteranceId) {
        PostTask.runOrPostTask(UiThreadTaskTraits.DEFAULT, () -> {
            if (mNativeTtsPlatformImplAndroid != 0) {
                TtsPlatformImplJni.get().onEndEvent(
                        mNativeTtsPlatformImplAndroid, Integer.parseInt(utteranceId));
            }
        });
    }

    /**
     * Post a task to the UI thread to send the TTS "error" event.
     */
    private void sendErrorEventOnUiThread(final String utteranceId) {
        PostTask.runOrPostTask(UiThreadTaskTraits.DEFAULT, () -> {
            if (mNativeTtsPlatformImplAndroid != 0) {
                TtsPlatformImplJni.get().onErrorEvent(
                        mNativeTtsPlatformImplAndroid, Integer.parseInt(utteranceId));
            }
        });
    }

    /**
     * Post a task to the UI thread to send the TTS "start" event.
     */
    private void sendStartEventOnUiThread(final String utteranceId) {
        PostTask.runOrPostTask(UiThreadTaskTraits.DEFAULT, () -> {
            if (mNativeTtsPlatformImplAndroid != 0) {
                TtsPlatformImplJni.get().onStartEvent(
                        mNativeTtsPlatformImplAndroid, Integer.parseInt(utteranceId));
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void addOnUtteranceProgressListener() {
        mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onDone(final String utteranceId) {
                sendEndEventOnUiThread(utteranceId);
            }

            @Override
            public void onError(final String utteranceId, int errorCode) {
                sendErrorEventOnUiThread(utteranceId);
            }

            @Override
            @Deprecated
            public void onError(final String utteranceId) {}

            @Override
            public void onStart(final String utteranceId) {
                sendStartEventOnUiThread(utteranceId);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private int callSpeak(String text, float volume, int utteranceId) {
        Bundle params = new Bundle();
        if (volume != 1.0) {
            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume);
        }
        return mTextToSpeech.speak(
                text, TextToSpeech.QUEUE_FLUSH, params, Integer.toString(utteranceId));
    }

    /**
     * Note: we enforce that this method is called on the UI thread, so
     * we can call TtsPlatformImplJni.get().voicesChanged directly.
     */
    private void initialize() {
        TraceEvent.startAsync("TtsPlatformImpl:initialize", hashCode());

        new AsyncTask<List<TtsVoice>>() {
            @Override
            protected List<TtsVoice> doInBackground() {
                assert mNativeTtsPlatformImplAndroid != 0;

                try (TraceEvent te = TraceEvent.scoped("TtsPlatformImpl:initialize.async_task")) {
                    Locale[] locales = Locale.getAvailableLocales();
                    final List<TtsVoice> voices = new ArrayList<>();
                    for (Locale locale : locales) {
                        if (!locale.getVariant().isEmpty()) continue;
                        try {
                            if (mTextToSpeech.isLanguageAvailable(locale) > 0) {
                                String name = locale.getDisplayLanguage();
                                if (!locale.getCountry().isEmpty()) {
                                    name += " " + locale.getDisplayCountry();
                                }
                                TtsVoice voice = new TtsVoice(name, locale.toString());
                                voices.add(voice);
                            }
                        } catch (Exception e) {
                            // Just skip the locale if it's invalid.
                            //
                            // We used to catch only java.util.MissingResourceException,
                            // but we need to catch more exceptions to work around a bug
                            // in Google TTS when we query "bn".
                            // http://crbug.com/792856
                        }
                    }
                    return voices;
                }
            }

            @Override
            protected void onPostExecute(List<TtsVoice> voices) {
                mVoices = voices;
                mInitialized = true;

                TtsPlatformImplJni.get().voicesChanged(mNativeTtsPlatformImplAndroid);

                if (mPendingUtterance != null) mPendingUtterance.speak();

                TraceEvent.finishAsync(
                        "TtsPlatformImpl:initialize", TtsPlatformImpl.this.hashCode());
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @NativeMethods
    interface Natives {
        void voicesChanged(long nativeTtsPlatformImplAndroid);
        void onEndEvent(long nativeTtsPlatformImplAndroid, int utteranceId);
        void onStartEvent(long nativeTtsPlatformImplAndroid, int utteranceId);
        void onErrorEvent(long nativeTtsPlatformImplAndroid, int utteranceId);
    }
}
