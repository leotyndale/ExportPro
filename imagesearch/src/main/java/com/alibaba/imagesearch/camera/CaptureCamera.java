package com.alibaba.imagesearch.camera;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraState;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCase;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.alibaba.imagesearch.searchml.SearchAnalysis;
import com.alibaba.imagesearch.sensor.DeviceMotionDetector;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class CaptureCamera {

    private final Context mContext;
    private final LifecycleOwner mLifecycleOwner;
    private Camera mCamera;
    boolean mIsBackCamera = true;
    private ImageCapture mImageCapture;
    private ProcessCameraProvider mCameraProvider;
    private PreviewView mPreviewView;
    private DeviceMotionDetector mMotionDetector;
    private Runnable mOnDetected;
    private FrameLayout mGraphicOverlay;
    @Nullable
    public UseCase mAnalysisUseCase;

    public CaptureCamera(Context context, LifecycleOwner lifecycleOwner) {
        this.mContext = context;
        this.mLifecycleOwner = lifecycleOwner;
    }

    public CaptureCamera setOnDetected(Runnable onDetected) {
        this.mOnDetected = onDetected;
        return this;
    }

    public void init(PreviewView previewView, FrameLayout graphicOverlay) {
        this.mPreviewView = previewView;
        this.mGraphicOverlay = graphicOverlay;
        previewView.setImplementationMode(PreviewView.ImplementationMode.PERFORMANCE);
    }

    public void startCamera(Observer<CameraState> observer) {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(mContext);
        cameraProviderFuture.addListener(() -> {
            if (mLifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
                return;
            }
            try {
                mCameraProvider = cameraProviderFuture.get();
                bindPreview();
                observeCameraState(observer);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(mContext));
    }

    void bindPreview() {
        CameraSelector cameraSelector = getCameraSelector();
        initImageCapture();
        bindToLifecycle(cameraSelector);
    }

    private void initImageCapture() {
        ImageCapture.Builder builder = new ImageCapture.Builder();
        int rotation = 0;
        if (mContext instanceof Activity) {
            rotation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();
        }
        mImageCapture = builder
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetRotation(rotation)
                .build();
    }

    private void bindToLifecycle(CameraSelector cameraSelector) {
        mCameraProvider.unbindAll();
        ArrayList<UseCase> useCases = new ArrayList<>();
        useCases.add(getPreview());
        useCases.add(mImageCapture);
        UseCase mlAnalysis = getMLAnalysis();
        if (mlAnalysis != null) {
            useCases.add(mlAnalysis);
        }
        mCamera = mCameraProvider.bindToLifecycle(
                mLifecycleOwner,
                cameraSelector,
                useCases.toArray(new UseCase[0])
        );
    }

    @NonNull
    private CameraSelector getCameraSelector() {
        return new CameraSelector.Builder()
                .requireLensFacing(mIsBackCamera ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT)
                .build();
    }

    @NonNull
    private Preview getPreview() {
        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
        return preview;
    }

    private ListenableFuture<FocusMeteringResult> startFocusAndMetering(float x, float y) {
        if (mCamera == null) {
            return null;
        }
        MeteringPoint point = mPreviewView.getMeteringPointFactory().createPoint(x, y);
        return mCamera.getCameraControl().startFocusAndMetering(
                new FocusMeteringAction.Builder(point)
                        .setAutoCancelDuration(2, TimeUnit.SECONDS)
                        .build());
    }

    public void switchCamera() {
        mIsBackCamera = !mIsBackCamera;
    }

    private UseCase getMLAnalysis() {
        UseCase searchAnalysis = getSearchAnalysis(mGraphicOverlay, () -> {
            if (mMotionDetector == null || !mMotionDetector.mStanding) {
                return false;
            }
            ListenableFuture<FocusMeteringResult> listenableFuture = startFocusAndMetering(0.5f, 0.5f);
            if (listenableFuture == null) {
                return false;
            }
            listenableFuture.addListener(() -> {
                if (mMotionDetector == null || !mMotionDetector.mStanding
                        || mGraphicOverlay.getVisibility() == View.GONE) {
                    return;
                }
                if (mOnDetected != null) {
                    mOnDetected.run();
                }
            }, ContextCompat.getMainExecutor(mContext));
            return true;
        });
        if (searchAnalysis == null) {
            return null;
        }
        mMotionDetector = new DeviceMotionDetector(mContext.getApplicationContext(), 1000, 1f);
        mMotionDetector.startMonitoring();
        return searchAnalysis;
    }

    public UseCase getSearchAnalysis(ViewGroup viewGroup,
                                     Callable<Boolean> runnable) {
        if (mAnalysisUseCase != null) {
            return mAnalysisUseCase;
        }
        try {
            SearchAnalysis searchAnalysis = new SearchAnalysis(viewGroup, runnable);
            mAnalysisUseCase = searchAnalysis.getUseCase();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return mAnalysisUseCase;
    }

    public void destroy() {
        if (mMotionDetector != null) {
            mMotionDetector.mMoveListeners.clear();
        }
    }

    private void observeCameraState(Observer<CameraState> observer) {
        if (observer == null) {
            return;
        }
        LiveData<CameraState> cameraState = mCamera.getCameraInfo().getCameraState();
        cameraState.observe(mLifecycleOwner, observer);
    }
}
