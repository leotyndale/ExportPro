package com.alibaba.imagesearch.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

public class DeviceMotionDetector implements SensorEventListener {
    public final List<DeviceMotionListener> mMoveListeners;
    private final SensorManager mSensorManager;
    private final Sensor mAccessSensor;
    private final Sensor mMagneticSensor;
    private long mLastEvent = -1;
    private float[] mAcceleration;
    private final float[] mOrientation;
    float[] mAccessDiff = null;
    float[] mOrieDiff = null;

    public boolean mStanding = false;
    private static final float MOVING_ACCESS = 0.3f;
    private static final float STABLE_ACCESS = 0.1f;
    private static final float ORIENTATION = 10f;
    private final long mStandingSlop;
    private final float mScale;
    public volatile boolean mIsDetecting;

    public DeviceMotionDetector(Context context, long slop, float scale) {
        mScale = scale;
        mStandingSlop = slop;
        mMoveListeners = new ArrayList<>();

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccessSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mOrientation = new float[3];
        mAcceleration = new float[3];
    }

    public void startMonitoring() {
        try {
            mLastEvent = System.currentTimeMillis();
            mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mAccessSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mIsDetecting = true;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            if (!mIsDetecting) return;
            boolean isStable, isMoved;
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    float[] lastAccessValues = new float[]{mAcceleration[0], mAcceleration[1], mAcceleration[2]};
                    mAcceleration = new float[]{event.values[0], event.values[1], event.values[2]};
                    mAccessDiff = new float[]{mAcceleration[0] - lastAccessValues[0], mAcceleration[1] - lastAccessValues[1], mAcceleration[2] - lastAccessValues[2]};
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    float[] lastOrientationValues = new float[]{mOrientation[0], mOrientation[1], mOrientation[2]};
                    float[] r = new float[9];
                    SensorManager.getRotationMatrix(r, null, mAcceleration, event.values);
                    SensorManager.getOrientation(r, mOrientation);
                    mOrieDiff = new float[]{(float) Math.toDegrees(mOrientation[0] - lastOrientationValues[0]),
                            (float) Math.toDegrees(mOrientation[1] - lastOrientationValues[1]),
                            (float) Math.toDegrees(mOrientation[2] - lastOrientationValues[2])};
                    break;
            }
            isStable = isStable(mAccessDiff);
            isMoved = isMoved(mAccessDiff, mOrieDiff);

            if (isMoved) {
                notifyDeviceMoving();
                mLastEvent = System.currentTimeMillis();
            } else {
                final long silentTime = Math.abs(System.currentTimeMillis() - mLastEvent);
                if (silentTime >= mStandingSlop && mLastEvent != -1) {
                    if (isStable) {
                        notifyDeviceStanding();
                    }
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private boolean isStable(float[] accelerationDiff) {
        if (accelerationDiff == null) {
            return false;
        }
        float xDiff = Math.abs(accelerationDiff[0]);
        float yDiff = Math.abs(accelerationDiff[1]);
        float zDiff = Math.abs(accelerationDiff[2]);
        float maxDiff = Math.max(Math.max(xDiff, yDiff), zDiff);
        return maxDiff < STABLE_ACCESS * mScale;
    }

    private boolean isMoved(float[] accelerationDiff, float[] orientationDiff) {
        if (accelerationDiff == null || orientationDiff == null) {
            return false;
        }
        float xDiff = Math.abs(accelerationDiff[0]);
        float yDiff = Math.abs(accelerationDiff[1]);
        float zDiff = Math.abs(accelerationDiff[2]);
        float maxAccessDiff = Math.max(Math.max(xDiff, yDiff), zDiff);

        float azimuth = Math.abs(orientationDiff[0]);
        float pitch = Math.abs(orientationDiff[1]);
        float roll = Math.abs(orientationDiff[2]);
        float maxOrieDiff = Math.max(Math.max(azimuth, pitch), roll);
        return maxAccessDiff > MOVING_ACCESS * mScale || maxOrieDiff > ORIENTATION * mScale;
    }

    private void notifyDeviceMoving() {
        mStanding = false;
        for (DeviceMotionListener listener : mMoveListeners) {
            if (listener != null) {
                listener.onDeviceMove();
            }
        }
    }

    private void notifyDeviceStanding() {
        if (mStanding) {
            return;
        }
        mStanding = true;
        for (DeviceMotionListener listener : mMoveListeners) {
            if (listener != null) {
                listener.onDeviceStand();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
