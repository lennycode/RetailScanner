package com.example.lenny.barcodevison.datainterface;


import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


public interface RetailAPI {
    //http://grocerygetter.x10host.com/vehendpoint.php?mode=years
    //Leading slash resolves to root domain
    @GET("s/ref=nb_sb_ss_rsis_1_0")
    Observable<String> loadProductPage(@Query("url") String url, @Query("field-keywords") String upc);


    @GET("search/")
    Observable<String> loadWmProductPage(@Query("query") String url);


    @GET("Product/ProductList.aspx")
    Observable<String> loadNeProductPage(@Query("Submit") String submit,
                                         @Query("DEPA") int depa,
                                         @Query("Order") String order,
                                         @Query("Description") String upc,
                                         @Query("N") String n,
                                         @Query("isNodeId") int i);

        //http://www.bestbuy.com/site/searchpage.jsp?st=703113017230&_dyncharset=UTF-8&id=pcat17071&type=page&sc=Global&cp=1&nrp=&sp=&qp=&list=n&af=true&iht=y&usc=All+Categories&ks=960&keys=keys
    @GET("site/searchpage.jsp")
    Observable<String> loadBbProductPage(@QueryMap Map<String, String> params);


//    Request URL:http://www.ebay.com/sch/i.html?_sacat=0&_nkw=722868829905&_sop=15
    @GET("sch/i.html?_sacat=0&_nkw={upc}&_sop=15")
    Observable<String> loadEbayProductPage(@Path("upc") String upc);


}