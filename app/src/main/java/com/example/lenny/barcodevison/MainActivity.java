package com.example.lenny.barcodevison;

/**
 * Created by lenny on 4/7/17.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static com.google.zxing.BarcodeFormat.UPC_A;

public class MainActivity extends Activity {
    TextView barcoderesult;
    ImageView imageView;
    Bitmap bitmap = null;
    Pattern upcPattern = Pattern.compile("^\\d{12}");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        barcoderesult = (TextView) findViewById(R.id.barcode_result);
        imageView = (ImageView) findViewById(R.id.iv);

    }

    public void scanBacode(View v) {
        barcoderesult.setText("");
        imageView.setImageBitmap(null);
        Intent intent = new Intent(this, ScanBarcodeActivity.class);
        startActivityForResult(intent, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    barcoderesult.setText(barcode.displayValue);
                    try {
                        MediaPlayer ring= MediaPlayer.create(MainActivity.this,R.raw.scan);
                        ring.start();
                        bitmap = encodeAsBitmap(barcode.displayValue, BarcodeFormat.UPC_A, 600, 300);
                        imageView.setImageBitmap(bitmap);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                } else {
                    barcoderesult.setText("No Barcode Found");
                }
            }
        }
    }

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents.trim();
        if (upcPattern.matcher(contentsToEncode).find()) {
            Toast.makeText(this, "Please Scan a Valid UPC Code", Toast.LENGTH_LONG).show();
            return null;
        }
        Map<EncodeHintType, Object> hints = null;

        //Only UPC_A for now (you can create a routine to check length, numeric/text, etc to guess barcode type).
        hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, UPC_A);

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            Toast.makeText(this, "UPC Graphic Failed", Toast.LENGTH_LONG).show();
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


}
