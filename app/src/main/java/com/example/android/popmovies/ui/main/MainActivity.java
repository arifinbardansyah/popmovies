package com.example.android.popmovies.ui.main;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.popmovies.R;
import com.example.android.popmovies.api.ApiClient;
import com.example.android.popmovies.api.ApiInterface;
import com.example.android.popmovies.db.FavoritesContract;
import com.example.android.popmovies.db.MoviesDbHelper;
import com.example.android.popmovies.model.Movie;
import com.example.android.popmovies.model.MovieResponse;
import com.example.android.popmovies.ui.detail.DetailActivity;
import com.example.android.popmovies.ui.main.MoviesAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements
        MoviesAdapter.MovieClickListener, SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FAVORITE_LOADER_ID = 0;

    private MoviesAdapter mAdapter;
    private ArrayList<Movie> movieList = new ArrayList<>();
    private SwipeRefreshLayout swipeContainer;
    public static final String SAVE_INSTANCE_MOVIES = "movies";
    public static final String SAVE_INSTANCE_SORT = "sort";
    public static final String MOST_POPULAR = "mostpopular";
    public static final String TOP_RATED = "toprated";
    public static final String FAVORITED = "favorited";
    private String sort = MOST_POPULAR;

    private Toast toast;

    private boolean INSTANCE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            movieList = savedInstanceState.getParcelableArrayList(SAVE_INSTANCE_MOVIES);
            sort = savedInstanceState.getString(SAVE_INSTANCE_SORT, MOST_POPULAR);
        } else {
            getPopularMovies();
        }

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(this);

        RecyclerView recyclerMovies = (RecyclerView) findViewById(R.id.rv_movies);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerMovies.setLayoutManager(layoutManager);
        recyclerMovies.setHasFixedSize(true);
        mAdapter = new MoviesAdapter(this, movieList, this);
        recyclerMovies.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }
    }

    private void getPopularMovies() {
        ApiInterface apiInterface = ApiClient.provideApiInterface();
        apiInterface.getPopularMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieResponse>() {
                    @Override
                    public void onCompleted() {
                        stopRefreshing();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(e.getMessage());
                        stopRefreshing();
                    }

                    @Override
                    public void onNext(MovieResponse movieResponse) {
                        refreshAdapter(movieResponse.getResults());
                    }
                });
    }

    public void getTopRatedMovies() {
        ApiInterface apiInterface = ApiClient.provideApiInterface();
        apiInterface.getTopRatedMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieResponse>() {
                    @Override
                    public void onCompleted() {
                        stopRefreshing();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(e.getMessage());
                        stopRefreshing();
                    }

                    @Override
                    public void onNext(MovieResponse movieResponse) {
                        refreshAdapter(movieResponse.getResults());
                    }
                });
    }

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
    }

    public void getFavorites() {
        if (!INSTANCE) {
            getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
            INSTANCE = true;
        } else {
            getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
        }
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
            case R.id.action_favorited:
                sort = FAVORITED;
                getFavorites();
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
        DetailActivity.start(this, movieList.get(position), ivMovie);
    }

    public void refreshAdapter(List<Movie> movie) {
        movieList.clear();
        movieList.addAll(movie);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        getCurrentMovies();
    }

    public void stopRefreshing() {
        swipeContainer.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentMovies();
    }

    public void getCurrentMovies(){
        switch (sort) {
            case MOST_POPULAR:
                getPopularMovies();
                break;
            case TOP_RATED:
                getTopRatedMovies();
                break;
            case FAVORITED:
                getFavorites();
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mFavoriteData = null;

            @Override
            protected void onStartLoading() {
                if (mFavoriteData != null) {
                    deliverResult(mFavoriteData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try{
                    return getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e){
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mFavoriteData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Movie> favoriteMovies = new ArrayList<>();
        for (int i = 0; i < data.getCount(); i++) {
            data.moveToPosition(i);
            Movie movie = new Movie();
            movie.setVote_count(data.getInt(data.getColumnIndex(FavoritesContract.FavoritesEntry.VOTE_COUNT)));
            movie.setId(data.getInt(data.getColumnIndex(FavoritesContract.FavoritesEntry._ID)));
            movie.setVideo(data.getInt(data.getColumnIndex(FavoritesContract.FavoritesEntry.VIDEO)) == 1);
            movie.setVote_average(data.getDouble(data.getColumnIndex(FavoritesContract.FavoritesEntry.VOTE_AVERAGE)));
            movie.setTitle(data.getString(data.getColumnIndex(FavoritesContract.FavoritesEntry.TITLE)));
            movie.setPopularity(data.getDouble(data.getColumnIndex(FavoritesContract.FavoritesEntry.POPULARITY)));
            movie.setPoster_path(data.getString(data.getColumnIndex(FavoritesContract.FavoritesEntry.POSTER_PATH)));
            movie.setOriginal_language(data.getString(data.getColumnIndex(FavoritesContract.FavoritesEntry.ORIGINAL_LANGUAGE)));
            movie.setOriginal_title(data.getString(data.getColumnIndex(FavoritesContract.FavoritesEntry.ORIGINAL_TITLE)));
            movie.setBackdrop_path(data.getString(data.getColumnIndex(FavoritesContract.FavoritesEntry.BACKDROP_PATH)));
            movie.setAdult(data.getInt(data.getColumnIndex(FavoritesContract.FavoritesEntry.VOTE_AVERAGE)) == 1);
            movie.setOverview(data.getString(data.getColumnIndex(FavoritesContract.FavoritesEntry.OVERVIEW)));
            movie.setRelease_date(data.getString(data.getColumnIndex(FavoritesContract.FavoritesEntry.RELEASE_DATE)));
            favoriteMovies.add(movie);
        }
//        data.close();
        refreshAdapter(favoriteMovies);
        stopRefreshing();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
