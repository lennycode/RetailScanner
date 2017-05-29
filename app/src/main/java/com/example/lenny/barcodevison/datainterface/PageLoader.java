package com.example.lenny.barcodevison.datainterface;

import android.util.Log;


import com.example.lenny.barcodevison.Config.Settings;
import com.example.lenny.barcodevison.MessageEvent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.util.ExceptionToResourceMapping;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class PageLoader {
    private static final String TAG = "APILoader";
    private static final String Spacer = "";
    NumberFormat formatter = NumberFormat.getCurrencyInstance();

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

    private RetailAPI buildClientrxA() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();


                Request request = new Request.Builder()

                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0")
                        .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,**/;q=0.8")
                        .addHeader("Accept-Language", "en-US,en;q=0.5")
                        .addHeader("DNT", "1")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Upgrade-Insecure-Requests", "1")
                        .build();

                Response response = chain.proceed(request);

                // Customize or return the response
                return response;
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(ScalarsConverterFactory.create())

                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Settings.amazonEndpoint)
                .build();

        return retrofit.create(RetailAPI.class);

    }

    private RetailAPI buildClientrxW(String endPoint) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Response response = null;
                try {

                    Request request = original.newBuilder()

                            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0")
                            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,**/;q=0.8")
                            .addHeader("Accept-Language", "en-US,en;q=0.5")
                            .addHeader("DNT", "1")
                            .addHeader("Connection", "keep-alive")
                            .addHeader("Upgrade-Insecure-Requests", "1")
                            .build();

                    response = chain.proceed(request);
                } catch (Exception e) {
                    Log.v("ds", "ds");
                }
                // Customize or return the response
                return response;
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(ScalarsConverterFactory.create())

                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(endPoint)
                .build();

        return retrofit.create(RetailAPI.class);

    }


    public void getWalmartInfo(String upcCode) {
        Pattern px = Pattern.compile("(\\{\"productId\".*?),\"preOrderAvailable\"");
        buildClientrxW(Settings.walmartEndpoint).loadWmProductPage(upcCode).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(s -> {
                            ObjectMapper srMapper;
                            Map<String, Object> map = new HashMap<String, Object>();
                            List<ProdBucket> allMatches = new ArrayList<ProdBucket>();
                            allMatches.add(new ProdBucket("Walmart"+ Spacer) );
                            try {
                                srMapper = new ObjectMapper();
                                Matcher tmpJson = px.matcher(s);
                                while (tmpJson.find()) {
                                    String json = tmpJson.group(1) + "}";

                                    allMatches.add(new ProdBucket(
                                            srMapper.readTree(json).path("title").toString().replace("\"",""),
                                            srMapper.readTree(json).path("primaryOffer").path("offerPrice").toString().replace("\"",""),
                                            srMapper.readTree(json).path("imageUrl").toString().replace("\"","")
                                    ));
                                    //map = srMapper.readValue(tmpJson.group(1)+"}", new TypeReference<HashMap<String, Object>>() {}
                                }


                                return  Observable.just(allMatches);


                            } catch (Exception e) {
                                return null;
                            }

                        }).subscribe(s -> {
                        EventBus.getDefault().post(new MessageEvent<List<ProdBucket>>((List<ProdBucket>) s));
                        Log.e(TAG, "Success");
                    });


// JSOUP fails on this page, bug listed
//                    Document doc = Jsoup.parse(s);
//                    try {
//                        //Elements frame = doc.select("div.s-item-container");
//                       // Elements frame = doc.select("li.celwidget");
//                        Elements frame = doc.select("div").attr("data-tl-id","ProductTileListView-0");
//                        //frame.get(0).select("span").attr("aria-label")
//                        String desc = frame.get(0).select("h2").text();
//                        //String price = frame.get(0).select("span").attr("aria-label");
//                        String price =    findLowest(frame);
//                        String image = frame.get(0).select("img").attr("src");
//                        return Observable.just(new ProdBucket(desc, price, image));
//                    }catch (Exception e){
//                        return Observable.just(new ProdBucket(null, null, null));
//                    }
                    //    } )


                }

    //http://www.bestbuy.com/site/searchpage.jsp?st=703113017230&_dyncharset=UTF-8&id=pcat17071&type=page&sc=Global&cp=1&nrp=&sp=&qp=&list=n&af=true&iht=y&usc=All+Categories&ks=960&keys=keys

    public void getBBInfo(String upcCode) {

        Map<String, String> paramMap = new HashMap<String,String>();
        paramMap.put("st",upcCode);
        paramMap.put("_dyncharset","UTF-8");
        paramMap.put("id","pcat17071");
        paramMap.put("type","page");
        paramMap.put("sc","Global");
        paramMap.put("cp","1");

        paramMap.put("sp","");
        paramMap.put("qp","");
        paramMap.put("list","n");
        paramMap.put("af","true");
        paramMap.put("iht","y");
        paramMap.put("usc","All+Categories");
        paramMap.put("ks","960");
        paramMap.put("keys","keys");
        buildClientrxW(Settings.bestbuyEndpoint).loadBbProductPage( paramMap).subscribeOn(Schedulers.newThread())

                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(s -> {
                    Document doc = Jsoup.parse(s);
                    List<ProdBucket> allMatches = new ArrayList<ProdBucket>();
                    allMatches.add(new ProdBucket("Best Buy"+Spacer));
                    try {
                        //Elements frame = doc.select("div.s-item-container");
                        Elements frame =  doc.select("div.list-items") ;
                        int elCount= frame.size();

                        for (int i=0; i<elCount;i++) {

                            String desc = frame.select("div.list-item").get(i).select("h4").text();

                            String price ="$"+frame.select("div.pb-purchase-price").select("span").get(0).attr("aria-label").replace("Your price for this item is ","");

                            String image = frame.select("div.thumb").select("img").attr("data-src").split(";")[0];
                            allMatches.add(new ProdBucket(
                                    desc,
                                    price,
                                    image
                            ));

                        }
                        return Observable.just(allMatches);
                    } catch (Exception e) {
                        return Observable.just(new ProdBucket(null, null, null));
                    }
                })
                .subscribe(s -> {
                    EventBus.getDefault().post(new MessageEvent<List<ProdBucket>>((List<ProdBucket>) s));
                    Log.e(TAG, "Success");
                });


    }

    public void getNEInfo(String upcCode) {

        buildClientrxW(Settings.neweggEndpoint).loadNeProductPage("ENE",0,"BESTMATCH", upcCode,"-1",1).subscribeOn(Schedulers.newThread())

                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(s -> {
                    Document doc = Jsoup.parse(s);
                    List<ProdBucket> allMatches = new ArrayList<ProdBucket>();
                    allMatches.add(new ProdBucket("NewEgg"+Spacer));
                    try {
                        //Elements frame = doc.select("div.s-item-container");
                        Elements frame = doc.select("div.items-view").select("div.item-container");
                        int elCount= frame.size();

                        for (int i=0; i<elCount;i++) {
                            //frame.get(0).select("span").attr("aria-label")
                            String desc = frame.select("div.item-container").get(i).select("a.item-title").text();
                            //String price = frame.get(0).select("span").attr("aria-label");
                            String price = frame.select("div.item-container").get(i).select("li.price-current").text();
                            if(frame.select("div.item-container").get(i).select("li.price-ship").text().trim() != ""){
                                price += frame.select("div.item-container").get(i).select("li.price-ship").text().trim();
                            }
                            String image = "https:"+frame.select("div.item-container").get(i).select("img").attr("src");
                            allMatches.add(new ProdBucket(
                                    desc,
                                    price,
                                    image
                            ));

                        }
                        return Observable.just(allMatches);
                    } catch (Exception e) {
                        return Observable.just(new ProdBucket(null, null, null));
                    }
                })
                .subscribe(s -> {
                    EventBus.getDefault().post(new MessageEvent<List<ProdBucket>>((List<ProdBucket>) s));
                    Log.e(TAG, "Success");
                });


    }


    public void getAmazonInfo(String upcCode) {

        buildClientrxW(Settings.amazonEndpoint).loadProductPage("search-alias=aps", upcCode).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(s -> {
                    Document doc = Jsoup.parse(s);
                    List<ProdBucket> allMatches = new ArrayList<ProdBucket>();
                    allMatches.add(new ProdBucket("Amazon"+Spacer));
                    try {
                        //Elements frame = doc.select("div.s-item-container");
                        Elements frame = doc.select("li.celwidget");
                        int elCount= frame.size();

                        for (int i=0; i<elCount;i++) {
                            //frame.get(0).select("span").attr("aria-label")
                            String desc = frame.get(i).select("h2").text();
                            //String price = frame.get(0).select("span").attr("aria-label");
                            String price = "$"+getAmazonPrice(frame.get(i));
                            String image = frame.get(i). select("img").attr("src");
                            allMatches.add(new ProdBucket(
                                    desc,
                                    price,
                                    image
                            ));

                        }
                        return Observable.just(allMatches);
                    } catch (Exception e) {
                        return Observable.just(new ProdBucket(null, null, null));
                    }
                })
                .subscribe(s -> {
                    EventBus.getDefault().post(new MessageEvent<List<ProdBucket>>((List<ProdBucket>) s));
                    Log.e(TAG, "Success");
                });


    }

    String getAmazonPrice(Element  els) {
        String lowestPrice;


        //List<Float> slist = new ArrayList<>();
       // for (Element els : elements) {
            String conditonalPrice = (els.select("span").attr("aria-label").replace("$", ""));
            if (conditonalPrice != "") {
                return ( ((conditonalPrice.contains("-")) ? conditonalPrice.split("-")[0] : conditonalPrice));
            } else if (els.select("td.a-text-left").select("span.a-size-small").text().replace("$", "") != "") {
                String backupPrice = els.select("td.a-text-left").select("span.a-size-small").text().replace("$", "");
                return ((backupPrice.contains("-")) ? backupPrice.split("-")[0] : backupPrice);
            } else if (els.select("span.a-size-base").select(".a-color-base").text().replace("$", "") != "") {
                String backupPrice = els.select("span.a-size-base").select(".a-color-base").text().replace("$", "").trim();
                if (backupPrice.contains(" ")) {//New price
                    backupPrice = backupPrice.split(" ")[0];
                }//Used price?

               return ((backupPrice.contains("-")) ? backupPrice.split("-")[0] : backupPrice);
            }
        //}

        //Collections.sort(slist);
        return ("$0.00");

        //return formatter.format(slist.get(0));

    }


}