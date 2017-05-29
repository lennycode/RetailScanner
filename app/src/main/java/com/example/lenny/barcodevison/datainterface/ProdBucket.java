package com.example.lenny.barcodevison.datainterface;

import android.support.annotation.NonNull;

/**
 * Created by lenny on 5/25/17.
 */

public class ProdBucket implements Comparable<ProdBucket>{
    public String itemDescription= null;
    public String itemPrice = null;
    public String itemPic = null;
    public Boolean isDivider = false;

    public ProdBucket(String itemDescription, String itemPrice, String itemPic) {
        this.itemDescription = itemDescription;
        this.itemPrice = itemPrice;
        this.itemPic = itemPic;
    }

    public ProdBucket(String itemDescription) {
        this.itemDescription = itemDescription;
        this.isDivider = true;

    }
    public float getFloatPrice(){
        return Float.parseFloat(this.itemPrice.trim().replace("$",""));
    }

    @Override
    public int compareTo(@NonNull ProdBucket prodBucket) {
        try{
           return Float.parseFloat(this.itemPrice.trim().replace("$","")) < getFloatPrice() ? 1: Float.parseFloat(this.itemPrice.trim().replace("$","")) > getFloatPrice() ? -1 : 0 ;
        }catch (Exception e){
            return 0;
        }
    }
}
