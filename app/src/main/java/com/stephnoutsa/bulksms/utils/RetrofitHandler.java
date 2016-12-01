package com.stephnoutsa.bulksms.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by stephnoutsa on 11/30/16.
 */

public class RetrofitHandler {

    // Trailing slash is needed
    private String BASE_URL = "http://192.168.43.170:8080/bulksms/webapi/"; // Localhost value is 10.0.2.2
    //private String BASE_URL = "http://10.0.2.2:8080/cuib/webapi/"; // Localhost value is 10.0.2.2

    private Retrofit retrofit ;

    public RetrofitHandler() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public RetrofitHandler(String BASE_URL) {
        this.BASE_URL = BASE_URL;

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public BulkSmsService create() {
        return retrofit.create(BulkSmsService.class);
    }

}
