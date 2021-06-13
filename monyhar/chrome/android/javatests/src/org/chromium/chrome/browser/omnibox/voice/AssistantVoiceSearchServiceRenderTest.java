// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.omnibox.voice;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import static org.monyhar.base.test.util.Restriction.RESTRICTION_TYPE_NON_LOW_END_DEVICE;
import static org.monyhar.chrome.browser.preferences.ChromePreferenceKeys.ASSISTANT_VOICE_SEARCH_ENABLED;

import android.support.test.filters.MediumTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.DisabledTest;
import org.monyhar.base.test.util.Feature;
import org.monyhar.base.test.util.Restriction;
import org.monyhar.chrome.R;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.gsa.GSAState;
import org.monyhar.chrome.browser.preferences.SharedPreferencesManager;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.ChromeRenderTestRule;
import org.monyhar.chrome.test.util.browser.signin.AccountManagerTestRule;
import org.monyhar.components.embedder_support.util.UrlConstants;
import org.monyhar.components.externalauth.ExternalAuthUtils;
import org.monyhar.ui.test.util.DisableAnimationsTestRule;

import java.io.IOException;

/** Tests for AssistantVoiceSearchService */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE,
        "enable-features=" + ChromeFeatureList.OMNIBOX_ASSISTANT_VOICE_SEARCH + "<Study",
        "force-fieldtrials=Study/Group"})
@Restriction({RESTRICTION_TYPE_NON_LOW_END_DEVICE})
public class AssistantVoiceSearchServiceRenderTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();
    @Rule
    public ChromeRenderTestRule mRenderTestRule =
            ChromeRenderTestRule.Builder.withPublicCorpus().build();
    @Rule
    public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Rule
    public DisableAnimationsTestRule mDisableAnimationsTestRule = new DisableAnimationsTestRule();

    @Rule
    public final AccountManagerTestRule mAccountManagerTestRule = new AccountManagerTestRule();

    @Mock
    private GSAState mGsaState;
    @Mock
    private ExternalAuthUtils mExternalAuthUtils;

    @Before
    public void setUp() throws Exception {
        SharedPreferencesManager.getInstance().writeBoolean(ASSISTANT_VOICE_SEARCH_ENABLED, true);

        doReturn(false).when(mGsaState).isAgsaVersionBelowMinimum(anyString(), anyString());
        doReturn(true).when(mGsaState).canAgsaHandleIntent(anyObject());
        doReturn(true).when(mGsaState).isGsaInstalled();
        GSAState.setInstanceForTesting(mGsaState);

        doReturn(true).when(mExternalAuthUtils).isGoogleSigned(anyString());
        doReturn(true).when(mExternalAuthUtils).isChromeGoogleSigned();
        ExternalAuthUtils.setInstanceForTesting(mExternalAuthUtils);

        mActivityTestRule.startMainActivityOnBlankPage();
        mAccountManagerTestRule.addTestAccountThenSigninAndEnableSync();
    }

    @Test
    @MediumTest
    @CommandLineFlags.Add({"force-fieldtrial-params=Study.Group:colorful_mic/true"})
    @Feature({"RenderTest"})
    @DisabledTest(message = "crbug.com/1196384")
    public void testAssistantColorfulMic() throws IOException {
        mActivityTestRule.loadUrl(UrlConstants.NTP_URL);

        mRenderTestRule.render(mActivityTestRule.getActivity().findViewById(R.id.ntp_content),
                "avs_colorful_mic_unfocused_ntp");

        onView(withId(R.id.search_box)).perform(click());
        mRenderTestRule.render(mActivityTestRule.getActivity().findViewById(R.id.toolbar),
                "avs_colorful_mic_focused");
    }

    @Test
    @MediumTest
    @CommandLineFlags.Add({"force-fieldtrial-params=Study.Group:colorful_mic/false"})
    @Feature({"RenderTest"})
    public void testAssistantMic() throws IOException {
        mActivityTestRule.loadUrl(UrlConstants.NTP_URL);

        mRenderTestRule.render(mActivityTestRule.getActivity().findViewById(R.id.ntp_content),
                "avs__mic_unfocused_ntp");

        onView(withId(R.id.search_box)).perform(click());
        mRenderTestRule.render(
                mActivityTestRule.getActivity().findViewById(R.id.toolbar), "avs_mic_focused");
    }
}
