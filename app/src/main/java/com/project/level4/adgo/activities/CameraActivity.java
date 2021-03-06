package com.project.level4.adgo.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.project.level4.adgo.R;
import com.project.level4.adgo.utils.Advertisement;
import com.project.level4.adgo.utils.DeviceOrientation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraActivity extends Activity {
    private static final String TAG = "AndroidCameraApi";

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private TextureView textureView;
    private ImageView adListing;
    private ImageView placeholderQR;
    private TextView adInformation;
    private String cameraId;
    private ImageView adOwner;

    private DialogInterface.OnCancelListener mOnCancelListener;


    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;

    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;

    private Toolbar toolbar;

    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    private android.support.v7.app.AlertDialog.Builder dialog1;
    private android.support.v7.app.AlertDialog.Builder dialog2;
    private LayoutInflater factory;

    private AlertDialog visibleDialog;

    private DeviceOrientation deviceOrientation;
    private boolean active;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        dialog1 = new android.support.v7.app.AlertDialog.Builder(this);
        dialog2 = new android.support.v7.app.AlertDialog.Builder(this);
        factory = LayoutInflater.from(this);

//
        adInformation = (TextView) findViewById(R.id.camera_ad_info);
        adOwner = (ImageView) findViewById(R.id.camera_ad_owner);
        placeholderQR = (ImageView) findViewById(R.id.qr_code_placeholder_reward);

        toolbar.setNavigationIcon(resize(getResources().getDrawable(R.drawable.app_icon, null), 100, 100));
        toolbar.setTitle("Point at the ad with this alien");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        placeholderQR.setImageDrawable(getResources().getDrawable(R.drawable.qrcodeicon, null));
        adOwner.setImageDrawable(getResources().getDrawable(R.drawable.nikeicon, null));
//
        adInformation.setText("Reward: 20% Discount in Store\n\n\n Prize: £0.05");

        textureView = (TextureView) findViewById(R.id.texture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);

        deviceOrientation = new DeviceOrientation(this);
        active = true;
    }

    public void orientationSuccess(){
        launchRingDialog(textureView);
    }

    private Drawable resize(Drawable image, int xSize, int ySize) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, xSize, ySize, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    public void launchRingDialog(View view) {
        final int sec = 10;
        // hard coded adverstisement
        final Advertisement ad = new Advertisement("nike", getResources().getDrawable(R.drawable.nikeicon, null), "20% Discount in store", getResources().getDrawable(R.drawable.qrcodeicon, null), 0.05);

        final View dialogView = factory.inflate(R.layout.dialog_countdown, null);

        dialog1.setView(dialogView);

        mOnCancelListener = new DialogInterface.OnCancelListener() {

            CountDownTimer countDownTimer = new CountDownTimer(sec * 1000, 300) {
                // 500 means, onTick function will be called at every 500 milliseconds

                ProgressBar progressBar = (ProgressBar) dialogView.findViewById(R.id.circular_progress_bar) ;
                TextView countdown = (TextView) dialogView.findViewById(R.id.countdown);
                TextView countdownMessage = (TextView) dialogView.findViewById(R.id.countdown_message);


                @Override
                public void onTick(long leftTimeInMilliseconds) {
                    long seconds = leftTimeInMilliseconds / 1000;
                    progressBar.setProgress((int)seconds);
                    countdownMessage.setText("Remaining Time to Win: ");
                    countdown.setText(Long.toString(seconds) + " seconds");
                }
                @Override
                public void onFinish() {
                    if(countdown.getText().equals("0 seconds")){

                        View successDialogView = factory.inflate(R.layout.dialog_success, null);

                        dialog2.setView(successDialogView);

                        TextView successMessage = (TextView) successDialogView.findViewById(R.id.success_message);
                        TextView congratulationsMessage = (TextView) successDialogView.findViewById(R.id.congratulations_message);
                        ImageView tickIcon = (ImageView) successDialogView.findViewById(R.id.complete_image);

                        tickIcon.setImageDrawable(resize(getResources().getDrawable(R.drawable.green_tick_icon, null), 300, 300));
                        congratulationsMessage.setText("Congratulations!");
                        successMessage.setText("Your bag has been updated with rewards");

                        mOnCancelListener = new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("ad", ad.getAdOwner());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        };
                        dialog2.setOnCancelListener(mOnCancelListener);
                    }
                    visibleDialog.dismiss();
                    visibleDialog = dialog2.create();
                    if (active && !visibleDialog.isShowing()) visibleDialog.show();
                }
            }.start();

            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                countDownTimer.cancel();
                if (deviceOrientation != null) {
                    deviceOrientation.setSuccess(false);
                }
            }

        };
        dialog1.setOnCancelListener(mOnCancelListener);
        visibleDialog = dialog1.create();
        if (active && !visibleDialog.isShowing()) visibleDialog.show();
    }

    public void orientationFailure(){
        if (visibleDialog != null){
            visibleDialog.cancel();
            if (deviceOrientation != null) deviceOrientation.setSuccess(false);
        }
    }


    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };


    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(CameraActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    protected void updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(CameraActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        active = true;
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        active = false;
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    protected void onStop() {
        active = false;
        deviceOrientation = null;
        super.onStop();
    }

    @Override
    protected void onStart() {
        active = true;
        super.onStart();
    }
}