package com.example.android.popmovies.api;

import com.example.android.popmovies.BuildConfig;
import com.example.android.popmovies.model.MovieResponse;
import com.example.android.popmovies.model.ReviewsResponse;
import com.example.android.popmovies.model.TrailersResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by arifinbardansyah on 6/29/17.
 */

public interface ApiInterface {

    @GET("3/movie/popular?api_key="+ BuildConfig.MovieDBApiKey)
    Observable<MovieResponse> getPopularMovies();

    @GET("3/movie/top_rated?api_key="+ BuildConfig.MovieDBApiKey)
    Observable<MovieResponse> getTopRatedMovies();

    @GET("3/movie/{movie_id}/videos?api_key="+ BuildConfig.MovieDBApiKey)
    Observable<TrailersResponse> getMovies(
            @Path("movie_id") int movieId
    );

    @GET("3/movie/{movie_id}/reviews?api_key="+ BuildConfig.MovieDBApiKey)
    Observable<ReviewsResponse> getReviews(
            @Path("movie_id") int movieId
    );
}
