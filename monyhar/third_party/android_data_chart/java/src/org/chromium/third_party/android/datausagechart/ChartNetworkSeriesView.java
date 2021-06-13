/*
 * Copyright (C) 2013 The Android Open Source Project
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.monyhar.third_party.android.R;

/**
 * {@link NetworkStatsHistory} series to render inside a {@link ChartView},
 * using {@link ChartAxis} to map into screen coordinates.
 * This is derived from com.android.settings.widget.ChartNetworkSeriesView.
 */
public class ChartNetworkSeriesView extends View {
    private static final String TAG = "ChartNetworkSeriesView";
    private static final boolean LOGD = false;

    private ChartAxis mHoriz;
    private ChartAxis mVert;

    private Paint mPaintStroke;
    private Paint mPaintFill;
    private Paint mPaintFillSecondary;
    private Paint mPaintEstimate;

    private NetworkStatsHistory mStats;

    private Path mPathStroke;
    private Path mPathFill;
    private Path mPathEstimate;

    private long mStart;
    private long mEnd;

    private long mPrimaryLeft;
    private long mPrimaryRight;

    /** Series will be extended to reach this end time. */
    private long mEndTime = Long.MIN_VALUE;

    private boolean mPathValid;
    private boolean mEstimateVisible;

    private long mMax;
    private long mMaxEstimate;

    public ChartNetworkSeriesView(Context context) {
        this(context, null, 0);
    }

