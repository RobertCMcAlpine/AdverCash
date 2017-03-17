package com.project.level4.adgo.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.project.level4.adgo.activities.CameraActivity;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Rob on 3/17/17.
 */

public class DeviceOrientation implements SensorEventListener{
    private SensorManager sensorManager;
    private Context context;

    private boolean success;

    private float[] inR = new float[16];
    private float[] I = new float[16];
    private float[] gravity = new float[3];
    private float[] geomag = new float[3];
    private float[] orientVals = new float[3];

    private double azimuth = 0;
    private double pitch = 0;
    private double roll = 0;

    public DeviceOrientation(Context context){
        this.context = context;
        this.sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        this.success = false;
        registerListeners();

    }

    public void setSuccess(boolean success){
        this.success = success;
    }

    public void kill(){
        unregisterListeners();
    }

    private void registerListeners(){
        // Register this class as a listener for the accelerometer sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        // ...and the orientation sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterListeners(){
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // If the sensor data is unreliable return
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
            return;

        // Gets the value of the sensor that has been changed
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gravity = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomag = event.values.clone();
                break;
        }

        // If gravity and geomag have values then find rotation matrix
        if (gravity != null && geomag != null) {

            // checks that the rotation matrix is found
            boolean success = SensorManager.getRotationMatrix(inR, I,
                    gravity, geomag);
            if (success) {
                SensorManager.getOrientation(inR, orientVals);
                azimuth = Math.toDegrees(orientVals[0]);
                pitch = Math.toDegrees(orientVals[1]);
                roll = Math.toDegrees(orientVals[2]);
            }
        }

        if (pitch < -70 && !success){
            success = true;
            CameraActivity cameraActivity = (CameraActivity) context;
            if (cameraActivity != null) cameraActivity.orientationSuccess();
        }

        if (success && pitch > -70){
            CameraActivity cameraActivity = (CameraActivity) context;
            if (cameraActivity != null) cameraActivity.orientationFailure();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
