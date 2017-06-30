package com.example.android.popmovies;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.android.popmovies.api.ApiClient;
import com.example.android.popmovies.api.ApiInterface;
import com.google.gson.Gson;

import java.util.ArrayList;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieClickListener, SwipeRefreshLayout.OnRefreshListener {

    private MoviesAdapter mAdapter;
    private ArrayList<ResultDao> movieList = new ArrayList<>();
    private MovieDao movies;
    private SwipeRefreshLayout swipeContainer;
    public static final String SAVE_INSTANCE_MOVIES = "movies";
    public static final String SAVE_INSTANCE_SORT = "sort";
    public static final String MOST_POPULAR = "mostpopular";
    public static final String TOP_RATED = "toprated";
    private String sort = MOST_POPULAR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(this);

        if (savedInstanceState != null) {
            movieList = savedInstanceState.getParcelableArrayList(SAVE_INSTANCE_MOVIES);
            sort = savedInstanceState.getString(SAVE_INSTANCE_SORT,MOST_POPULAR);
        } else {
            getPopularMovies();
        }

        postponeEnterTransition();

        RecyclerView recyclerMovies = (RecyclerView) findViewById(R.id.rv_movies);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerMovies.setLayoutManager(layoutManager);
        recyclerMovies.setHasFixedSize(true);
        mAdapter = new MoviesAdapter(this, movieList, this);
        recyclerMovies.setAdapter(mAdapter);
    }

    private void getPopularMovies() {
        ApiInterface apiInterface = ApiClient.provideApiInterface();
        apiInterface.getPopularMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieDao>() {
                    @Override
                    public void onCompleted() {
                        stopRefreshing();
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopRefreshing();
                    }

                    @Override
                    public void onNext(MovieDao movieDao) {
                        movies = movieDao;
                        refreshAdapter();
                    }
                });
    }

    public void getTopRatedMovies() {
        ApiInterface apiInterface = ApiClient.provideApiInterface();
        apiInterface.getTopRatedMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieDao>() {
                    @Override
                    public void onCompleted() {
                        stopRefreshing();
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopRefreshing();
                    }

                    @Override
                    public void onNext(MovieDao movieDao) {
                        movies = movieDao;
                        refreshAdapter();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_most_popular:
                sort = MOST_POPULAR;
                getPopularMovies();
                break;
            case R.id.action_top_rated:
                sort = TOP_RATED;
                getTopRatedMovies();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(SAVE_INSTANCE_MOVIES, movieList);
        outState.putString(SAVE_INSTANCE_SORT, sort);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(int position, ImageView ivMovie) {
        DetailActivity.start(this,movieList.get(position),ivMovie);
    }

    public void refreshAdapter(){
        movieList.clear();
        movieList.addAll(movies.getResults());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        if (sort.equals(MOST_POPULAR)) {
            getPopularMovies();
        } else {
            getTopRatedMovies();
        }
    }

    public void stopRefreshing(){
        swipeContainer.setRefreshing(false);
    }
}
