// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.browser_ui.modaldialog;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.monyhar.components.browser_ui.modaldialog.ModalDialogTestUtils.checkCurrentPresenter;
import static org.monyhar.components.browser_ui.modaldialog.ModalDialogTestUtils.checkDialogDismissalCause;
import static org.monyhar.components.browser_ui.modaldialog.ModalDialogTestUtils.checkPendingSize;
import static org.monyhar.components.browser_ui.modaldialog.ModalDialogTestUtils.createDialog;
import static org.monyhar.components.browser_ui.modaldialog.ModalDialogTestUtils.showDialog;

import androidx.test.espresso.Espresso;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.monyhar.base.test.BaseJUnit4ClassRunner;
import org.monyhar.base.test.util.CallbackHelper;
import org.monyhar.base.test.util.Feature;
import org.monyhar.ui.modaldialog.DialogDismissalCause;
import org.monyhar.ui.modaldialog.ModalDialogManager;
import org.monyhar.ui.modaldialog.ModalDialogManager.ModalDialogType;
import org.monyhar.ui.modelutil.PropertyModel;
import org.monyhar.ui.test.util.DummyUiActivityTestCase;

/**
 * Tests for {@link AppModalPresenter}.
 */
@RunWith(BaseJUnit4ClassRunner.class)
public class AppModalPresenterTest extends DummyUiActivityTestCase {
    private class TestObserver implements ModalDialogTestUtils.TestDialogDismissedObserver {
        public final CallbackHelper onDialogDismissedCallback = new CallbackHelper();

        @Override
        public void onDialogDismissed(int dismissalCause) {
            onDialogDismissedCallback.notifyCalled();
            checkDialogDismissalCause(mExpectedDismissalCause, dismissalCause);
        }
    }

    private ModalDialogManager mManager;
    private TestObserver mTestObserver;
    private Integer mExpectedDismissalCause;

    @Override
    public void setUpTest() throws Exception {
        super.setUpTest();
        mManager = new ModalDialogManager(
                new AppModalPresenter(getActivity()), ModalDialogManager.ModalDialogType.APP);
        mTestObserver = new TestObserver();
    }

    @Test
    @SmallTest
    @Feature({"ModalDialog"})
    public void testDismiss_BackPressed() throws Exception {
        PropertyModel dialog1 = createDialog(getActivity(), mManager, "1", null);
        PropertyModel dialog2 = createDialog(getActivity(), mManager, "2", null);

        // Initially there are no dialogs in the pending list. Browser controls are not restricted.
        checkPendingSize(mManager, ModalDialogType.APP, 0);
        checkCurrentPresenter(mManager, null);

        // Add three dialogs available for showing. The app modal dialog should be shown first.
        showDialog(mManager, dialog1, ModalDialogType.APP);
        showDialog(mManager, dialog2, ModalDialogType.APP);
        checkPendingSize(mManager, ModalDialogType.APP, 1);
        onView(withText("1")).check(matches(isDisplayed()));
        checkCurrentPresenter(mManager, ModalDialogType.APP);

        // Perform back press. The first app modal dialog should be dismissed, and the second one
        // should be shown.
        Espresso.pressBack();
        checkPendingSize(mManager, ModalDialogType.APP, 0);
        onView(withText("1")).check(doesNotExist());
        onView(withText("2")).check(matches(isDisplayed()));
        checkCurrentPresenter(mManager, ModalDialogType.APP);

        // Perform a second back press. The second app modal dialog should be dismissed.
        Espresso.pressBack();
        checkPendingSize(mManager, ModalDialogType.APP, 0);
        onView(withText("2")).check(doesNotExist());
        checkCurrentPresenter(mManager, null);
    }

    @Test
    @SmallTest
    @Feature({"ModalDialog"})
    public void testDismiss_DismissalCause_BackPressed() throws Exception {
        PropertyModel dialog1 = createDialog(getActivity(), mManager, "1", mTestObserver);
        mExpectedDismissalCause = DialogDismissalCause.NAVIGATE_BACK_OR_TOUCH_OUTSIDE;

        showDialog(mManager, dialog1, ModalDialogType.APP);

        // Dismiss the tab modal dialog and verify dismissal cause.
        int callCount = mTestObserver.onDialogDismissedCallback.getCallCount();
        Espresso.pressBack();
        mTestObserver.onDialogDismissedCallback.waitForCallback(callCount);

        mExpectedDismissalCause = null;
    }
}
