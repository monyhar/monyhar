// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.omnibox.voice;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.test.filters.MediumTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.UiThreadTest;
import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.util.ChromeRenderTestRule;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;
import org.monyhar.ui.test.util.DummyUiActivityTestCase;

import java.io.IOException;

/** Render tests for AssistantVoiceSearchConsentDialog */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class AssistantVoiceSearchConsentUiRenderTest extends DummyUiActivityTestCase {
    @Rule
    public ChromeRenderTestRule mRenderTestRule =
            ChromeRenderTestRule.Builder.withPublicCorpus().build();

    private ViewGroup mParentView;
    private LinearLayout mContentView;

    @Override
    public void setUpTest() throws Exception {
        super.setUpTest();

        TestThreadUtils.runOnUiThreadBlocking(() -> {
            getActivity().setContentView(R.layout.assistant_voice_search_consent_ui);
        });
    }

    @Test
    @UiThreadTest
    @MediumTest
    @Feature({"RenderTest"})
    public void testShow() throws IOException {
        mRenderTestRule.render(
                getActivity().findViewById(R.id.avs_consent_ui), "avs_consent_ui_ntp");
    }
}