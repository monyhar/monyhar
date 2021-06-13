// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.chrome.browser.keyboard_accessory.sheet_tabs;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.AllOf.allOf;

import static org.monyhar.chrome.browser.keyboard_accessory.ManualFillingTestHelper.isTransformed;
import static org.monyhar.chrome.browser.keyboard_accessory.ManualFillingTestHelper.selectTabAtPosition;
import static org.monyhar.chrome.browser.keyboard_accessory.ManualFillingTestHelper.whenDisplayed;
import static org.monyhar.chrome.browser.keyboard_accessory.tab_layout_component.KeyboardAccessoryTabTestHelper.isKeyboardAccessoryTabLayout;

import android.os.Build.VERSION_CODES;

import androidx.test.filters.SmallTest;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.util.CommandLineFlags;
import org.monyhar.base.test.util.CriteriaHelper;
import org.monyhar.base.test.util.DisableIf;
import org.monyhar.chrome.browser.flags.ChromeFeatureList;
import org.monyhar.chrome.browser.flags.ChromeSwitches;
import org.monyhar.chrome.browser.keyboard_accessory.ManualFillingTestHelper;
import org.monyhar.chrome.browser.keyboard_accessory.R;
import org.monyhar.chrome.test.ChromeJUnit4ClassRunner;
import org.monyhar.chrome.test.ChromeTabbedActivityTestRule;
import org.monyhar.chrome.test.util.browser.Features.EnableFeatures;
import org.monyhar.ui.test.util.UiDisableIf;

import java.util.concurrent.TimeoutException;

/**
 * Integration tests for password accessory views.
 */
@RunWith(ChromeJUnit4ClassRunner.class)
@CommandLineFlags.Add({ChromeSwitches.DISABLE_FIRST_RUN_EXPERIENCE})
public class PasswordAccessoryIntegrationTest {
    @Rule
    public final ChromeTabbedActivityTestRule mActivityTestRule =
            new ChromeTabbedActivityTestRule();

    private final ManualFillingTestHelper mHelper = new ManualFillingTestHelper(mActivityTestRule);

    @After
    public void tearDown() {
        mHelper.clear();
    }

    @Test
    @SmallTest
    public void testPasswordSheetIsAvailable() {
        mHelper.loadTestPage(false);

        CriteriaHelper.pollUiThread(() -> {
            return mHelper.getOrCreatePasswordAccessorySheet() != null;
        }, " Password Sheet should be bound to accessory sheet.");
    }

    @Test
    @SmallTest
    @DisableIf.
    Build(sdk_is_greater_than = VERSION_CODES.LOLLIPOP_MR1, sdk_is_less_than = VERSION_CODES.N,
            message = "Flaky on Marshmallow https://crbug.com/1102302")
    public void
    testPasswordSheetDisplaysProvidedItems() throws TimeoutException {
        mHelper.loadTestPage(false);
        mHelper.cacheCredentials("mayapark@gmail.com", "SomeHiddenPassword");

        // Focus the field to bring up the accessory.
        mHelper.focusPasswordField();
        mHelper.waitForKeyboardAccessoryToBeShown();
        whenDisplayed(allOf(isDisplayed(), isKeyboardAccessoryTabLayout()))
                .perform(selectTabAtPosition(0));

        // Check that the provided elements are there.
        whenDisplayed(withText("mayapark@gmail.com"));
        whenDisplayed(withText("SomeHiddenPassword")).check(matches(isTransformed()));
    }

    @Test
    @SmallTest
    public void testPasswordSheetDisplaysOptions() throws TimeoutException {
        mHelper.loadTestPage(false);

        // Focus the field to bring up the accessory.
        mHelper.focusPasswordField();
        mHelper.waitForKeyboardAccessoryToBeShown();
        whenDisplayed(allOf(isDisplayed(), isKeyboardAccessoryTabLayout()))
                .perform(selectTabAtPosition(0));

        mHelper.waitForKeyboardToDisappear();
        whenDisplayed(withId(R.id.passwords_sheet));
        onView(withText(containsString("Manage password"))).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    @DisableIf.Device(type = {UiDisableIf.TABLET}) // https://crbug.com/1111770
    public void testFillsPasswordOnTap() throws TimeoutException {
        mHelper.loadTestPage(false);
        mHelper.cacheCredentials("mpark@abc.com", "ShorterPassword");

        // Focus the field to bring up the accessory.
        mHelper.focusPasswordField();
        mHelper.waitForKeyboardAccessoryToBeShown();
        whenDisplayed(allOf(isDisplayed(), isKeyboardAccessoryTabLayout()))
                .perform(selectTabAtPosition(0));

        // Click the suggestion.
        whenDisplayed(withText("ShorterPassword")).perform(click());

        // The callback should have triggered and set the reference to the selected Item.
        CriteriaHelper.pollInstrumentationThread(
                () -> mHelper.getPasswordText().equals("ShorterPassword"));
    }

    @Test
    @SmallTest
    public void testDisplaysEmptyStateMessageWithoutSavedPasswords() throws TimeoutException {
        mHelper.loadTestPage(false);

        // Focus the field to bring up the accessory.
        mHelper.focusPasswordField();
        mHelper.waitForKeyboardAccessoryToBeShown();

        // Click the tab to show the sheet and hide the keyboard.
        whenDisplayed(allOf(isDisplayed(), isKeyboardAccessoryTabLayout()))
                .perform(selectTabAtPosition(0));
        mHelper.waitForKeyboardToDisappear();
        whenDisplayed(withId(R.id.passwords_sheet));
        onView(withText(containsString("No saved passwords"))).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    @EnableFeatures({ChromeFeatureList.RECOVER_FROM_NEVER_SAVE_ANDROID,
            ChromeFeatureList.AUTOFILL_KEYBOARD_ACCESSORY})
    public void
    testEnablesUndenylistingToggle() throws TimeoutException {
        mHelper.loadTestPage(false);
        mHelper.cacheCredentials(new String[0], new String[0], true);

        // Focus the field to bring up the accessory.
        mHelper.focusPasswordField();
        mHelper.waitForKeyboardAccessoryToBeShown();
        whenDisplayed(allOf(isDisplayed(), isKeyboardAccessoryTabLayout()))
                .perform(selectTabAtPosition(0));

        whenDisplayed(withId(R.id.option_toggle_switch)).check(matches(isNotChecked()));
        onView(withId(R.id.option_toggle_subtitle)).check(matches(withText(R.string.text_off)));

        whenDisplayed(withId(R.id.option_toggle_switch)).perform(click());
        onView(withId(R.id.option_toggle_switch)).check(matches(isChecked()));
        onView(withId(R.id.option_toggle_subtitle)).check(matches(withText(R.string.text_on)));
    }
}
