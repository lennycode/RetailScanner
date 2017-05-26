package com.example.lenny.barcodevison;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.lang.reflect.Field;

import static com.google.android.gms.vision.barcode.Barcode.DRIVER_LICENSE;
import static com.google.android.gms.vision.barcode.Barcode.EAN_13;
import static com.google.android.gms.vision.barcode.Barcode.PRODUCT;
import static com.google.android.gms.vision.barcode.Barcode.UPC_A;
import static com.google.android.gms.vision.barcode.Barcode.UPC_E;

/**
 * Created by lenny on 4/7/17.
 */

public class ScanBarcodeActivity extends Activity {

    SurfaceView cameraPreview;
    CameraSource cameraSource;
    CameraManager camManager;
    Button buttonFlash;
    private Camera cam;
    private boolean isFlashOn = false;
    private boolean hasFlash;

    //Kudos:
    //https://stackoverflow.com/questions/35811411/accessing-autofocus-flash-with-google-vision-barcode-reader
    //You aren't allowed to open a new camera instance, but we get the cameraSource's camera instance to work with.
    private static Camera getCamera(@NonNull CameraSource cameraSource) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        return camera;
                    }
                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
        buttonFlash = (Button) findViewById(R.id.btnFlash);

        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {
            buttonFlash.setVisibility(View.INVISIBLE);
        } else {

        }

        buttonFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isFlashOn) {
                    flashLightOn();
                    buttonFlash.setText("FLASH\nOFF");
                } else {
                    flashLightOff();
                    buttonFlash.setText("FLASH\nON");
                }

            }
        });
        createCameraSource();
    }

    private void createCameraSource() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(UPC_A|UPC_E|EAN_13).build();

        cameraSource  = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .setFacing(CameraSource.CAMERA_FACING_BACK).build() ;



        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {
                    if (ActivityCompat.checkSelfPermission(ScanBarcodeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ScanBarcodeActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                1);
                        return;
                    }
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {

                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("barcode", barcodes.valueAt(0));
                    setResult(CommonStatusCodes.SUCCESS, intent);
                    finish();

                }
            }
        });
    }

    public void flashLightOn() {

        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam = getCamera(cameraSource);

                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                isFlashOn = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Unable to Light Flash!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void flashLightOff() {
        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam = getCamera(cameraSource);
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                cam.setParameters(p);
                isFlashOn = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Unable To Extinguish Flash",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