    public ChartNetworkSeriesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartNetworkSeriesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ChartNetworkSeriesView, defStyle, 0);

        final int stroke = a.getColor(R.styleable.ChartNetworkSeriesView_strokeColor, Color.RED);
        final int fill = a.getColor(R.styleable.ChartNetworkSeriesView_fillColor, Color.RED);
        final int fillSecondary = a.getColor(
                R.styleable.ChartNetworkSeriesView_fillColorSecondary, Color.RED);

        setChartColor(stroke, fill, fillSecondary);
        setWillNotDraw(false);

        a.recycle();

        mPathStroke = new Path();
        mPathFill = new Path();
        mPathEstimate = new Path();
    }

    void init(ChartAxis horiz, ChartAxis vert) {
        if (horiz == null) throw new NullPointerException("missing horiz");
        if (vert == null) throw new NullPointerException("missing vert");
        mHoriz = horiz;
        mVert = vert;
    }

    public void setChartColor(int stroke, int fill, int fillSecondary) {
        mPaintStroke = new Paint();
        mPaintStroke.setStrokeWidth(4.0f * getResources().getDisplayMetrics().density);
        mPaintStroke.setColor(stroke);
        mPaintStroke.setStyle(Style.STROKE);
        mPaintStroke.setAntiAlias(true);

        mPaintFill = new Paint();
        mPaintFill.setColor(fill);
        mPaintFill.setStyle(Style.FILL);
        mPaintFill.setAntiAlias(true);

        mPaintFillSecondary = new Paint();
        mPaintFillSecondary.setColor(fillSecondary);
        mPaintFillSecondary.setStyle(Style.FILL);
        mPaintFillSecondary.setAntiAlias(true);

        mPaintEstimate = new Paint();
        mPaintEstimate.setStrokeWidth(3.0f);
        mPaintEstimate.setColor(fillSecondary);
        mPaintEstimate.setStyle(Style.STROKE);
        mPaintEstimate.setAntiAlias(true);
        mPaintEstimate.setPathEffect(new DashPathEffect(new float[] { 10, 10 }, 1));
    }

    public void bindNetworkStats(NetworkStatsHistory stats) {
        mStats = stats;
        invalidatePath();
        invalidate();
    }

    public void setBounds(long start, long end) {
        mStart = start;
        mEnd = end;
        if (end > mEndTime) {
            mEndTime = end;
        }
    }

    /**
     * Set the range to paint with {@link #mPaintFill}, leaving the remaining
     * area to be painted with {@link #mPaintFillSecondary}.
     */
    public void setPrimaryRange(long left, long right) {
        mPrimaryLeft = left;
        mPrimaryRight = right;
        invalidate();
    }

    public void invalidatePath() {
        mPathValid = false;
        mMax = 0;
        invalidate();
    }

    /**
     * Erase any existing {@link Path} and generate series outline based on
     * currently bound {@link NetworkStatsHistory} data.
     */
    private void generatePath() {
        if (LOGD) Log.d(TAG, "generatePath()");

        mMax = 0;
        mPathStroke.reset();
        mPathFill.reset();
        mPathEstimate.reset();
        mPathValid = true;

        // bail when not enough stats to render
        if (mStats == null || mStats.size() < 1) {
            return;
        }

        final int height = getHeight();

        boolean started = false;
        float lastX = 0;
        float lastY = height;
        long lastTime = mHoriz.convertToValue(lastX);

        // move into starting position
        mPathStroke.moveTo(lastX, lastY);
        mPathFill.moveTo(lastX, lastY);

        // TODO(bengr): count fractional data from first bucket crossing start;
        // currently it only accepts first full bucket.

        long totalData = 0;

        NetworkStatsHistory.Entry entry = null;

        final int start = mStats.getIndexBefore(mStart);
        final int end = mStats.getIndexAfter(mEnd);
        for (int i = start; i <= end; i++) {
            entry = mStats.getValues(i, entry);

            final long startTime = entry.bucketStart;
            final long endTime = startTime + entry.bucketDuration;

            final float startX = mHoriz.convertToPoint(startTime);
            final float endX = mHoriz.convertToPoint(endTime);

            // skip until we find first stats on screen
            if (endX < 0) continue;

            // increment by current bucket total
            totalData += entry.rxBytes + entry.txBytes;

            final float startY = lastY;
            final float endY = mVert.convertToPoint(totalData);

            if (lastTime != startTime) {
                // gap in buckets; line to start of current bucket
                mPathStroke.lineTo(startX, startY);
                mPathFill.lineTo(startX, startY);
            }

            // always draw to end of current bucket
            mPathStroke.lineTo(endX, endY);
            mPathFill.lineTo(endX, endY);

            lastX = endX;
            lastY = endY;
            lastTime = endTime;
        }

        // when data falls short, extend to requested end time
        if (lastTime < mEndTime) {
            lastX = mHoriz.convertToPoint(mEndTime);

            mPathStroke.lineTo(lastX, lastY);
            mPathFill.lineTo(lastX, lastY);
        }

        if (LOGD) {
            final RectF bounds = new RectF();
            mPathFill.computeBounds(bounds, true);
            Log.d(TAG, "onLayout() rendered with bounds=" + bounds.toString() + " and totalData="
                    + totalData);
        }

        // drop to bottom of graph from current location
        mPathFill.lineTo(lastX, height);
        mPathFill.lineTo(0, height);

        mMax = totalData;

        invalidate();
    }

    public void setEndTime(long endTime) {
        mEndTime = endTime;
    }

    public void setEstimateVisible(boolean estimateVisible) {
        mEstimateVisible = false;
        invalidate();
    }

    public long getMaxEstimate() {
        return mMaxEstimate;
    }

    public long getMaxVisible() {
        final long maxVisible = mEstimateVisible ? mMaxEstimate : mMax;
        if (maxVisible <= 0 && mStats != null) {
            // haven't generated path yet; fall back to raw data
            final NetworkStatsHistory.Entry entry = mStats.getValues(mStart, mEnd, null);
            return entry.rxBytes + entry.txBytes;
        } else {
            return maxVisible;
        }
    }

    /** Returns the total network byte count in the current stats. */
    public long getMaxStats() {
        if (mStats == null) {
            return 0;
        }
        final NetworkStatsHistory.Entry entry =
                mStats.getValues(mStats.getStart(), mStats.getEnd(), null);
        return entry.rxBytes + entry.txBytes;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int save;

        if (!mPathValid) {
            generatePath();
        }

        final float primaryLeftPoint = mHoriz.convertToPoint(mPrimaryLeft);
        final float primaryRightPoint = mHoriz.convertToPoint(mPrimaryRight);

        if (mEstimateVisible) {
            save = canvas.save();
            canvas.clipRect(0, 0, getWidth(), getHeight());
            canvas.drawPath(mPathEstimate, mPaintEstimate);
            canvas.restoreToCount(save);
        }

        save = canvas.save();
        canvas.clipRect(0, 0, primaryLeftPoint, getHeight());
        canvas.drawPath(mPathFill, mPaintFillSecondary);
        canvas.restoreToCount(save);

        save = canvas.save();
        canvas.clipRect(primaryRightPoint, 0, getWidth(), getHeight());
        canvas.drawPath(mPathFill, mPaintFillSecondary);
        canvas.restoreToCount(save);

        save = canvas.save();
        canvas.clipRect(primaryLeftPoint, 0, primaryRightPoint, getHeight());
        canvas.drawPath(mPathFill, mPaintFill);
        canvas.drawPath(mPathStroke, mPaintStroke);
        canvas.restoreToCount(save);

    }
}
