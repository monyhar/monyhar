// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.toolbar.top;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import androidx.test.filters.MediumTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.ApiCompatibilityUtils;
import org.monyhar.base.test.params.ParameterAnnotations;
import org.monyhar.base.test.params.ParameterSet;
import org.monyhar.base.test.params.ParameterizedRunner;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.R;
import org.monyhar.chrome.test.ChromeJUnit4RunnerDelegate;
import org.monyhar.chrome.test.util.ChromeRenderTestRule;
import org.monyhar.components.browser_ui.widget.listmenu.ListMenuButton;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.ui.test.util.DummyUiActivityTestCase;
import org.monyhar.ui.test.util.NightModeTestUtils;

import java.io.IOException;
import java.util.List;

/**
 * Render tests for tab switcher long-press menu popup.
 */
@RunWith(ParameterizedRunner.class)
@ParameterAnnotations.UseRunnerDelegate(ChromeJUnit4RunnerDelegate.class)
public class TabSwitcherActionMenuRenderTest extends DummyUiActivityTestCase {
    @ParameterAnnotations.ClassParameter
    private static List<ParameterSet> sClassParams =
            new NightModeTestUtils.NightModeParams().getParameters();

    @Rule
    public ChromeRenderTestRule mRenderTestRule =
            ChromeRenderTestRule.Builder.withPublicCorpus().setRevision(1).build();

    private View mView;

    public TabSwitcherActionMenuRenderTest(boolean nightModeEnabled) {
        NightModeTestUtils.setUpNightModeForDummyUiActivity(nightModeEnabled);
        mRenderTestRule.setNightModeEnabled(nightModeEnabled);
    }

    @Override
    public void setUpTest() throws Exception {
        super.setUpTest();
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            Activity activity = getActivity();
            TabSwitcherActionMenuCoordinator coordinator = new TabSwitcherActionMenuCoordinator();

            coordinator.displayMenu(activity, new ListMenuButton(activity, null),
                    coordinator.buildMenuItems(), null);

            mView = coordinator.getContentView();
            ((ViewGroup) mView.getParent()).removeView(mView);

            int popupWidth =
                    activity.getResources().getDimensionPixelSize(R.dimen.tab_switcher_menu_width);
            mView.setBackground(ApiCompatibilityUtils.getDrawable(
                    activity.getResources(), R.drawable.popup_bg_tinted));
            activity.setContentView(mView, new LayoutParams(popupWidth, WRAP_CONTENT));
        });
    }

    @Override
    public void tearDownTest() throws Exception {
        NightModeTestUtils.tearDownNightModeForDummyUiActivity();
        super.tearDownTest();
    }

    @Test
    @MediumTest
    @Feature({"RenderTest"})
    public void testRender_TabSwitcherActionMenu() throws IOException {
        mRenderTestRule.render(mView, "tab_switcher_action_menu");
    }
}
