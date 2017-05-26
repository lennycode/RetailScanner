package com.example.lenny.barcodevison.datainterface;

import android.util.Log;


import com.example.lenny.barcodevison.Config.Settings;
import com.example.lenny.barcodevison.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class PageLoader {
    private static final String TAG = "APILoader";

    private RetailAPI buildClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //Removed, causing an exception.
        //httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Settings.amazonEndpoint)
                .build();

        return retrofit.create(RetailAPI.class);

    }
    private RetailAPI buildClientrx() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(ScalarsConverterFactory.create())

                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Settings.amazonEndpoint)
                .build();

        return retrofit.create(RetailAPI.class);

    }


    public void getAmazonInfo(String upcCode){

        buildClientrx().loadProductPage("search-alias=aps",upcCode).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(s->{
                    Document doc = Jsoup.parse(s);
                    try {
                        Elements frame = doc.select("div.s-item-container");
                        String desc = frame.get(0).select("h2").text();
                        String price = frame.get(0).select("span").attr("aria-label");
                        String image = frame.get(0).select("img").attr("src");
                        return Observable.just(new ProdBucket(desc, price, image));
                    }catch (Exception e){
                        return Observable.just(new ProdBucket(null, null, null));
                    }


                })
                .subscribe(s->{
                    EventBus.getDefault().post(new MessageEvent<ProdBucket>((ProdBucket ) s));
                    Log.e(TAG, "Success");
                }    );



    }




}