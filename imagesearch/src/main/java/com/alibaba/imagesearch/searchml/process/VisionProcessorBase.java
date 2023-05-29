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

import androidx.camera.core.ImageProxy;

import com.alibaba.imagesearch.searchml.ScopedExecutor;
import com.alibaba.imagesearch.searchml.overlay.CameraImageGraphic;
import com.alibaba.imagesearch.searchml.overlay.GraphicOverlay;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.mlkit.vision.common.InputImage;

/**
 * Abstract base class for vision frame processors. Subclasses need to implement {@link
 * #onSuccess(Object, GraphicOverlay)} to define what they want to with the detection results and
 * {@link #detectInImage(InputImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
public abstract class VisionProcessorBase<T> implements VisionImageProcessor<T> {

    private final ScopedExecutor mExecutor;
    private boolean mIsShutdown;
    protected VisionProcessorBase() {
        mExecutor = new ScopedExecutor(TaskExecutors.MAIN_THREAD);
    }

    @Override
    public void processImageProxy(ImageProxy image, GraphicOverlay graphicOverlay) {
        if (mIsShutdown) {
            image.close();
            return;
        }

        requestDetectInImage(
                InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees()),
                graphicOverlay
        ).addOnCompleteListener(results -> image.close());
    }

    // -----------------Common processing logic-------------------------------------------------------
    private Task<T> requestDetectInImage(
            final InputImage image,
            final GraphicOverlay graphicOverlay) {
        return setUpListener(
                detectInImage(image), graphicOverlay);
    }

    private Task<T> setUpListener(
            Task<T> task,
            final GraphicOverlay graphicOverlay) {
        return task.addOnSuccessListener(
                        mExecutor,
                        results -> {
                            graphicOverlay.clear();
                            if (VisionProcessorBase.this.onSuccess(results, graphicOverlay)) {
                                graphicOverlay.postInvalidate();
                            }
                        })
                .addOnFailureListener(
                        mExecutor,
                        e -> {
                            graphicOverlay.clear();
                            graphicOverlay.postInvalidate();
                            e.printStackTrace();
                            VisionProcessorBase.this.onFailure(e);
                        });
    }

    @Override
    public void stop() {
        mExecutor.shutdown();
        mIsShutdown = true;
    }

    protected abstract Task<T> detectInImage(InputImage image);

    protected abstract boolean onSuccess( T results, GraphicOverlay graphicOverlay);

    protected abstract void onFailure( Exception e);

}
