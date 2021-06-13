// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.app.video_tutorials;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.WindowManager;

import org.monyhar.base.IntentUtils;
import org.monyhar.chrome.browser.ChromeTabbedActivity;
import org.monyhar.chrome.browser.SynchronousInitializationActivity;
import org.monyhar.chrome.browser.WebContentsFactory;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.video_tutorials.FeatureType;
import org.monyhar.chrome.browser.video_tutorials.Tutorial;
import org.monyhar.chrome.browser.video_tutorials.VideoTutorialService;
import org.monyhar.chrome.browser.video_tutorials.VideoTutorialServiceFactory;
import org.monyhar.chrome.browser.video_tutorials.player.VideoPlayerCoordinator;
import org.monyhar.components.embedder_support.util.UrlConstants;
import org.monyhar.components.embedder_support.view.ContentView;
import org.monyhar.components.version_info.VersionConstants;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.ui.base.ActivityWindowAndroid;
import org.monyhar.ui.base.ViewAndroidDelegate;
import org.monyhar.ui.base.WindowAndroid;

/**
 * Java interface for interacting with the native video tutorial service. Responsible for
 * initializing and fetching data fo be shown on the UI.
 */
public class VideoPlayerActivity extends SynchronousInitializationActivity {
    public static final String EXTRA_VIDEO_TUTORIAL = "extra_video_tutorial";

    private WindowAndroid mWindowAndroid;
    private VideoPlayerCoordinator mCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        VideoTutorialService videoTutorialService =
                VideoTutorialServiceFactory.getForProfile(Profile.getLastUsedRegularProfile());
        mWindowAndroid = new ActivityWindowAndroid(this, /* listenToActivityState= */ true);
        mCoordinator = VideoTutorialServiceFactory.createVideoPlayerCoordinator(this,
                videoTutorialService, this::createWebContents, new ChromeLanguageInfoProvider(),
                this::tryNow, this::finish);
        setContentView(mCoordinator.getView());

        int featureType =
                IntentUtils.safeGetIntExtra(getIntent(), EXTRA_VIDEO_TUTORIAL, FeatureType.INVALID);
        videoTutorialService.getTutorial(featureType, mCoordinator::playVideoTutorial);
    }

    private Pair<WebContents, ContentView> createWebContents() {
        Profile profile = Profile.getLastUsedRegularProfile();
        WebContents webContents = WebContentsFactory.createWebContents(profile, false);
        ContentView contentView =
                ContentView.createContentView(this, null /* eventOffsetHandler */, webContents);
        webContents.initialize(VersionConstants.PRODUCT_VERSION,
                ViewAndroidDelegate.createBasicDelegate(contentView), contentView, mWindowAndroid,
                WebContents.createDefaultInternalsHolder());
        return Pair.create(webContents, contentView);
    }

    @Override
    public void onBackPressed() {
        if (mCoordinator.onBackPressed()) return;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mCoordinator.destroy();
        super.onDestroy();
    }

    /** Called to launch this activity to play a given video tutorial. */
    public static void playVideoTutorial(Context context, @FeatureType int featureType) {
        Intent intent = new Intent();
        intent.setClass(context, VideoPlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_TUTORIAL, featureType);
        context.startActivity(intent);
    }

    private void tryNow(Tutorial tutorial) {
        VideoTutorialServiceFactory.getTryNowTracker().recordTryNowButtonClicked(
                tutorial.featureType);
        Intent intent = new Intent();
        intent.setData(Uri.parse(UrlConstants.NTP_URL));
        intent.setClass(this, ChromeTabbedActivity.class);
        startActivity(intent);
    }
}
