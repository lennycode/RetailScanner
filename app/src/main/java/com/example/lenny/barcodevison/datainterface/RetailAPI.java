package com.example.lenny.barcodevison.datainterface;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface RetailAPI {
    //http://grocerygetter.x10host.com/vehendpoint.php?mode=years
    //Leading slash resolves to root domain
    @GET("s/ref=nb_sb_ss_rsis_1_0")
    Observable<String> loadProductPage(@Query("url") String url,@Query("field-keywords") String upc );

}