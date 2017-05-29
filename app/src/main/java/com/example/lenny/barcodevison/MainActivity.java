package com.example.lenny.barcodevison;

/**
 * Created by lenny on 4/7/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenny.barcodevison.datainterface.PageLoader;
import com.example.lenny.barcodevison.datainterface.ProdBucket;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static com.google.zxing.BarcodeFormat.UPC_A;

public class MainActivity extends Activity {
    @BindView(R.id.barcode_result) TextView barcodeResult;
     @BindView(R.id.iv) ImageView imageBarcode;
    RecyclerView.Adapter myAdapter = null;
    @BindView(R.id.lstResult)RecyclerView resultList;
    List<ProdBucket> prodList = new ArrayList<>();
    Bitmap bitmap = null;
    Pattern upcPattern = Pattern.compile("^\\d{12}");
    RecyclerView.LayoutManager layoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        barcodeResult = (TextView) findViewById(R.id.barcode_result);
        imageBarcode = (ImageView) findViewById(R.id.iv);
        resultList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);


        myAdapter = new ResultAdapter(this, prodList );
        resultList.setAdapter(myAdapter);

        resultList.setLayoutManager(layoutManager);
        //new PageLoader().getWalmartInfo("031604016173");
        //new PageLoader().getAmazonInfo("031604016173");
         //new PageLoader().getWalmartInfo("722868829905");
         //new PageLoader().getAmazonInfo("722868829905");
        new PageLoader().getBBInfo("703113017230");
    }

    public void scanBacode(View v) {
        barcodeResult.setText("");
        imageBarcode.setImageBitmap(null);
        Intent intent = new Intent(this, ScanBarcodeActivity.class);
        startActivityForResult(intent, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    barcodeResult.setText(barcode.displayValue);
                    try {
                        MediaPlayer ring= MediaPlayer.create(MainActivity.this,R.raw.scan);
                        ring.start();
                        bitmap = encodeAsBitmap(barcode.displayValue, BarcodeFormat.UPC_A, 600, 300);
                        imageBarcode.setImageBitmap(bitmap);
                        new PageLoader().getAmazonInfo(barcode.displayValue);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                } else {
                    barcodeResult.setText("No Barcode Found");
                }
            }
        }
    }

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents.trim();
        if (!upcPattern.matcher(contentsToEncode).find()) {
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onMessageEvent(MessageEvent event) {

             for (ProdBucket p : (ArrayList<ProdBucket>) event.packet) {
                 prodList.add(p);

         }
         myAdapter.notifyDataSetChanged();


//        if (((ProdBucket)event.packet).itemDescription != null) {
////            itemDescription.setText( ((ProdBucket)event.packet).itemDescription);
////            itemPrice.setText(((ProdBucket)event.packet).itemPrice);
////            Picasso.with(this)
////                    .load(((ProdBucket) event.packet).itemPic)
////                    .resize(400, 400)
////                    .into(itemPic);
//        }else{
//            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
//        }

    }



    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {

        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
