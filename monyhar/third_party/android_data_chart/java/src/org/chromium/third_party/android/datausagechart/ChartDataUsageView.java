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
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;

import org.monyhar.third_party.android.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * Specific {@link ChartView} that displays {@link ChartNetworkSeriesView} for inspection ranges.
 * This is derived from com.android.settings.widget.ChartDataUsageView.
 */
public class ChartDataUsageView extends ChartView {
    public static final int MAXIMUM_DAYS_IN_CHART = 30;
    public static final int MINIMUM_DAYS_IN_CHART = 2;

    private static final long MB_IN_BYTES = 1024 * 1024;
    private static final long GB_IN_BYTES = 1024 * 1024 * 1024;

    private ChartNetworkSeriesView mOriginalSeries;
    private ChartNetworkSeriesView mCompressedSeries;

    private NetworkStatsHistory mHistory;

    private long mLeft;
    private long mRight;

    /** Current maximum value of {@link #mVert}. */
    private long mVertMax;

    /**
     * Constructs a new {@link ChartDataUsageView} with the appropriate context.
     */
    public ChartDataUsageView(Context context) {
        this(context, null, 0);
    }

    /**
     * Constructs a new {@link ChartDataUsageView} with the appropriate context and attributes.
     */
    public ChartDataUsageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructs a new {@link ChartDataUsageView} with the appropriate context, attributes, and
     * style.
     */
    public ChartDataUsageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(new TimeAxis(), new InvertedChartAxis(new DataAxis()));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mOriginalSeries = (ChartNetworkSeriesView) findViewById(R.id.original_series);
        mCompressedSeries = (ChartNetworkSeriesView) findViewById(R.id.compressed_series);

