package com.example.android.popmovies.api;

import com.example.android.popmovies.BuildConfig;
import com.example.android.popmovies.MovieDao;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by arifinbardansyah on 6/29/17.
 */

public interface ApiInterface {
    @GET("3/movie/popular?api_key="+ BuildConfig.MovieDBApiKey)
    Observable<MovieDao> getPopularMovies();
    @GET("3/movie/top_rated?api_key="+BuildConfig.MovieDBApiKey)
    Observable<MovieDao> getTopRatedMovies();
}
