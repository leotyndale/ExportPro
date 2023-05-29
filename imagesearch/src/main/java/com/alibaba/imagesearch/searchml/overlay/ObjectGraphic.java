/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may context a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.imagesearch.searchml.overlay;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.TypedValue;

import com.google.mlkit.vision.objects.DetectedObject;

/**
 * Draw the detected object info in preview.
 */
public class ObjectGraphic extends GraphicOverlay.Graphic {

    private static final float TEXT_SIZE = 150.0f;
    private static final float STROKE_WIDTH = 18f;
    private static final int NUM_COLORS = 10;
    private static final int[][] COLORS =
            new int[][]{
                    // {Text color, background color}
                    {Color.BLACK, Color.WHITE},
                    {Color.WHITE, Color.MAGENTA},
                    {Color.BLACK, Color.LTGRAY},
                    {Color.WHITE, Color.RED},
                    {Color.WHITE, Color.BLUE},
                    {Color.WHITE, Color.DKGRAY},
                    {Color.BLACK, Color.CYAN},
                    {Color.BLACK, Color.YELLOW},
                    {Color.WHITE, Color.BLACK},
                    {Color.BLACK, Color.GREEN}
            };
    public final DetectedObject mObject;
    private final Paint[] mBoxPaints;
    private final Paint[] mTextPaints;
    private final Paint[] mLabelPaints;
    public static String mText;

    public ObjectGraphic(GraphicOverlay overlay, DetectedObject object) {
        super(overlay);

        this.mObject = object;

        int numColors = COLORS.length;
        mTextPaints = new Paint[numColors];
        mBoxPaints = new Paint[numColors];
        mLabelPaints = new Paint[numColors];
        for (int i = 0; i < numColors; i++) {
            mTextPaints[i] = new Paint();
            mTextPaints[i].setColor(COLORS[i][0] /* text color */);
            mTextPaints[i].setTextSize(TEXT_SIZE);

            mBoxPaints[i] = new Paint();
            mBoxPaints[i].setColor(Color.WHITE /* background color */);
            mBoxPaints[i].setStyle(Paint.Style.STROKE);
            mBoxPaints[i].setStrokeWidth(STROKE_WIDTH);
//            boxPaints[i].setPathEffect(new DashPathEffect(new float[]{60, 60}, 0));

            mLabelPaints[i] = new Paint();
            mLabelPaints[i].setColor(COLORS[i][1] /* background color */);
            mLabelPaints[i].setStyle(Paint.Style.FILL);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        try {
            int colorID =
                    mObject.getTrackingId() == null ? 0 : Math.abs(mObject.getTrackingId() % NUM_COLORS);
            RectF rect = new RectF(mObject.getBoundingBox());
            translateRectF(rect);
            drawCorners(canvas, rect, mBoxPaints[colorID]);

            for (DetectedObject.Label label : mObject.getLabels()) {
                Paint textPaint = mTextPaints[colorID];
                textPaint.setColor(Color.WHITE);
                mText = label.getText();
                canvas.drawText(mText, rect.left, rect.centerY(), textPaint);
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void translateRectF(RectF rect) {
        float x0 = translateX(rect.left);
        float x1 = translateX(rect.right);
        rect.left = Math.min(x0, x1);
        rect.right = Math.max(x0, x1);
        rect.top = translateY(rect.top);
        rect.bottom = translateY(rect.bottom);
    }

    private void drawCorners(Canvas canvas, RectF rect, Paint mBorderCornerPaint) {
        int cornerRadius = dp2px(8);
        float mBorderCornerLength = dp2px(24);
        // Top left
        int borderOffset = cornerRadius - dp2px(1);
        canvas.drawLine(
                rect.left,
                rect.top + borderOffset,
                rect.left,
                rect.top + mBorderCornerLength,
                mBorderCornerPaint);

        canvas.drawLine(
                rect.left + borderOffset,
                rect.top,
                rect.left + mBorderCornerLength,
                rect.top,
                mBorderCornerPaint);
        canvas.drawArc(new RectF(rect.left, rect.top
                        , rect.left + cornerRadius * 2,
                        rect.top + cornerRadius * 2),
                -90, -90, false,
                mBorderCornerPaint);

        // Top right
        canvas.drawLine(
                rect.right,
                rect.top + borderOffset,
                rect.right,
                rect.top + mBorderCornerLength,
                mBorderCornerPaint);
        canvas.drawLine(
                rect.right - borderOffset,
                rect.top,
                rect.right - mBorderCornerLength,
                rect.top,
                mBorderCornerPaint);
        canvas.drawArc(new RectF(rect.right - cornerRadius * 2, rect.top
                        , rect.right,
                        rect.top + cornerRadius * 2),
                0, -90, false,
                mBorderCornerPaint);

        // Bottom left
        canvas.drawLine(
                rect.left,
                rect.bottom - borderOffset,
                rect.left,
                rect.bottom - mBorderCornerLength,
                mBorderCornerPaint);
        canvas.drawLine(
                rect.left + borderOffset,
                rect.bottom,
                rect.left + mBorderCornerLength,
                rect.bottom,
                mBorderCornerPaint);
        canvas.drawArc(new RectF(rect.left, rect.bottom - cornerRadius * 2, rect.left + cornerRadius * 2, rect.bottom),
                90, 90, false, mBorderCornerPaint);

        // Bottom right
        canvas.drawLine(
                rect.right,
                rect.bottom - borderOffset,
                rect.right,
                rect.bottom - mBorderCornerLength,
                mBorderCornerPaint);
        canvas.drawLine(
                rect.right - borderOffset,
                rect.bottom,
                rect.right - mBorderCornerLength,
                rect.bottom,
                mBorderCornerPaint);
        canvas.drawArc(new RectF(rect.right - cornerRadius * 2, rect.bottom - cornerRadius * 2
                        , rect.right,
                        rect.bottom),
                0, 90, false,
                mBorderCornerPaint);
    }

    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                Resources.getSystem().getDisplayMetrics());
    }
}
