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
 * Axis along a {@link ChartView} that knows how to convert between raw point
 * and screen coordinate systems.
 * Derived from com.android.settings.widget.ChartAxis
 */
public interface ChartAxis {

    /** Set range of raw values this axis should cover. */
    public boolean setBounds(long min, long max);
    /** Set range of screen points this axis should cover. */
    public boolean setSize(float size);

    /** Convert raw value into screen point. */
    public float convertToPoint(long value);
    /** Convert screen point into raw value. */
    public long convertToValue(float point);

    /**
     * Build label that describes given raw value. If the label is rounded for
     * display, return the rounded value.
     */
    public long buildLabel(Resources res, SpannableStringBuilder builder, long value);

    /**
     * Test if given raw value should cause the axis to grow or shrink;
     * returning positive value to grow and negative to shrink.
     */
    public int shouldAdjustAxis(long value);

}
