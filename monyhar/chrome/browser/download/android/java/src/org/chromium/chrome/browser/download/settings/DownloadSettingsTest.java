// Copyright 2021 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.download.settings;

import androidx.preference.Preference;
import androidx.test.filters.MediumTest;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.Criteria;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.base.test.util.Matchers;
import org.monyhar.chrome.browser.download.DownloadDialogBridge;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.preferences.Pref;
import org.monyhar.chrome.browser.profiles.Profile;
import org.monyhar.chrome.browser.settings.SettingsActivityTestRule;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.browser.Features.DisableFeatures;
import org.monyhar.chrome.test.util.browser.Features.EnableFeatures;
import org.monyhar.components.browser_ui.settings.ChromeSwitchPreference;
import org.monyhar.components.browser_ui.settings.ManagedPreferenceDelegate;
import org.monyhar.components.policy.test.annotations.Policies;
import org.monyhar.components.prefs.PrefService;
import org.monyhar.components.user_prefs.UserPrefs;
import org.monyhar.content_public.browser.test.util.TestThreadUtils;

/**
 * Test for download settings.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class DownloadSettingsTest {
    @Rule
    public ChromeTabbedActivityTestRule mActivityTestRule = new ChromeTabbedActivityTestRule();
    @Rule
    public final SettingsActivityTestRule<DownloadSettings> mSettingsActivityTestRule =
            new SettingsActivityTestRule<>(DownloadSettings.class);
    @Rule
    public final RuleChain mRuleChain =
            RuleChain.outerRule(mActivityTestRule).around(mSettingsActivityTestRule);

    @Before
    public void setUp() {
        mActivityTestRule.startMainActivityFromLauncher();
    }

    private Preference assertPreference(final String preferenceKey) throws Exception {
        return assertPreference(preferenceKey, Matchers.notNullValue());
    }

    private Preference assertPreference(final String preferenceKey, Matcher<Object> matcher)
            throws Exception {
        DownloadSettings downloadSettings = mSettingsActivityTestRule.getFragment();
        CriteriaHelper.pollUiThread(() -> {
            Criteria.checkThat("Expected valid preference for: " + preferenceKey,
                    downloadSettings.findPreference(preferenceKey), matcher);
        });

        return TestThreadUtils.runOnUiThreadBlocking(
                () -> downloadSettings.findPreference(preferenceKey));
    }

    private void waitForPolicyReady() {
        // Policy data from the annotation needs to be populated before the setting UI is opened.
        CriteriaHelper.pollUiThread(() -> DownloadDialogBridge.isLocationDialogManaged());
    }

    private void verifyLocationPromptPolicy(boolean promptForDownload) {
        TestThreadUtils.runOnUiThreadBlocking(() -> {
            Assert.assertTrue(DownloadDialogBridge.isLocationDialogManaged());
            Assert.assertTrue(getPrefService().isManagedPreference(Pref.PROMPT_FOR_DOWNLOAD));
            DownloadSettings downloadSettings = mSettingsActivityTestRule.getFragment();
            ChromeSwitchPreference locationPromptPreference =
                    downloadSettings.findPreference(DownloadSettings.PREF_LOCATION_PROMPT_ENABLED);
            Assert.assertEquals(promptForDownload, locationPromptPreference.isChecked());
            ManagedPreferenceDelegate delegate =
                    downloadSettings.getLocationPromptEnabledPrefDelegateForTesting();
            Assert.assertTrue(delegate.isPreferenceControlledByPolicy(locationPromptPreference));
        });
    }

    PrefService getPrefService() {
        return UserPrefs.get(Profile.getLastUsedRegularProfile());
    }

    @Test
    @MediumTest
    @EnableFeatures(ChromeFeatureList.DOWNLOAD_LATER)
    public void testGeneralSettings() throws Exception {
        mSettingsActivityTestRule.startSettingsActivity();
        assertPreference(DownloadSettings.PREF_LOCATION_CHANGE);
        assertPreference(DownloadSettings.PREF_LOCATION_PROMPT_ENABLED);
        assertPreference(DownloadSettings.PREF_DOWNLOAD_LATER_PROMPT_ENABLED);
        assertPreference(DownloadSettings.PREF_PREFETCHING_ENABLED);
        mSettingsActivityTestRule.getActivity().finish();
    }

    @Test
    @MediumTest
    @DisableFeatures(ChromeFeatureList.DOWNLOAD_LATER)
    public void testWithoutDownloadLater() throws Exception {
        mSettingsActivityTestRule.startSettingsActivity();
        assertPreference(DownloadSettings.PREF_LOCATION_CHANGE);
        assertPreference(DownloadSettings.PREF_LOCATION_PROMPT_ENABLED);
        assertPreference(DownloadSettings.PREF_DOWNLOAD_LATER_PROMPT_ENABLED, Matchers.nullValue());
        assertPreference(DownloadSettings.PREF_PREFETCHING_ENABLED);
        mSettingsActivityTestRule.getActivity().finish();
    }

    @Test
    @MediumTest
    @Policies.Add({ @Policies.Item(key = "PromptForDownloadLocation", string = "true") })
    public void testLocationPromptEnabledManagedByPolicy() throws Exception {
        waitForPolicyReady();
        mSettingsActivityTestRule.startSettingsActivity();
        verifyLocationPromptPolicy(true);
        mSettingsActivityTestRule.getActivity().finish();
    }

    @Test
    @MediumTest
    @Policies.Add({ @Policies.Item(key = "PromptForDownloadLocation", string = "false") })
    public void testLocationPromptDisabledManagedByPolicy() throws Exception {
        waitForPolicyReady();
        mSettingsActivityTestRule.startSettingsActivity();
        verifyLocationPromptPolicy(false);
        mSettingsActivityTestRule.getActivity().finish();
    }
}
