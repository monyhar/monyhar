// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.tab;

import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.provider.Browser;
import android.provider.ContactsContract;

import androidx.browser.customtabs.CustomTabsIntent;

import org.monyhar.base.ContextUtils;
import org.monyhar.base.IntentUtils;
import org.monyhar.base.PackageManagerUtils;
import org.monyhar.base.metrics.RecordUserAction;
import org.monyhar.base.supplier.Supplier;
import org.monyhar.blink.mojom.TextFragmentReceiver;
import org.monyhar.chrome.browser.DefaultBrowserInfo;
import org.monyhar.chrome.browser.IntentHandler;
import org.monyhar.chrome.browser.bookmarks.BookmarkModel;
import org.monyhar.chrome.browser.bookmarks.BookmarkUtils;
import org.monyhar.chrome.browser.compositor.bottombar.ephemeraltab.EphemeralTabCoordinator;
import org.monyhar.chrome.browser.contextmenu.ContextMenuItemDelegate;
import org.monyhar.chrome.browser.document.ChromeLauncherActivity;
import org.monyhar.chrome.browser.download.ChromeDownloadDelegate;
import org.monyhar.chrome.browser.feature_engagement.TrackerFactory;
import org.monyhar.chrome.browser.incognito.IncognitoUtils;
import org.monyhar.chrome.browser.multiwindow.MultiWindowUtils;
import org.monyhar.chrome.browser.offlinepages.OfflinePageBridge;
import org.monyhar.chrome.browser.offlinepages.RequestCoordinatorBridge;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.tab.state.CriticalPersistedTabData;
import org.monyhar.chrome.browser.tabmodel.TabModelSelector;
import org.monyhar.chrome.browser.tabmodel.document.TabDelegate;
import org.monyhar.chrome.browser.ui.messages.snackbar.SnackbarManager;
import org.monyhar.components.embedder_support.util.UrlUtilities;
import org.monyhar.components.feature_engagement.EventConstants;
import org.monyhar.content_public.browser.LoadUrlParams;
import org.monyhar.content_public.browser.WebContents;
import org.monyhar.content_public.common.Referrer;
import org.monyhar.ui.base.Clipboard;
import org.monyhar.ui.base.PageTransition;
import org.monyhar.url.GURL;

/**
 * A default {@link ContextMenuItemDelegate} that supports the context menu functionality in Tab.
 */
public class TabContextMenuItemDelegate implements ContextMenuItemDelegate {
    private final TabImpl mTab;
    private final TabModelSelector mTabModelSelector;
    private boolean mLoadOriginalImageRequestedForPageLoad;
    private EmptyTabObserver mDataReductionProxyContextMenuTabObserver;
    private final Supplier<EphemeralTabCoordinator> mEphemeralTabCoordinatorSupplier;
    private final Runnable mContextMenuCopyLinkObserver;
    private final Supplier<SnackbarManager> mSnackbarManagerSupplier;

    /**
     * Builds a {@link TabContextMenuItemDelegate} instance.
     */
    public TabContextMenuItemDelegate(Tab tab, TabModelSelector tabModelSelector,
            Supplier<EphemeralTabCoordinator> ephemeralTabCoordinatorSupplier,
            Runnable contextMenuCopyLinkObserver,
            Supplier<SnackbarManager> snackbarManagerSupplier) {
        mTab = (TabImpl) tab;
        mTabModelSelector = tabModelSelector;
        mEphemeralTabCoordinatorSupplier = ephemeralTabCoordinatorSupplier;
        mContextMenuCopyLinkObserver = contextMenuCopyLinkObserver;
        mSnackbarManagerSupplier = snackbarManagerSupplier;

        mDataReductionProxyContextMenuTabObserver = new EmptyTabObserver() {
            @Override
            public void onPageLoadStarted(Tab tab, GURL url) {
                mLoadOriginalImageRequestedForPageLoad = false;
            }
        };
        mTab.addObserver(mDataReductionProxyContextMenuTabObserver);
    }

    @Override
    public void onDestroy() {
        mTab.removeObserver(mDataReductionProxyContextMenuTabObserver);
    }

    @Override
    public String getPageTitle() {
        return mTab.getTitle();
    }

    @Override
    public WebContents getWebContents() {
        return mTab.getWebContents();
    }

    @Override
    public boolean isIncognito() {
        return mTab.isIncognito();
    }

    @Override
    public boolean isIncognitoSupported() {
        return IncognitoUtils.isIncognitoModeEnabled();
    }

    @Override
    public boolean isOpenInOtherWindowSupported() {
        return MultiWindowUtils.getInstance().isOpenInOtherWindowSupported(
                TabUtils.getActivity(mTab));
    }

