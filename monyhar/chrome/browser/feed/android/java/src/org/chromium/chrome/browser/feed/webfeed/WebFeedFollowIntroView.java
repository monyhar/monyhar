// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.feed.webfeed;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;

import org.monyhar.chrome.browser.preferences.Pref;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.ui.appmenu.AppMenuHandler;
import org.monyhar.chrome.browser.util.ChromeAccessibilityUtil;
import org.monyhar.components.browser_ui.widget.highlight.ViewHighlighter;
import org.monyhar.components.browser_ui.widget.textbubble.ClickableTextBubble;
import org.monyhar.components.browser_ui.widget.textbubble.TextBubble;
import org.monyhar.components.feature_engagement.FeatureConstants;
import org.monyhar.components.feature_engagement.Tracker;
import org.monyhar.components.prefs.PrefService;
import org.monyhar.components.user_prefs.UserPrefs;
import org.monyhar.ui.widget.LoadingView;
import org.monyhar.ui.widget.ViewRectProvider;

/**
 * Manages the view of the WebFeed follow intro.
 *
 * This is the chip that shows up under the 3-dot menu informing users that this is a page
 * they can follow.
 */
class WebFeedFollowIntroView {
    private static final int sAcceleratorTimeout = 10 * 1000; // 10 seconds

    private final Activity mActivity;
    private final AppMenuHandler mAppMenuHandler;
    private final Handler mHandler = new Handler();
    private final PrefService mPrefService = UserPrefs.get(Profile.getLastUsedRegularProfile());
    private final View mMenuButtonAnchorView;

    private ClickableTextBubble mFollowBubble;

    /**
     * Constructs an instance of {@link WebFeedFollowIntroView}.
     *
     * @param activity The current {@link Activity}.
     * @param appMenuHandler The {@link AppMenuHandler} to highlight the Web Feed menu item.
     * @param menuButtonAnchorView The menu button {@link View} to serve as an anchor.
     */
    WebFeedFollowIntroView(
            Activity activity, AppMenuHandler appMenuHandler, View menuButtonAnchorView) {
        mActivity = activity;
        mAppMenuHandler = appMenuHandler;
        mMenuButtonAnchorView = menuButtonAnchorView;
    }

    void showAccelerator(View.OnTouchListener onTouchListener, Tracker featureEngagementTracker) {
        mFollowBubble = new ClickableTextBubble(mActivity, mMenuButtonAnchorView,
                R.string.menu_follow, R.string.menu_follow, createRectProvider(), R.drawable.ic_add,
                ChromeAccessibilityUtil.get().isAccessibilityEnabled(), onTouchListener);
        mFollowBubble.addOnDismissListener(() -> {
            mHandler.postDelayed(this::turnOffHighlightForFollowMenuItem,
                    ViewHighlighter.IPH_MIN_DELAY_BETWEEN_TWO_HIGHLIGHTS);
            if (!mPrefService.getBoolean(Pref.ENABLE_WEB_FEED_FOLLOW_INTRO_DEBUG)) {
                featureEngagementTracker.dismissed(FeatureConstants.IPH_WEB_FEED_FOLLOW_FEATURE);
            }
        });
        // TODO(crbug/1152592): Figure out a way to dismiss on outside taps as well.
        mFollowBubble.setAutoDismissTimeout(sAcceleratorTimeout);
        turnOnHighlightForFollowMenuItem();

        mFollowBubble.show();
    }

    void showLoadingUI() {
        if (mFollowBubble != null) {
            mFollowBubble.showLoadingUI(R.string.web_feed_follow_loading_description);
        }
    }

    void hideLoadingUI(LoadingView.Observer loadingViewObserver) {
        if (mFollowBubble != null) {
            mFollowBubble.hideLoadingUI(loadingViewObserver);
        }
    }

    void dismissBubble() {
        if (mFollowBubble != null) {
            mFollowBubble.dismiss();
            mFollowBubble.destroy();
            mFollowBubble = null;
        }
    }

    void showFollowingBubble() {
        TextBubble followingBubble = new TextBubble(mActivity, mMenuButtonAnchorView,
                R.string.menu_following, R.string.menu_following, /*showArrow=*/false,
                createRectProvider(), R.drawable.ic_done_blue, /*isRoundBubble=*/true,
                /*inverseColor=*/false, ChromeAccessibilityUtil.get().isAccessibilityEnabled());
        followingBubble.setDismissOnTouchInteraction(true);
        followingBubble.show();
    }

    private ViewRectProvider createRectProvider() {
        ViewRectProvider rectProvider = new ViewRectProvider(mMenuButtonAnchorView);
        int yInsetPx =
                mActivity.getResources().getDimensionPixelOffset(R.dimen.web_feed_intro_y_inset);
        Rect insetRect = new Rect(0, 0, 0, yInsetPx);
        rectProvider.setInsetPx(insetRect);

        return rectProvider;
    }

    private void turnOnHighlightForFollowMenuItem() {
        mAppMenuHandler.setMenuHighlight(R.id.follow_chip_view);
    }

    private void turnOffHighlightForFollowMenuItem() {
        mAppMenuHandler.clearMenuHighlight();
    }
}