        // tell everyone about our axis
        mOriginalSeries.init(mHoriz, mVert);
        mCompressedSeries.init(mHoriz, mVert);
    }

    public void bindNetworkStats(
            NetworkStatsHistory originalStats, NetworkStatsHistory compressedStats) {
        mOriginalSeries.bindNetworkStats(originalStats);
        mCompressedSeries.bindNetworkStats(compressedStats);
        mCompressedSeries.setVisibility(compressedStats != null ? View.VISIBLE : View.GONE);
        mHistory = originalStats;
        if (mHistory != null) {
            // Update end time to match end of data.
            mOriginalSeries.setEndTime(mHistory.getEnd());
            mCompressedSeries.setEndTime(mHistory.getEnd());
        }
        // Clear any existing max range estimate.
        updateEstimateVisible();
        // Update the primary ranges of the series.
        updatePrimaryRange();
        // Determine the vertical max from the updated primary range for this original series.
        updateVertAxisBounds();
        requestLayout();
    }

    /**
     * Update {@link #mVert} to show data from {@link NetworkStatsHistory}.
     */
    private void updateVertAxisBounds() {
        long newMax = 0;

        // Determine the maximum value of the series data (which is scoped to the data to chart).
        final long maxSeries =
                Math.max(mOriginalSeries.getMaxStats(), mCompressedSeries.getMaxStats());
        final long maxVisible = Math.max(maxSeries, 0) * 12 / 10;
        final long maxDefault = Math.max(maxVisible, 1 * MB_IN_BYTES);
        newMax = Math.max(maxDefault, newMax);

        // only invalidate when vertMax actually changed
        if (newMax != mVertMax) {
            mVertMax = newMax;

            final boolean changed = mVert.setBounds(0L, newMax);

            if (changed) {
                mOriginalSeries.invalidatePath();
                mCompressedSeries.invalidatePath();
            }
        }
    }

    private void updateEstimateVisible() {
        mOriginalSeries.setEstimateVisible(false);
    }

    public long getInspectStart() {
        return mLeft;
    }

    public long getInspectEnd() {
        return mRight;
    }

    /**
     * Set the exact time range that should be displayed, updating how
     * {@link ChartNetworkSeriesView} paints. Moves inspection ranges
     * without triggering listener events.
     */
    public void setVisibleRange(long visibleStart, long visibleEnd) {
        final boolean changed = mHoriz.setBounds(visibleStart, visibleEnd);
        mOriginalSeries.setBounds(visibleStart, visibleEnd);
        mCompressedSeries.setBounds(visibleStart, visibleEnd);
        mLeft = visibleStart;
        mRight = visibleEnd;

        if (changed) {
            mOriginalSeries.invalidatePath();
            mCompressedSeries.invalidatePath();
        }

        // Clear any existing max range estimate.
        updateEstimateVisible();
        // Update the primary range for the data series.
        updatePrimaryRange();
        // Determine the vertical max from the updated primary range.
        updateVertAxisBounds();
        // Now request layout.
        requestLayout();
    }

    private void updatePrimaryRange() {
        final long left = mLeft;
        final long right = mRight;

        // prefer showing primary range on detail series, when available
        if (mCompressedSeries.getVisibility() == View.VISIBLE) {
            mCompressedSeries.setPrimaryRange(left, right);
            // Overlay the compressed series, when available, on top of the series.
            mOriginalSeries.setPrimaryRange(left, right);
        } else {
            mOriginalSeries.setPrimaryRange(left, right);
        }
    }

    /**
     * A chart axis that represents time.
     */
    public static class TimeAxis implements ChartAxis {
        private static final int FIRST_DAY_OF_WEEK = Calendar.getInstance().getFirstDayOfWeek() - 1;

        private long mMin;
        private long mMax;
        private float mSize;

        public TimeAxis() {
            final long currentTime = System.currentTimeMillis();
            setBounds(currentTime - DateUtils.DAY_IN_MILLIS * MAXIMUM_DAYS_IN_CHART, currentTime);
        }

        /**
         * Generates a hash code for multiple values. The hash code is generated by
         * calling {@link Arrays#hashCode(Object[])}.
         *
         * <p>This is useful for implementing {@link Object#hashCode()}. For example,
         * in an object that has three properties, {@code x}, {@code y}, and
         * {@code z}, one could write:
         * <pre>
         * public int hashCode() {
         *   return Objects.hashCode(getX(), getY(), getZ());
         * }</pre>
         *
         * <b>Warning</b>: When a single object is supplied, the returned hash code
         * does not equal the hash code of that object.
         */
        public int objectsHashCode(Object... objects) {
            return Arrays.hashCode(objects);
        }

        @Override
        public int hashCode() {
            return objectsHashCode(mMin, mMax, mSize);
        }

        @Override
        public boolean setBounds(long min, long max) {
            if (mMin != min || mMax != max) {
                mMin = min;
                mMax = max;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean setSize(float size) {
            if (mSize != size) {
                mSize = size;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public float convertToPoint(long value) {
            return (mSize * (value - mMin)) / (mMax - mMin);
        }

        @Override
        public long convertToValue(float point) {
            return (long) (mMin + ((point * (mMax - mMin)) / mSize));
        }

        @Override
        public long buildLabel(Resources res, SpannableStringBuilder builder, long value) {
            builder.replace(0, builder.length(), Long.toString(value));
            return value;
        }

        @Override
        public int shouldAdjustAxis(long value) {
            // time axis never adjusts
            return 0;
        }
    }

    /**
     * A chart axis that represents aggregate transmitted data.
     */
    public static class DataAxis implements ChartAxis {
        private long mMin;
        private long mMax;
        private float mSize;

        private static final boolean LOG_SCALE = false;

        public int objectsHashCode(Object... objects) {
            return Arrays.hashCode(objects);
        }

        @Override
        public int hashCode() {
            return objectsHashCode(mMin, mMax, mSize);
        }

        @Override
        public boolean setBounds(long min, long max) {
            if (mMin != min || mMax != max) {
                mMin = min;
                mMax = max;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean setSize(float size) {
            if (mSize != size) {
                mSize = size;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public float convertToPoint(long value) {
            if (LOG_SCALE) {
                // derived polynomial fit to make lower values more visible
                final double normalized = ((double) value - mMin) / (mMax - mMin);
                final double fraction = Math.pow(
                        10, 0.3688434310617512 * Math.log10(normalized) + -0.043281994520182526);
                return (float) (fraction * mSize);
            } else {
                return (mSize * (value - mMin)) / (mMax - mMin);
            }
        }

        @Override
        public long convertToValue(float point) {
            if (LOG_SCALE) {
                final double normalized = point / mSize;
                final double fraction =
                        1.3102228476089057 * Math.pow(normalized, 2.711177469316463);
                return (long) (mMin + (fraction * (mMax - mMin)));
            } else {
                return (long) (mMin + ((point * (mMax - mMin)) / mSize));
            }
        }

        private static final Object sSpanSize = new Object();
        private static final Object sSpanUnit = new Object();

        @Override
        public long buildLabel(Resources res, SpannableStringBuilder builder, long value) {

            final CharSequence unit;
            final long unitFactor;
            if (value < 1000 * MB_IN_BYTES) {
                unit = "MB";  // TODO(bengr): res.getText(R.string.origin_settings_storage_mbytes);
                unitFactor = MB_IN_BYTES;
            } else {
                unit = "GB";  // TODO(bengr): res.getText(R.string.origin_settings_storage_gbytes);
                unitFactor = GB_IN_BYTES;
            }

            final double result = (double) value / unitFactor;
            final double resultRounded;
            final CharSequence size;

            if (result < 10) {
                size = String.format(Locale.getDefault(), "%.1f", result);
                resultRounded = (unitFactor * Math.round(result * 10)) / 10d;
            } else {
                size = String.format(Locale.getDefault(), "%.0f", result);
                resultRounded = unitFactor * Math.round(result);
            }

            setText(builder, sSpanSize, size, "^1");
            setText(builder, sSpanUnit, unit, "^2");

            return (long) resultRounded;
        }

        @Override
        public int shouldAdjustAxis(long value) {
            final float point = convertToPoint(value);
            if (point < mSize * 0.1) {
                return -1;
            } else if (point > mSize * 0.85) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private static void setText(
            SpannableStringBuilder builder, Object key, CharSequence text, String bootstrap) {
        int start = builder.getSpanStart(key);
        int end = builder.getSpanEnd(key);
        if (start == -1) {
            start = TextUtils.indexOf(builder, bootstrap);
            end = start + bootstrap.length();
            builder.setSpan(key, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        builder.replace(start, end, text);
    }

    private static long roundUpToPowerOfTwo(long i) {
        // NOTE: borrowed from Hashtable.roundUpToPowerOfTwo()

        i--; // If input is a power of two, shift its high-order bit right

        // "Smear" the high-order bit all the way to the right
        i |= i >>>  1;
        i |= i >>>  2;
        i |= i >>>  4;
        i |= i >>>  8;
        i |= i >>> 16;
        i |= i >>> 32;

        i++;

        return i > 0 ? i : Long.MAX_VALUE;
    }
}
