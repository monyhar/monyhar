// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.webapps;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import org.monyhar.base.metrics.RecordUserAction;
import org.monyhar.base.supplier.Supplier;
import org.monyhar.chrome.browser.banners.AppMenuVerbiage;
import org.monyhar.chrome.browser.feature_engagement.TrackerFactory;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.browser.ui.appmenu.AppMenuHandler;
import org.monyhar.chrome.browser.user_education.IPHCommandBuilder;
import org.monyhar.chrome.browser.user_education.UserEducationHelper;
import org.monyhar.components.embedder_support.util.UrlConstants;
import org.monyhar.components.feature_engagement.EventConstants;
import org.monyhar.components.feature_engagement.FeatureConstants;
import org.monyhar.components.feature_engagement.Tracker;
import org.monyhar.components.messages.MessageBannerProperties;
import org.monyhar.components.messages.MessageDispatcher;
import org.monyhar.components.messages.MessageIdentifier;
import org.monyhar.components.messages.MessageScopeType;
import org.monyhar.components.webapk.lib.client.WebApkValidator;
import org.monyhar.components.webapps.AddToHomescreenCoordinator;
import org.monyhar.components.webapps.AppBannerManager;
import org.monyhar.components.webapps.WebappsUtils;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.ui.base.WindowAndroid;
import org.monyhar.ui.modaldialog.ModalDialogManager;
import org.monyhar.ui.modelutil.PropertyModel;
import org.monyhar.url.GURL;

/**
 * This class is responsible for rendering the 'Add to home screen' IPH, such as IPH bubble/message
 * creation, metrics logging etc.
 */
public class AddToHomescreenIPHController {
    private static final String VARIATION_KEY_USE_TEXT_BUBBLE = "use_text_bubble";
    private static final String VARIATION_KEY_USE_MESSAGE = "use_message";

    private final Activity mActivity;
    private final WindowAndroid mWindowAndroid;
    private final ModalDialogManager mModalDialogManager;
    private final AppMenuHandler mAppMenuHandler;
    private final @IdRes int mHighlightMenuItemId;
    private final Supplier<View> mMenuButtonView;
    private final MessageDispatcher mMessageDispatcher;
    private final UserEducationHelper mUserEducationHelper;
    private final Tracker mTracker;
    private final Handler mHandler = new Handler();

    private GURL mLastClickedMostVisitedTileUrl;

    /**
     * Creates an {@link AddToHomescreenIPHController}.
     * @param activity The associated activity.
     * @param windowAndroid The associated {@link WindowAndroid}.
     * @param modalDialogManager The {@link ModalDialogManager} for showing the dialog.
     * @param appMenuHandler The {@link AppMenuHandler}.
     * @param highlightMenuItemId The resource id of 'Add to Home screen' in the app menu.
     * @param menuButtonView The view representing the menu button.
     * @param messageDispatcher The {@link MessageDispatcher} for displaying messages.
     */
    public AddToHomescreenIPHController(Activity activity, WindowAndroid windowAndroid,
            ModalDialogManager modalDialogManager, AppMenuHandler appMenuHandler,
            @IdRes int highlightMenuItemId, Supplier<View> menuButtonView,
            MessageDispatcher messageDispatcher) {
        mActivity = activity;
        mWindowAndroid = windowAndroid;
        mModalDialogManager = modalDialogManager;
        mAppMenuHandler = appMenuHandler;
        mHighlightMenuItemId = highlightMenuItemId;
        mMenuButtonView = menuButtonView;
        mMessageDispatcher = messageDispatcher;
        mTracker = TrackerFactory.getTrackerForProfile(Profile.getLastUsedRegularProfile());
        mUserEducationHelper = new UserEducationHelper(mActivity, mHandler);
    }

    /**
     * Called to show in-product-help message.
     * @param tab The current tab.
     */
    public void showAddToHomescreenIPH(Tab tab) {
        if (!canShowAddToHomescreenMenuItem(mActivity, tab)) return;

        if (ChromeFeatureList.getFieldTrialParamByFeatureAsBoolean(
                    ChromeFeatureList.ADD_TO_HOMESCREEN_IPH, VARIATION_KEY_USE_TEXT_BUBBLE,
                    false)) {
            showTextBubbleIPH();
        } else if (ChromeFeatureList.getFieldTrialParamByFeatureAsBoolean(
                           ChromeFeatureList.ADD_TO_HOMESCREEN_IPH, VARIATION_KEY_USE_MESSAGE,
                           false)) {
            showMessageIPH(tab);
        }
    }

