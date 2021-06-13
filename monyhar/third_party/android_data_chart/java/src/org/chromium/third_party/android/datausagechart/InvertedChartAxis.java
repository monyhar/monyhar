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

import android.content.res.Resources;
import android.text.SpannableStringBuilder;

/**
 * Utility to invert another {@link ChartAxis}.
 * Derived from com.android.settings.widget.InvertedChartAxis
 */
public class InvertedChartAxis implements ChartAxis {
    private final ChartAxis mWrapped;
    private float mSize;

    public InvertedChartAxis(ChartAxis wrapped) {
        mWrapped = wrapped;
    }

    @Override
    public boolean setBounds(long min, long max) {
        return mWrapped.setBounds(min, max);
    }

    @Override
    public boolean setSize(float size) {
        mSize = size;
        return mWrapped.setSize(size);
    }

    @Override
    public float convertToPoint(long value) {
        return mSize - mWrapped.convertToPoint(value);
    }

    @Override
    public long convertToValue(float point) {
        return mWrapped.convertToValue(mSize - point);
    }

    @Override
    public long buildLabel(Resources res, SpannableStringBuilder builder, long value) {
        return mWrapped.buildLabel(res, builder, value);
    }

    @Override
    public int shouldAdjustAxis(long value) {
        return mWrapped.shouldAdjustAxis(value);
    }
}
