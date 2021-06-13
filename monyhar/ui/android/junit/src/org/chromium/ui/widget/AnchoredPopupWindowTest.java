// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.ui.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.graphics.Rect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import org.monyhar.base.test.BaseRobolectricTestRunner;

/**
 * Unit tests for the static positioning methods in {@link AnchoredPopupWindow}.
 */
@RunWith(BaseRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public final class AnchoredPopupWindowTest {
    private Rect mWindowRect;
    int mPopupWidth;
    int mPopupHeight;

    @Before
    public void setUp() {
        mWindowRect = new Rect(0, 0, 600, 1000);
        mPopupWidth = 150;
        mPopupHeight = 300;
    }

    @Test
    public void testGetPopupPosition_BelowRight() {
        Rect anchorRect = new Rect(10, 10, 20, 20);

        int spaceLeftOfAnchor =
                AnchoredPopupWindow.getSpaceLeftOfAnchor(anchorRect, mWindowRect, false);
        int spaceRightOfAnchor =
                AnchoredPopupWindow.getSpaceRightOfAnchor(anchorRect, mWindowRect, false);
        boolean positionToLeft = AnchoredPopupWindow.shouldPositionLeftOfAnchor(
                spaceLeftOfAnchor, spaceRightOfAnchor, mPopupWidth, false, false);

        assertEquals("Space left of anchor incorrect.", 10, spaceLeftOfAnchor);
        assertEquals("Space right of anchor incorrect.", 580, spaceRightOfAnchor);
        assertFalse("positionToLeft incorrect.", positionToLeft);

        int x = AnchoredPopupWindow.getPopupX(anchorRect, mWindowRect, mPopupWidth, 0, false,
                AnchoredPopupWindow.HorizontalOrientation.MAX_AVAILABLE_SPACE, false);
        int y = AnchoredPopupWindow.getPopupY(anchorRect, mPopupHeight, false, true);

        assertEquals("Wrong x position.", 20, x);
        assertEquals("Wrong y position.", 20, y);
    }

    @Test
    public void testGetPopupPosition_BelowRight_Overlap() {
        Rect anchorRect = new Rect(10, 10, 20, 20);

        int spaceLeftOfAnchor =
                AnchoredPopupWindow.getSpaceLeftOfAnchor(anchorRect, mWindowRect, true);
        int spaceRightOfAnchor =
                AnchoredPopupWindow.getSpaceRightOfAnchor(anchorRect, mWindowRect, true);
        boolean positionToLeft = AnchoredPopupWindow.shouldPositionLeftOfAnchor(
                spaceLeftOfAnchor, spaceRightOfAnchor, mPopupWidth, false, false);

        assertEquals("Space left of anchor incorrect.", 20, spaceLeftOfAnchor);
        assertEquals("Space right of anchor incorrect.", 590, spaceRightOfAnchor);
        assertFalse("positionToLeft incorrect.", positionToLeft);

        int x = AnchoredPopupWindow.getPopupX(anchorRect, mWindowRect, mPopupWidth, 0, true,
                AnchoredPopupWindow.HorizontalOrientation.MAX_AVAILABLE_SPACE, false);
        int y = AnchoredPopupWindow.getPopupY(anchorRect, mPopupHeight, true, true);

        assertEquals("Wrong x position.", 10, x);
        assertEquals("Wrong y position.", 10, y);
    }

    @Test
    public void testGetPopupPosition_BelowCenter() {
        Rect anchorRect = new Rect(295, 10, 305, 20);
        int x = AnchoredPopupWindow.getPopupX(anchorRect, mWindowRect, mPopupWidth, 0, false,
                AnchoredPopupWindow.HorizontalOrientation.CENTER, false);
        int y = AnchoredPopupWindow.getPopupY(anchorRect, mPopupHeight, false, true);

        assertEquals("Wrong x position.", 225, x);
        assertEquals("Wrong y position.", 20, y);
    }

    @Test
    public void getPopupPosition_AboveLeft() {
        Rect anchorRect = new Rect(400, 800, 410, 820);

        int spaceLeftOfAnchor =
                AnchoredPopupWindow.getSpaceLeftOfAnchor(anchorRect, mWindowRect, false);
        int spaceRightOfAnchor =
                AnchoredPopupWindow.getSpaceRightOfAnchor(anchorRect, mWindowRect, false);
        boolean positionToLeft = AnchoredPopupWindow.shouldPositionLeftOfAnchor(
                spaceLeftOfAnchor, spaceRightOfAnchor, mPopupWidth, false, false);

        assertEquals("Space left of anchor incorrect.", 400, spaceLeftOfAnchor);
        assertEquals("Space right of anchor incorrect.", 190, spaceRightOfAnchor);
        assertTrue("positionToLeft incorrect.", positionToLeft);

        int x = AnchoredPopupWindow.getPopupX(anchorRect, mWindowRect, mPopupWidth, 0, false,
                AnchoredPopupWindow.HorizontalOrientation.MAX_AVAILABLE_SPACE, positionToLeft);
        int y = AnchoredPopupWindow.getPopupY(anchorRect, mPopupHeight, false, false);

        assertEquals("Wrong x position.", 250, x);
        assertEquals("Wrong y position.", 500, y);
    }

    @Test
    public void testGetPopupPosition_AboveLeft_Overlap() {
        Rect anchorRect = new Rect(400, 800, 410, 820);

        int spaceLeftOfAnchor =
                AnchoredPopupWindow.getSpaceLeftOfAnchor(anchorRect, mWindowRect, true);
        int spaceRightOfAnchor =
                AnchoredPopupWindow.getSpaceRightOfAnchor(anchorRect, mWindowRect, true);
        boolean positionToLeft = AnchoredPopupWindow.shouldPositionLeftOfAnchor(
                spaceLeftOfAnchor, spaceRightOfAnchor, mPopupWidth, false, false);

        assertEquals("Space left of anchor incorrect.", 410, spaceLeftOfAnchor);
        assertEquals("Space right of anchor incorrect.", 200, spaceRightOfAnchor);
        assertTrue("positionToLeft incorrect.", positionToLeft);

        int x = AnchoredPopupWindow.getPopupX(anchorRect, mWindowRect, mPopupWidth, 0, true,
                AnchoredPopupWindow.HorizontalOrientation.MAX_AVAILABLE_SPACE, true);
        int y = AnchoredPopupWindow.getPopupY(anchorRect, mPopupHeight, true, false);

        assertEquals("Wrong x position.", 260, x);
        assertEquals("Wrong y position.", 520, y);
    }

    @Test
    public void testGetPopupPosition_ClampedLeftEdge() {
        Rect anchorRect = new Rect(10, 10, 20, 20);
        int x = AnchoredPopupWindow.getPopupX(anchorRect, mWindowRect, mPopupWidth, 20, false,
                AnchoredPopupWindow.HorizontalOrientation.MAX_AVAILABLE_SPACE, true);

        assertEquals("Wrong x position.", 20, x);
    }

    @Test
    public void testGetPopupPosition_ClampedRightEdge() {
        Rect anchorRect = new Rect(590, 800, 600, 820);
        int x = AnchoredPopupWindow.getPopupX(anchorRect, mWindowRect, mPopupWidth, 20, false,
                AnchoredPopupWindow.HorizontalOrientation.MAX_AVAILABLE_SPACE, true);

        assertEquals("Wrong x position.", 430, x);
    }

    @Test
    public void testShouldPositionLeftOfAnchor() {
        Rect anchorRect = new Rect(300, 10, 310, 20);
        int spaceLeftOfAnchor =
                AnchoredPopupWindow.getSpaceLeftOfAnchor(anchorRect, mWindowRect, false);
        int spaceRightOfAnchor =
                AnchoredPopupWindow.getSpaceRightOfAnchor(anchorRect, mWindowRect, false);
        boolean positionToLeft = AnchoredPopupWindow.shouldPositionLeftOfAnchor(
                spaceLeftOfAnchor, spaceRightOfAnchor, mPopupWidth, false, false);

        assertEquals("Space left of anchor incorrect.", 300, spaceLeftOfAnchor);
        assertEquals("Space right of anchor incorrect.", 290, spaceRightOfAnchor);
        assertTrue("Should be positioned to the left.", positionToLeft);

        anchorRect = new Rect(250, 10, 260, 20);
        spaceLeftOfAnchor =
                AnchoredPopupWindow.getSpaceLeftOfAnchor(anchorRect, mWindowRect, false);
        spaceRightOfAnchor =
                AnchoredPopupWindow.getSpaceRightOfAnchor(anchorRect, mWindowRect, false);
        positionToLeft = AnchoredPopupWindow.shouldPositionLeftOfAnchor(
                spaceLeftOfAnchor, spaceRightOfAnchor, mPopupWidth, true, true);

        // There is more space to the right, but the popup will still fit to the left and should
        // be positioned to the left.
        assertEquals("Space left of anchor incorrect.", 250, spaceLeftOfAnchor);
        assertEquals("Space right of anchor incorrect.", 340, spaceRightOfAnchor);
        assertTrue("Should still be positioned to the left.", positionToLeft);
    }

    @Test
    public void testGetMaxContentWidth() {
        int maxWidth = AnchoredPopupWindow.getMaxContentWidth(300, 600, 10, 10);
        assertEquals("Max width should be based on desired width.", 290, maxWidth);

        maxWidth = AnchoredPopupWindow.getMaxContentWidth(300, 300, 10, 10);
        assertEquals("Max width should be based on root view width.", 270, maxWidth);

        maxWidth = AnchoredPopupWindow.getMaxContentWidth(0, 600, 10, 10);
        assertEquals("Max width should be based on root view width when desired with is 0.", 570,
                maxWidth);

        maxWidth = AnchoredPopupWindow.getMaxContentWidth(300, 300, 10, 300);
        assertEquals("Max width should be clamped at 0.", 0, maxWidth);
    }
}
