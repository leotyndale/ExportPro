package com.alibaba.imagesearch.searchml;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.core.content.ContextCompat;

import com.alibaba.base.App;
import com.alibaba.imagesearch.searchml.overlay.GraphicOverlay;
import com.alibaba.imagesearch.searchml.process.ObjectDetectorProcessor;
import com.alibaba.imagesearch.searchml.process.VisionImageProcessor;
import com.google.mlkit.common.MlKit;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.util.List;
import java.util.concurrent.Callable;

public class SearchAnalysis {
    private static final String TAG = "SearchAnalysis";
    private static boolean mIsInit = false;

    private VisionImageProcessor<List<DetectedObject>> mImageProcessor;
    private ImageAnalysis mAnalysisUseCase;
    private boolean mNeedUpdateGraphicOverlayImageSourceInfo;
    private final GraphicOverlay mGraphicOverlay;

    public static void onInit() {
        if (mIsInit) {
            return;
        }
        try {
            MlKit.initialize(App.context().getApplicationContext());
            mIsInit = true;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public ImageAnalysis getUseCase() {
        return mAnalysisUseCase;
    }

    public SearchAnalysis(ViewGroup viewGroup, Callable<Boolean> onSuccess) {
        onInit();
        mGraphicOverlay = new GraphicOverlay(viewGroup.getContext(), null);
        mGraphicOverlay.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        viewGroup.addView(mGraphicOverlay);
        bindAnalysisUseCase(viewGroup.getContext(), onSuccess);
    }

    public void bindAnalysisUseCase(Context context, Callable<Boolean> onSuccess) {
        if (mImageProcessor != null) {
            mImageProcessor.stop();
        }
        try {
            Log.i(TAG, "Using Object Detector Processor");
            LocalModel localModel =
                    new LocalModel.Builder()
                            .setAssetFilePath("custom_models/mnasnet_1.3_224_1_metadata_1.tflite")
                            .build();
            CustomObjectDetectorOptions customObjectDetectorOptions =
                    new CustomObjectDetectorOptions.Builder(localModel)
                            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                            .enableClassification()
                            .setMaxPerObjectLabelCount(1)
                            .build();
            mImageProcessor = new ObjectDetectorProcessor(customObjectDetectorOptions,onSuccess);

        } catch (Exception e) {
            Log.e(TAG, "Can not create image processor: ", e);
            return;
        }

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        mAnalysisUseCase = builder
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        mNeedUpdateGraphicOverlayImageSourceInfo = true;
        mAnalysisUseCase.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                imageProxy -> {
                    updateOverlayInfo(imageProxy);
                    try {
                        mImageProcessor.processImageProxy(imageProxy, mGraphicOverlay);
                    } catch (MlKitException e) {
                        Log.e(TAG, "Failed to process image. Error: " + e.getLocalizedMessage());
                    }
                });
    }

    private void updateOverlayInfo(ImageProxy imageProxy) {
        if (mNeedUpdateGraphicOverlayImageSourceInfo) {
            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
            if (rotationDegrees == 0 || rotationDegrees == 180) {
                mGraphicOverlay.setImageSourceInfo(
                        imageProxy.getWidth(), imageProxy.getHeight(), false);
            } else {
                mGraphicOverlay.setImageSourceInfo(
                        imageProxy.getHeight(), imageProxy.getWidth(), false);
            }
            mNeedUpdateGraphicOverlayImageSourceInfo = false;
        }
    }


}
