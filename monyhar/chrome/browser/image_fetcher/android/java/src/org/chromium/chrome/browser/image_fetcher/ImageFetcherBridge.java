// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.image_fetcher;

import android.graphics.Bitmap;

import androidx.annotation.VisibleForTesting;

import org.monyhar.base.Callback;
import org.monyhar.base.ThreadUtils;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.base.annotations.NativeMethods;
import org.monyhar.chrome.browser.profiles.Profile;

import jp.tomorrowkey.android.gifplayer.BaseGifImage;

/**
 * Provides access to native implementations of ImageFetcher for the given profile.
 */
@JNINamespace("image_fetcher")
public class ImageFetcherBridge {
    private Profile mProfile;

    /**
     * Get the ImageFetcherBridge for the given profile.
     *
     * @param profile   The profile for which the ImageFetcherBridge is returned.
     * @return          The ImageFetcherBridge for the given profile.
     */
    public static ImageFetcherBridge getForProfile(Profile profile) {
        ThreadUtils.assertOnUiThread();

        return new ImageFetcherBridge(profile);
    }

    /**
     * Creates a ImageFetcherBridge for the given profile.
     *
     * @param profile The profile to reach regarding image_fetcher_service on native side.
     */
    @VisibleForTesting
    ImageFetcherBridge(Profile profile) {
        mProfile = profile;
    }

    /**
     * Get the full path of the given url on disk.
     *
     * @param url The url to hash.
     * @return The full path to the resource on disk.
     */
    public String getFilePath(String url) {
        return ImageFetcherBridgeJni.get().getFilePath(mProfile, url);
    }

    /**
     * Fetch a gif from native or null if the gif can't be fetched or decoded.
     *
     * @param config The configuration of the image fetcher.
     * @param params The parameters to specify image fetching details.
     * @param callback The callback to call when the gif is ready. The callback will be invoked on
     *      the same thread it was called on.
     */
    public void fetchGif(@ImageFetcherConfig int config, final ImageFetcher.Params params,
            Callback<BaseGifImage> callback) {
        ImageFetcherBridgeJni.get().fetchImageData(mProfile, config, params.url, params.clientName,
                params.expirationIntervalMinutes, (byte[] data) -> {
                    if (data == null || data.length == 0) {
                        callback.onResult(null);
                        return;
                    }

                    callback.onResult(new BaseGifImage(data));
                });
    }

    /**
     * Fetch the image from native, then resize it to the given dimensions.
     *
     * @param config The configuration of the image fetcher.
     * @param params The parameters to specify image fetching details.
     * @param callback The callback to call when the image is ready. The callback will be invoked on
     *      the same thread that it was called on.
     */
    public void fetchImage(@ImageFetcherConfig int config, final ImageFetcher.Params params,
            Callback<Bitmap> callback) {
        ImageFetcherBridgeJni.get().fetchImage(mProfile, config, params.url, params.clientName,
                params.expirationIntervalMinutes, (bitmap) -> {
                    callback.onResult(
                            ImageFetcher.resizeImage(bitmap, params.width, params.height));
                });
    }

    /**
     * Report a metrics event.
     *
     * @param clientName The UMA client name to report the metrics to.
     * @param eventId The event to report.
     */
    public void reportEvent(String clientName, @ImageFetcherEvent int eventId) {
        ImageFetcherBridgeJni.get().reportEvent(clientName, eventId);
    }

    /**
     * Report a timing event for a cache hit.
     *
     * @param clientName The UMA client name to report the metrics to.
     * @param startTimeMillis The start time (in milliseconds) of the request, used to measure the
     *      total duration.
     */
    public void reportCacheHitTime(String clientName, long startTimeMillis) {
        ImageFetcherBridgeJni.get().reportCacheHitTime(clientName, startTimeMillis);
    }

    /**
     * Report a timing event for a call to native
     *
     * @param clientName The UMA client name to report the metrics to.
     * @param startTimeMillis The start time (in milliseconds) of the request, used to measure the
     *      total duration.
     */
    public void reportTotalFetchTimeFromNative(String clientName, long startTimeMillis) {
        ImageFetcherBridgeJni.get().reportTotalFetchTimeFromNative(clientName, startTimeMillis);
    }

    @NativeMethods
    interface Natives {
        // Native methods
        String getFilePath(Profile profile, String url);
        void fetchImageData(Profile profile, @ImageFetcherConfig int config, String url,
                String clientName, int expirationIntervalMinutes, Callback<byte[]> callback);
        void fetchImage(Profile profile, @ImageFetcherConfig int config, String url,
                String clientName, int expirationIntervalMinutes, Callback<Bitmap> callback);
        void reportEvent(String clientName, int eventId);
        void reportCacheHitTime(String clientName, long startTimeMillis);
        void reportTotalFetchTimeFromNative(String clientName, long startTimeMillis);
    }
}
