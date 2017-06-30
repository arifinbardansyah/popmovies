package com.example.android.popmovies.api;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by arifinbardansyah on 6/29/17.
 */

public class ApiClient {
    private static ApiInterface apiInterface = null;
    public static final String BASE_URL = "http://api.themoviedb.org/";
    public static final String BASE_URL_IMAGE = "http://image.tmdb.org/t/p/w185/";

    public static ApiInterface provideApiInterface() {
        if (apiInterface == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            apiInterface = retrofit.create(ApiInterface.class);
        }
        return apiInterface;
    }
}
