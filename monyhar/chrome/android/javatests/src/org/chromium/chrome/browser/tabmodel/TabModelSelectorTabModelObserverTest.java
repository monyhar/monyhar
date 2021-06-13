// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.tabmodel;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.BaseJUnit4ClassRunner;
import org.monyhar.base.test.UiThreadTest;
import org.monyhar.base.test.util.Batch;
import org.monyhar.base.test.util.CallbackHelper;
import org.monyhar.chrome.browser.tab.Tab;
import org.monyhar.chrome.browser.tab.TabLaunchType;
import org.monyhar.chrome.browser.tabmodel.TabModelSelectorObserverTestRule.TabModelSelectorTestTabModel;
import org.monyhar.content_public.browser.LoadUrlParams;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;

import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Tests for the TabModelSelectorTabModelObserver.
 */
@RunWith(BaseJUnit4ClassRunner.class)
@Batch(Batch.PER_CLASS)
public class TabModelSelectorTabModelObserverTest {
    @ClassRule
    public static final TabModelSelectorObserverTestRule sTestRule =
            new TabModelSelectorObserverTestRule();

    private TabModelSelectorBase mSelector;

    @Before
    public void setUp() {
        mSelector = sTestRule.getSelector();
    }

    @Test
    @SmallTest
    public void testAlreadyInitializedSelector() throws TimeoutException {
        final CallbackHelper registrationCompleteCallback = new CallbackHelper();
        TabModelSelectorTabModelObserver observer =
                TestThreadUtils.runOnUiThreadBlockingNoException(
                        () -> new TabModelSelectorTabModelObserver(mSelector) {
                            @Override
                            protected void onRegistrationComplete() {
                                registrationCompleteCallback.notifyCalled();
                            }
                        });
        registrationCompleteCallback.waitForCallback(0);
        assertAllModelsHaveObserver(mSelector, observer);
    }

    @Test
    @UiThreadTest
    @SmallTest
    public void testUninitializedSelector() throws TimeoutException {
        mSelector = new TabModelSelectorBase(null, EmptyTabModelFilter::new, false) {
            @Override
            public Tab openNewTab(LoadUrlParams loadUrlParams, @TabLaunchType int type, Tab parent,
                    boolean incognito) {
                return null;
            }
        };
        final CallbackHelper registrationCompleteCallback = new CallbackHelper();
        TabModelSelectorTabModelObserver observer =
                new TabModelSelectorTabModelObserver(mSelector) {
                    @Override
                    protected void onRegistrationComplete() {
                        registrationCompleteCallback.notifyCalled();
                    }
                };
        mSelector.initialize(sTestRule.getNormalTabModel(), sTestRule.getIncognitoTabModel());
        registrationCompleteCallback.waitForCallback(0);
        assertAllModelsHaveObserver(mSelector, observer);
    }

    private static void assertAllModelsHaveObserver(
            TabModelSelector selector, TabModelObserver observer) {
        List<TabModel> models = selector.getModels();
        for (int i = 0; i < models.size(); i++) {
            Assert.assertTrue(models.get(i) instanceof TabModelSelectorTestTabModel);
            Assert.assertTrue(((TabModelSelectorTestTabModel) models.get(i))
                                      .getObservers()
                                      .contains(observer));
        }
    }
}