    private static boolean canShowAddToHomescreenMenuItem(Context context, Tab tab) {
        if (tab.isIncognito()) return false;

        GURL url = tab.getUrl();
        if (url.isEmpty() || !url.isValid()) return false;

        String urlString = url.getSpec();
        boolean isChromeScheme = urlString.startsWith(UrlConstants.CHROME_URL_PREFIX)
                || urlString.startsWith(UrlConstants.CHROME_NATIVE_URL_PREFIX);
        boolean isFileScheme = urlString.startsWith(UrlConstants.FILE_URL_PREFIX);
        boolean isContentScheme = urlString.startsWith(UrlConstants.CONTENT_URL_PREFIX);
        boolean urlSchemeSupported = !isChromeScheme && !isFileScheme && !isContentScheme;

        if (!urlSchemeSupported) return false;
        if (!WebappsUtils.isAddToHomeIntentSupported()) return false;

        // If it is a web apk, don't show the IPH.
        ResolveInfo resolveInfo = WebApkValidator.queryFirstWebApkResolveInfo(context, urlString);
        boolean isInstalledAsPwa =
                resolveInfo != null && resolveInfo.activityInfo.packageName != null;
        if (isInstalledAsPwa) return false;

        // If it can be installed as a PWA, don't show the IPH.
        WebContents webContents = tab.getWebContents();
        AppBannerManager manager =
                webContents != null ? AppBannerManager.forWebContents(webContents) : null;
        boolean canInstallAsPwa = manager != null && manager.getIsPwa(webContents);
        if (canInstallAsPwa) return false;

        return true;
    }

    private void showTextBubbleIPH() {
        mUserEducationHelper.requestShowIPH(
                new IPHCommandBuilder(mActivity.getResources(),
                        FeatureConstants.ADD_TO_HOMESCREEN_TEXT_BUBBLE_FEATURE,
                        R.string.iph_bubble_add_to_home_screen,
                        R.string.iph_bubble_add_to_home_screen_accessibility)
                        .setAnchorView(mMenuButtonView.get())
                        .setOnShowCallback(this::turnOnTextBubbleHighlightForMenuItem)
                        .setOnDismissCallback(this::turnOffTextBubbleHighlightForMenuItem)
                        .build());
    }

    private void showMessageIPH(Tab tab) {
        if (!ChromeFeatureList.isEnabled(ChromeFeatureList.MESSAGES_FOR_ANDROID_INFRASTRUCTURE)) {
            return;
        }

        if (!mTracker.shouldTriggerHelpUI(FeatureConstants.ADD_TO_HOMESCREEN_MESSAGE_FEATURE)) {
            return;
        }

        PropertyModel model =
                new PropertyModel.Builder(MessageBannerProperties.ALL_KEYS)
                        .with(MessageBannerProperties.MESSAGE_IDENTIFIER,
                                MessageIdentifier.ADD_TO_HOMESCREEN_IPH)
                        .with(MessageBannerProperties.ICON,
                                VectorDrawableCompat.create(mActivity.getResources(),
                                        R.drawable.ic_apps_blue_24dp, mActivity.getTheme()))
                        .with(MessageBannerProperties.TITLE,
                                mActivity.getResources().getString(
                                        R.string.iph_message_add_to_home_screen_title))
                        .with(MessageBannerProperties.DESCRIPTION,
                                mActivity.getResources().getString(
                                        R.string.iph_message_add_to_home_screen_description))
                        .with(MessageBannerProperties.PRIMARY_BUTTON_TEXT,
                                mActivity.getResources().getString(
                                        R.string.iph_message_add_to_home_screen_action))
                        .with(MessageBannerProperties.ON_DISMISSED, this::onMessageDismissed)
                        .with(MessageBannerProperties.ON_PRIMARY_ACTION,
                                () -> onMessageAddButtonClicked(tab))
                        .build();
        mMessageDispatcher.enqueueMessage(
                model, tab.getWebContents(), MessageScopeType.NAVIGATION, false);
        RecordUserAction.record("Android.AddToHomescreenIPH.Message.Shown");
    }

    private void onMessageAddButtonClicked(Tab tab) {
        if (tab.isDestroyed()) return;

        Bundle menuItemData = new Bundle();
        // Used for UMA.
        menuItemData.putInt(
                AppBannerManager.MENU_TITLE_KEY, AppMenuVerbiage.APP_MENU_OPTION_ADD_TO_HOMESCREEN);
        AddToHomescreenCoordinator.showForAppMenu(
                mActivity, mWindowAndroid, mModalDialogManager, tab.getWebContents(), menuItemData);
        mTracker.notifyEvent(EventConstants.ADD_TO_HOMESCREEN_DIALOG_SHOWN);
        RecordUserAction.record("Android.AddToHomescreenIPH.Message.Clicked");
    }

    private void onMessageDismissed(Integer dismissReason) {
        // TODO(shaktisahu): Record metrics for explicit dismiss vs timeout.
        mTracker.dismissed(FeatureConstants.ADD_TO_HOMESCREEN_MESSAGE_FEATURE);
    }

    private void turnOnTextBubbleHighlightForMenuItem() {
        mAppMenuHandler.setMenuHighlight(mHighlightMenuItemId);
    }

    private void turnOffTextBubbleHighlightForMenuItem() {
        mAppMenuHandler.clearMenuHighlight();
    }
}
