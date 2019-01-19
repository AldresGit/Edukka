package com.javier.edukka.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.1.42/edukka/";
    //private static final String BASE_URL = "https://edukka2.herokuapp.com/";
    private static RestInterface RETROFIT_CLIENT;

    public static RestInterface getInstance() {
        if (RETROFIT_CLIENT == null) {
            setupRestClient();
        }
        return RETROFIT_CLIENT;
    }

    private static void setupRestClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RETROFIT_CLIENT = retrofit.create(RestInterface.class);
    }
}