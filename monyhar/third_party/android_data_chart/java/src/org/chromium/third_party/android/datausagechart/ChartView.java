/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.monyhar.third_party.android.datausagechart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.widget.FrameLayout;

import org.monyhar.third_party.android.R;

/**
 * Container for two-dimensional chart, drawn with a combination of {@link ChartNetworkSeriesView}
 * children. The entire chart uses {@link ChartAxis} to map between raw values and screen
 * coordinates. Derived from com.android.settings.widget.ChartView
 */
public class ChartView extends FrameLayout {
    ChartAxis mHoriz;
    ChartAxis mVert;

    @ViewDebug.ExportedProperty
    private int mOptimalWidth = -1;
    private float mOptimalWidthWeight;

    // Used in onLayout(). Reused to avoid allocations during layout.
    private Rect mContent = new Rect();
    private Rect mChildRect = new Rect();

    public ChartView(Context context) {
        this(context, null, 0);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ChartView, defStyle, 0);
        setOptimalWidth(a.getDimensionPixelSize(R.styleable.ChartView_optimalWidth, -1),
                a.getFloat(R.styleable.ChartView_optimalWidthWeight, 0));
        a.recycle();

        setClipToPadding(false);
        setClipChildren(false);
    }

    void init(ChartAxis horiz, ChartAxis vert) {
        if (horiz == null) throw new NullPointerException("missing horiz");
        if (vert == null) throw new NullPointerException("missing vert");
        mHoriz = horiz;
        mVert = vert;
    }

    public void setOptimalWidth(int optimalWidth, float optimalWidthWeight) {
        mOptimalWidth = optimalWidth;
        mOptimalWidthWeight = optimalWidthWeight;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int slack = getMeasuredWidth() - mOptimalWidth;
        if (mOptimalWidth > 0 && slack > 0) {
            final int targetWidth = (int) (mOptimalWidth + (slack * mOptimalWidthWeight));
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(targetWidth, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mContent.set(getPaddingLeft(), getPaddingTop(), r - l - getPaddingRight(),
                b - t - getPaddingBottom());
        final int width = mContent.width();
        final int height = mContent.height();

        // no scrolling yet, so tell dimensions to fill exactly
        mHoriz.setSize(width);
        mVert.setSize(height);

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            final LayoutParams params = (LayoutParams) child.getLayoutParams();

            if (child instanceof ChartNetworkSeriesView) {
                // series are always laid out to fill entire graph area
                // Scrolling is not supported for series larger than the content area.
                Gravity.apply(params.gravity, width, height, mContent, mChildRect);
                child.layout(mChildRect.left, mChildRect.top, mChildRect.right, mChildRect.bottom);

            }
        }
    }
}
