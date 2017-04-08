package com.example.lenny.barcodevison;

/**
 * Created by lenny on 4/7/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class MainActivity extends  Activity {
    TextView barcoderesult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView (R.layout.activity_main);
        barcoderesult = (TextView) findViewById(R.id.barcode_result);
    }


    public void scanBacode(View v){
        Intent intent = new Intent(this, ScanBarcodeActivity.class);
        startActivityForResult(intent, 0);

    }

    @Override
    protected void  onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 0){
            if (resultCode == CommonStatusCodes.SUCCESS){
                if (data!=null){
                    Barcode barcode =  data.getParcelableExtra("barcode");
                    barcoderesult.setText(barcode.displayValue);

                } else{
                    barcoderesult.setText("No Barcode Found");
                }
            }
        }
    }






}
