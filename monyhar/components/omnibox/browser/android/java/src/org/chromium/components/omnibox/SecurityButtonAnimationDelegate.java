// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.components.omnibox;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.DimenRes;

import org.monyhar.components.browser_ui.widget.animation.CancelAwareAnimatorListener;
import org.monyhar.ui.interpolators.BakedBezierInterpolator;

/**
 * Helper class to animate the security status icon.
 */
public class SecurityButtonAnimationDelegate {
    public static final int SLIDE_DURATION_MS = 200;
    public static final int FADE_DURATION_MS = 150;

    private final ImageButton mSecurityButton;
    private final View mTitleUrlContainer;
    private final AnimatorSet mSecurityButtonShowAnimator;
    private final AnimatorSet mSecurityButtonHideAnimator;
    private final int mSecurityButtonWidth;

    public SecurityButtonAnimationDelegate(
            ImageButton securityButton, View urlTextView, @DimenRes int securityButtonIconSize) {
        mSecurityButton = securityButton;
        mTitleUrlContainer = urlTextView;
        mSecurityButtonWidth =
                mSecurityButton.getResources().getDimensionPixelSize(securityButtonIconSize);

        mSecurityButtonShowAnimator = new AnimatorSet();
        Animator translateRight = ObjectAnimator.ofFloat(mTitleUrlContainer, View.TRANSLATION_X, 0);
        translateRight.setInterpolator(BakedBezierInterpolator.TRANSFORM_CURVE);
        translateRight.setDuration(SLIDE_DURATION_MS);

        Animator fadeIn = ObjectAnimator.ofFloat(mSecurityButton, View.ALPHA, 1);
        fadeIn.setInterpolator(BakedBezierInterpolator.FADE_IN_CURVE);
        fadeIn.setDuration(FADE_DURATION_MS);
        fadeIn.addListener(new CancelAwareAnimatorListener() {
            @Override
            public void onStart(Animator animation) {
                mSecurityButton.setVisibility(View.VISIBLE);
            }
        });
        mSecurityButtonShowAnimator.playSequentially(translateRight, fadeIn);

        mSecurityButtonHideAnimator = new AnimatorSet();
        Animator fadeOut = ObjectAnimator.ofFloat(mSecurityButton, View.ALPHA, 0);
        fadeOut.setInterpolator(BakedBezierInterpolator.FADE_OUT_CURVE);
        fadeOut.setDuration(FADE_DURATION_MS);
        fadeOut.addListener(new CancelAwareAnimatorListener() {
            @Override
            public void onEnd(Animator animation) {
                mSecurityButton.setVisibility(View.INVISIBLE);
            }
        });

        Animator translateLeft = ObjectAnimator.ofFloat(
                mTitleUrlContainer, View.TRANSLATION_X, -mSecurityButtonWidth);
        translateLeft.setInterpolator(BakedBezierInterpolator.TRANSFORM_CURVE);
        translateLeft.setDuration(SLIDE_DURATION_MS);
        mSecurityButtonHideAnimator.playSequentially(fadeOut, translateLeft);
    }

    /**
     * Based on |securityIconResource|, animates the security status icon in or out.
     * @param securityIconResource The updated resource to be assigned to the security status icon.
     * When this is null, the icon is animated to the left and faded out.
     */
    public void updateSecurityButton(int securityIconResource) {
        if (securityIconResource == 0) {
            // No icon to display.
            mSecurityButton.setImageDrawable(null);
            hideSecurityButton();
        } else {
            // ImageView#setImageResource is no-op if given resource is the current one.
            mSecurityButton.setImageResource(securityIconResource);
            showSecurityButton();
        }
    }

    /**
     * Starts the animation to show the security button.
     */
    private void showSecurityButton() {
        if (mSecurityButtonHideAnimator.isStarted()) mSecurityButtonHideAnimator.cancel();
        if (mSecurityButtonShowAnimator.isStarted()
                || mSecurityButton.getVisibility() == View.VISIBLE) {
            return;
        }
        mSecurityButtonShowAnimator.start();
    }

    /**
     * Starts the animation to hide the security button.
     */
    private void hideSecurityButton() {
        if (mSecurityButtonShowAnimator.isStarted()) mSecurityButtonShowAnimator.cancel();
        if (mSecurityButtonHideAnimator.isStarted()
                || mTitleUrlContainer.getTranslationX() == -mSecurityButtonWidth) {
            return;
        }
        mSecurityButtonHideAnimator.start();
    }
}