    @Override
    public boolean canEnterMultiWindowMode() {
        return MultiWindowUtils.getInstance().canEnterMultiWindowMode(TabUtils.getActivity(mTab));
    }

    @Override
    public boolean startDownload(GURL url, boolean isLink) {
        return !isLink
                || !ChromeDownloadDelegate.from(mTab).shouldInterceptContextMenuDownload(url);
    }

    @Override
    public void onSaveToClipboard(String text, int clipboardType) {
        Clipboard.getInstance().setText(text);
        if (clipboardType == ClipboardType.LINK_URL) {
            // TODO(crbug/1150090): Find a better way of passing event for IPH.
            mContextMenuCopyLinkObserver.run();
        }
    }

    @Override
    public void onSaveImageToClipboard(Uri uri) {
        Clipboard.getInstance().setImageUri(uri);
    }

    @Override
    public boolean supportsCall() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("tel:"));
        return mTab.getWindowAndroid().canResolveActivity(intent);
    }

    @Override
    public void onCall(GURL url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url.getSpec()));
        IntentUtils.safeStartActivity(mTab.getContext(), intent);
    }

    @Override
    public boolean supportsSendEmailMessage() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("mailto:test@example.com"));
        return mTab.getWindowAndroid().canResolveActivity(intent);
    }

    @Override
    public void onSendEmailMessage(GURL url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url.getSpec()));
        IntentUtils.safeStartActivity(mTab.getContext(), intent);
    }

    @Override
    public boolean supportsSendTextMessage() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:"));
        return mTab.getWindowAndroid().canResolveActivity(intent);
    }

    @Override
    public void onSendTextMessage(GURL url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:" + UrlUtilities.getTelNumber(url)));
        IntentUtils.safeStartActivity(mTab.getContext(), intent);
    }

    @Override
    public boolean supportsAddToContacts() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        return mTab.getWindowAndroid().canResolveActivity(intent);
    }

    @Override
    public void onAddToContacts(GURL url) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        if (MailTo.isMailTo(url.getSpec())) {
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL,
                    MailTo.parse(url.getSpec()).getTo().split(",")[0]);
        } else if (UrlUtilities.isTelScheme(url)) {
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, UrlUtilities.getTelNumber(url));
        }
        IntentUtils.safeStartActivity(mTab.getContext(), intent);
    }

    @Override
    public void onOpenInOtherWindow(GURL url, Referrer referrer) {
        TabDelegate tabDelegate = new TabDelegate(mTab.isIncognito());
        LoadUrlParams loadUrlParams = new LoadUrlParams(url.getSpec());
        loadUrlParams.setReferrer(referrer);
        tabDelegate.createTabInOtherWindow(loadUrlParams, TabUtils.getActivity(mTab),
                CriticalPersistedTabData.from(mTab).getParentId());
    }

    @Override
    public void onOpenInNewTab(GURL url, Referrer referrer) {
        RecordUserAction.record("MobileNewTabOpened");
        RecordUserAction.record("LinkOpenedInNewTab");
        LoadUrlParams loadUrlParams = new LoadUrlParams(url.getSpec());
        loadUrlParams.setReferrer(referrer);
        mTabModelSelector.openNewTab(
                loadUrlParams, TabLaunchType.FROM_LONGPRESS_BACKGROUND, mTab, isIncognito());
    }

    @Override
    public void onOpenInNewTabInGroup(GURL url, Referrer referrer) {
        RecordUserAction.record("MobileNewTabOpened");
        RecordUserAction.record("LinkOpenedInNewTab");
        LoadUrlParams loadUrlParams = new LoadUrlParams(url.getSpec());
        loadUrlParams.setReferrer(referrer);
        mTabModelSelector.openNewTab(loadUrlParams,
                TabLaunchType.FROM_LONGPRESS_BACKGROUND_IN_GROUP, mTab, isIncognito());
    }

    @Override
    public void onLoadOriginalImage() {
        mLoadOriginalImageRequestedForPageLoad = true;
        mTab.loadOriginalImage();
    }

    @Override
    public boolean wasLoadOriginalImageRequestedForPageLoad() {
        return mLoadOriginalImageRequestedForPageLoad;
    }

    @Override
    public void onOpenInNewIncognitoTab(GURL url) {
        RecordUserAction.record("MobileNewTabOpened");
        mTabModelSelector.openNewTab(new LoadUrlParams(url.getSpec()),
                TabLaunchType.FROM_LONGPRESS_FOREGROUND, mTab, true);
    }

    @Override
    public GURL getPageUrl() {
        return mTab.getUrl();
    }

    @Override
    public void onOpenImageUrl(GURL url, Referrer referrer) {
        LoadUrlParams loadUrlParams = new LoadUrlParams(url.getSpec());
        loadUrlParams.setTransitionType(PageTransition.LINK);
        loadUrlParams.setReferrer(referrer);
        mTab.loadUrl(loadUrlParams);
    }

    @Override
    public void onOpenImageInNewTab(GURL url, Referrer referrer) {
        LoadUrlParams loadUrlParams = new LoadUrlParams(url.getSpec());
        loadUrlParams.setReferrer(referrer);
        mTabModelSelector.openNewTab(
                loadUrlParams, TabLaunchType.FROM_LONGPRESS_BACKGROUND, mTab, isIncognito());
    }

    @Override
    public void onOpenInEphemeralTab(GURL url, String title) {
        if (mEphemeralTabCoordinatorSupplier == null
                || mEphemeralTabCoordinatorSupplier.get() == null) {
            return;
        }
        mEphemeralTabCoordinatorSupplier.get().requestOpenSheet(url, title, mTab.isIncognito());
    }

    @Override
    public void onReadLater(GURL url, String title) {
        if (url == null || url.isEmpty()) return;
        assert url.isValid();

        BookmarkModel bookmarkModel = new BookmarkModel();
        bookmarkModel.finishLoadingBookmarkModel(() -> {
            // Add to reading list.
            BookmarkUtils.addToReadingList(
                    url, title, mSnackbarManagerSupplier.get(), bookmarkModel, mTab.getContext());
            TrackerFactory.getTrackerForProfile(Profile.getLastUsedRegularProfile())
                    .notifyEvent(EventConstants.READ_LATER_CONTEXT_MENU_TAPPED);
            bookmarkModel.destroy();

            // Add to offline pages.
            RequestCoordinatorBridge.getForProfile(Profile.getLastUsedRegularProfile())
                    .savePageLater(url.getSpec(), OfflinePageBridge.BOOKMARK_NAMESPACE,
                            /*userRequested*/ true);
        });
    }

    @Override
    public void onOpenInChrome(GURL linkUrl, GURL pageUrl) {
        Context applicationContext = ContextUtils.getApplicationContext();
        Intent chromeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl.getSpec()));
        chromeIntent.setPackage(applicationContext.getPackageName());
        chromeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (PackageManagerUtils.queryIntentActivities(chromeIntent, 0).isEmpty()) {
            // If Chrome can't handle intent fallback to using any other VIEW handlers.
            chromeIntent.setPackage(null);

            // Query again without the package name set and if there are still no handlers for the
            // URI fail gracefully, and do nothing, since this will still cause a crash if launched.
            if (PackageManagerUtils.queryIntentActivities(chromeIntent, 0).isEmpty()) return;
        }

        boolean activityStarted = false;
        if (pageUrl != null) {
            if (UrlUtilities.isInternalScheme(pageUrl)) {
                IntentHandler.startChromeLauncherActivityForTrustedIntent(chromeIntent);
                activityStarted = true;
            }
        }

        if (!activityStarted) {
            mTab.getContext().startActivity(chromeIntent);
            activityStarted = true;
        }
    }

    @Override
    public void onOpenInNewChromeTabFromCCT(GURL linkUrl, boolean isIncognito) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl.getSpec()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(ContextUtils.getApplicationContext(), ChromeLauncherActivity.class);
        if (isIncognito) {
            intent.putExtra(IntentHandler.EXTRA_OPEN_NEW_INCOGNITO_TAB, true);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID,
                    ContextUtils.getApplicationContext().getPackageName());
            IntentHandler.addTrustedIntentExtras(intent);
            IntentHandler.setTabLaunchType(intent, TabLaunchType.FROM_EXTERNAL_APP);
        }
        IntentUtils.safeStartActivity(mTab.getContext(), intent);
    }

    @Override
    public String getTitleForOpenTabInExternalApp() {
        return DefaultBrowserInfo.getTitleOpenInDefaultBrowser(false);
    }

    @Override
    public void onOpenInDefaultBrowser(GURL url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.getSpec()));
        CustomTabsIntent.setAlwaysUseBrowserUI(intent);
        IntentUtils.safeStartActivity(mTab.getContext(), intent);
    }

    @Override
    public void removeHighlighting() {
        TextFragmentReceiver producer =
                mTab.getWebContents().getMainFrame().getInterfaceToRendererFrame(
                        TextFragmentReceiver.MANAGER);
        producer.removeFragments();
    }
}
