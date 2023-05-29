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

package com.alibaba.imagesearch.searchml.process;

import android.content.res.Resources;
import android.graphics.RectF;

import com.alibaba.imagesearch.searchml.overlay.GraphicOverlay;
import com.alibaba.imagesearch.searchml.overlay.ObjectGraphic;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.ObjectDetectorOptionsBase;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * A processor to run object detector.
 */
public class ObjectDetectorProcessor extends VisionProcessorBase<List<DetectedObject>> {

    public static final int WIDTH_PIXELS = Resources.getSystem().getDisplayMetrics().widthPixels;

    private final ObjectDetector mDetector;
    private final Callable<Boolean> mOnSuccess;

    public ObjectDetectorProcessor(ObjectDetectorOptionsBase options, Callable<Boolean> onSuccess) {
        super();
        mDetector = ObjectDetection.getClient(options);
        this.mOnSuccess = onSuccess;
    }

    @Override
    public void stop() {
        super.stop();
        try {
            mDetector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Task<List<DetectedObject>> detectInImage(InputImage image) {
        return mDetector.process(image);
    }

    @Override
    protected boolean onSuccess(
             List<DetectedObject> results,  GraphicOverlay graphicOverlay) {
        boolean isChanged = false;
        for (DetectedObject object : results) {
            if (object == null) {
                continue;
            }
            ObjectGraphic graphic = new ObjectGraphic(graphicOverlay, object);
            RectF rect = new RectF(object.getBoundingBox());
            graphic.translateRectF(rect);
            if (rect.width() <= WIDTH_PIXELS) {
                graphicOverlay.add(graphic);
                isChanged = true;
            }
        }
        if (mOnSuccess != null && isChanged) {
            try {
                if (mOnSuccess.call()) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    protected void onFailure( Exception e) {
    }
}
