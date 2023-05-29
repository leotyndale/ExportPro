package com.alibaba.imagesearch;

import static android.Manifest.permission.CAMERA;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.alibaba.base.constants.IntentKey;
import com.alibaba.imagesearch.camera.CaptureCamera;
import com.alibaba.imagesearch.searchml.overlay.ObjectGraphic;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;

public class ImageSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private static final AudioAttributes VIBRATION_ATTRIBUTES = new AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .build();
    private final CaptureCamera mCaptureCamera = new CaptureCamera(this, this);
    final private int REQUEST_CODE_ASK_PERMISSIONS = 124;
    private boolean mIsCameraStated;
    private OnPermissionResultListener mPermissionListener;
    private String[] mPermissionActivityResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_image_search);
        initView();
        checkCameraPermission(null);
    }

    private void initView() {
        ImmersionBar.with(this).transparentStatusBar().init();
        mCaptureCamera.init(
                findViewById(R.id.previewView),
                findViewById(R.id.graphic_overlay)
        );
        findViewById(R.id.close).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptureCamera.destroy();
    }

    public void switchCamera(View view) {
        checkCameraPermission(() -> {
            mCaptureCamera.switchCamera();
            mIsCameraStated = false;
        });
    }

    /**
     * camera权限检查
     */
    public void checkCameraPermission(Runnable onSuccess) {
        checkPermission(new OnPermissionResultListener() {
            @Override
            public void onSucceed(String[] permission) {
                if (onSuccess != null) {
                    onSuccess.run();
                }
                startCamera();
            }

            @Override
            public void onFailed(String[] permission) {
                mIsCameraStated = false;
                startCamera();
            }

            @Override
            public void onNotAskAgain(String[] permission) {
                mIsCameraStated = false;
                startCamera();
            }
        }, CAMERA);
    }

    private void startCamera() {
        if (mIsCameraStated) {
            return;
        }
        mIsCameraStated = true;
        mCaptureCamera.setOnDetected(this::searchImage).startCamera(null);
    }

    private void searchImage() {
        String text = ObjectGraphic.mText;
        if (TextUtils.isEmpty(text)) {
            return;
        }
        Vibrator vib = (Vibrator) getSystemService(
                Context.VIBRATOR_SERVICE);
        vib.vibrate(50, VIBRATION_ATTRIBUTES);
        setResult(RESULT_OK, new Intent().putExtra(IntentKey.KEYWORD, text));
        finish();
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.close) {
            finish();
        }
    }

    public boolean checkPermission(OnPermissionResultListener listener, String... permissions) {
        this.mPermissionListener = listener;

        if (permissions.length == 0) {
            listener.onFailed(permissions);
            return false;
        }

        ArrayList<String> notAllowedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                notAllowedPermissions.add(permission);
            }
        }
        if (notAllowedPermissions.isEmpty()) {
            listener.onSucceed(permissions);
            return true;
        }
        String[] strings = notAllowedPermissions.toArray(new String[0]);
        ActivityCompat.requestPermissions(this, strings, REQUEST_CODE_ASK_PERMISSIONS);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CODE_ASK_PERMISSIONS) {
            return;
        }
        if (isDestroyed()) {
            return;
        }
        boolean isAllSuccess = true;
        boolean isAllNotAskAgain = true;
        if (mPermissionListener != null) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isAllSuccess = false;
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        isAllNotAskAgain = false;
                        break;
                    }
                }
            }

            if (isDestroyed()) {
                return;
            }

            if (isAllSuccess) {
                mPermissionListener.onSucceed(permissions);
            } else if (isAllNotAskAgain) {
                mPermissionListener.onNotAskAgain(permissions);
            } else {
                mPermissionListener.onFailed(permissions);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_ASK_PERMISSIONS) {
            return;
        }
        String[] permissionActivityResult = this.mPermissionActivityResult;
        if (permissionActivityResult == null || mPermissionListener == null) {
            return;
        }
        boolean isAllSuccess = true;
        for (String aMPermissionActivityResult : permissionActivityResult) {
            if (ContextCompat.checkSelfPermission(ImageSearchActivity.this, aMPermissionActivityResult)
                    != PackageManager.PERMISSION_GRANTED) {
                isAllSuccess = false;
                break;
            }
        }
        if (isAllSuccess) {
            mPermissionListener.onSucceed(permissionActivityResult);
        } else {
            mPermissionListener.onFailed(permissionActivityResult);
        }
        mPermissionActivityResult = null;
    }
}

