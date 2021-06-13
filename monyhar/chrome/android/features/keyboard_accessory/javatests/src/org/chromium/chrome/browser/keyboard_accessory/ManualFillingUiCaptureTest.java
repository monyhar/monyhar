// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.keyboard_accessory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.monyhar.base.test.util.ScalableTimeout.scaleTimeout;
import static org.monyhar.chrome.browser.keyboard_accessory.ManualFillingTestHelper.scrollToLastElement;
import static org.monyhar.chrome.browser.keyboard_accessory.ManualFillingTestHelper.selectTabAtPosition;
import static org.monyhar.chrome.browser.keyboard_accessory.ManualFillingTestHelper.whenDisplayed;
import static org.monyhar.chrome.browser.keyboard_accessory.tab_layout_component.KeyboardAccessoryTabTestHelper.isKeyboardAccessoryTabLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.DisabledTest;
import org.monyhar.base.test.util.Feature;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.test.ScreenShooter;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.browser.Features.DisableFeatures;
import org.monyhar.chrome.test.util.browser.Features.EnableFeatures;
import org.monyhar.components.browser_ui.widget.RecyclerViewTestUtils;

import java.util.concurrent.TimeoutException;

/**
 * Screenshot test for manual filling views. They ensure that we don't regress on visual details
 * like shadows, padding and RTL differences. Logic integration tests involving all filling
 * components belong into {@link ManualFillingIntegrationTest}.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@EnableFeatures({ChromeFeatureList.AUTOFILL_KEYBOARD_ACCESSORY})
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class ManualFillingUiCaptureTest {
    @Rule
    public final ChromeTabbedActivityTestRule mActivityTestRule =
            new ChromeTabbedActivityTestRule();

    @Rule
    public final ScreenShooter mScreenShooter = new ScreenShooter();

    private final ManualFillingTestHelper mHelper = new ManualFillingTestHelper(mActivityTestRule);

    @After
    public void tearDown() {
        mHelper.clear();
    }

    @Test
    @MediumTest
    @DisableFeatures(ChromeFeatureList.AUTOFILL_KEYBOARD_ACCESSORY)
    @Feature({"KeyboardAccessory", "LTR", "UiCatalogue"})
    @DisabledTest(message = "Flaky, see https://crbug.com/1095672")
    public void testCaptureKeyboardAccessoryWithPasswords()
            throws InterruptedException, TimeoutException {
        mHelper.loadTestPage(false);
        mHelper.cacheTestCredentials();
        mHelper.addGenerationButton();

        mHelper.focusPasswordField();
        mHelper.waitForKeyboardAccessoryToBeShown();

        waitForActionsInAccessory();
        waitForUnrelatedChromeUi();
        mScreenShooter.shoot("AccessoryBar");

        whenDisplayed(withId(R.id.tabs)).perform(selectTabAtPosition(0));
        waitForSuggestionsInSheet();
        waitForUnrelatedChromeUi();
        mScreenShooter.shoot("AccessorySheetPasswords");

        whenDisplayed(withId(R.id.passwords_sheet)).perform(scrollToLastElement());
        waitForUnrelatedChromeUi();
        mScreenShooter.shoot("AccessorySheetPasswordsScrolled");
    }

    @Test
    @MediumTest
    @DisableFeatures(ChromeFeatureList.AUTOFILL_KEYBOARD_ACCESSORY)
    @Feature({"KeyboardAccessory", "RTL", "UiCatalogue"})
    @DisabledTest(message = "Flaky, see https://crbug.com/1095672")
    public void testCaptureKeyboardAccessoryWithPasswordsRTL()
            throws InterruptedException, TimeoutException {
        mHelper.loadTestPage(true);
        mHelper.cacheTestCredentials();
        mHelper.focusPasswordField();
        mHelper.waitForKeyboardAccessoryToBeShown();
        mHelper.addGenerationButton();

        waitForActionsInAccessory();
        waitForUnrelatedChromeUi();
        mScreenShooter.shoot("AccessoryBarRTL");

        whenDisplayed(withId(R.id.tabs)).perform(selectTabAtPosition(0));
        waitForSuggestionsInSheet();
        waitForUnrelatedChromeUi();
        mScreenShooter.shoot("AccessorySheetPasswordsRTL");

        whenDisplayed(withId(R.id.passwords_sheet)).perform(scrollToLastElement());
        waitForUnrelatedChromeUi();
        mScreenShooter.shoot("AccessorySheetPasswordsScrolledRTL");
    }

    @Test
    @MediumTest
    @EnableFeatures(ChromeFeatureList.AUTOFILL_KEYBOARD_ACCESSORY)
    @Feature({"KeyboardAccessoryModern", "LTR", "UiCatalogue"})
    @DisabledTest(message = "Flaky, see https://crbug.com/1095672")
    public void testCaptureKeyboardAccessoryV2WithPasswords()
            throws InterruptedException, TimeoutException {
        mHelper.loadTestPage(false);
        ManualFillingTestHelper.createAutofillTestProfiles();
        mHelper.cacheTestCredentials();
        mHelper.focusPasswordField();
        mHelper.waitForKeyboardAccessoryToBeShown();
        mHelper.addGenerationButton();

        waitForActionsInAccessory();
        waitForUnrelatedChromeUi();
        mScreenShooter.shoot("AccessoryBarV2");

        whenDisplayed(withId(R.id.bar_items_view))
                .perform(scrollTo(isKeyboardAccessoryTabLayout()),
                        actionOnItem(isKeyboardAccessoryTabLayout(), selectTabAtPosition(0)));

        waitForSuggestionsInSheet();
        waitForUnrelatedChromeUi();
        mScreenShooter.shoot("AccessorySheetPasswordsV2");

        whenDisplayed(withId(R.id.passwords_sheet)).perform(scrollToLastElement());
        waitForUnrelatedChromeUi();
        mScreenShooter.shoot("AccessorySheetPasswordsV2Scrolled");
    }

    @Test
    @MediumTest
    @EnableFeatures(ChromeFeatureList.AUTOFILL_KEYBOARD_ACCESSORY)
    @Feature({"KeyboardAccessoryModern", "RTL", "UiCatalogue"})
    @DisabledTest(message = "Flaky, see https://crbug.com/1095672")
    public void testCaptureKeyboardAccessoryV2WithPasswordsRTL()
            throws InterruptedException, TimeoutException {
        mHelper.loadTestPage(true);
        ManualFillingTestHelper.createAutofillTestProfiles();
        mHelper.cacheTestCredentials();
        mHelper.focusPasswordField();
        mHelper.waitForKeyboardAccessoryToBeShown();
        mHelper.addGenerationButton();

        waitForActionsInAccessory();
        waitForUnrelatedChromeUi();
        mScreenShooter.shoot("AccessoryBarV2RTL");

        whenDisplayed(withId(R.id.bar_items_view))
                .perform(scrollTo(isKeyboardAccessoryTabLayout()),
                        actionOnItem(isKeyboardAccessoryTabLayout(), selectTabAtPosition(0)));

        waitForSuggestionsInSheet();
        waitForUnrelatedChromeUi();
        mScreenShooter.shoot("AccessorySheetPasswordsV2RTL");

        whenDisplayed(withId(R.id.passwords_sheet)).perform(scrollToLastElement());
        waitForUnrelatedChromeUi();
        mScreenShooter.shoot("AccessorySheetPasswordsV2ScrolledRTL");
    }

    private void waitForUnrelatedChromeUi() throws InterruptedException {
        Thread.sleep(scaleTimeout(50)); // Reduces flakiness due to delayed events.
    }

    private void waitForActionsInAccessory() {
        whenDisplayed(withId(R.id.bar_items_view));
        onView(withId(R.id.bar_items_view)).check((view, noViewFound) -> {
            if (noViewFound != null) throw noViewFound;
            RecyclerViewTestUtils.waitForStableRecyclerView((RecyclerView) view);
        });
    }

    private void waitForSuggestionsInSheet() {
        whenDisplayed(withId(R.id.keyboard_accessory_sheet));
        onView(withId(R.id.passwords_sheet)).check((view, noViewFound) -> {
            if (noViewFound != null) throw noViewFound;
            RecyclerViewTestUtils.waitForStableRecyclerView((RecyclerView) view);
        });
    }
}
