package com.example.android.popmovies.ui.detail;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popmovies.R;
import com.example.android.popmovies.api.ApiClient;
import com.example.android.popmovies.api.ApiInterface;
import com.example.android.popmovies.db.FavoritesContract;
import com.example.android.popmovies.db.MoviesDbHelper;
import com.example.android.popmovies.model.Movie;
import com.example.android.popmovies.model.Review;
import com.example.android.popmovies.model.ReviewsResponse;
import com.example.android.popmovies.model.Trailer;
import com.example.android.popmovies.model.TrailersResponse;
import com.example.android.popmovies.utility.StringHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerOnClickListener, LoaderManager.LoaderCallbacks<Cursor> {


    public static final String SAVE_INSTANCE_TRAILERS = "trailers";
    public static final String SAVE_INSTANCE_REVIEWS = "reviews";
    private static final int FAVORITE_LOADER_ID = 1;

    private boolean INSTANCE = false;
    private boolean isClick = false;

    private Movie movie;

    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvReleaseDate;
    private TextView tvRating;
    private TextView tvOverview;
    private RecyclerView rvTrailers;
    private RecyclerView rvReviews;
    private LinearLayout llTrailers;
    private LinearLayout llReviews;
    private Button btnMark;

    private TrailersAdapter trailersAdapter;
    private ArrayList<Trailer> trailerList = new ArrayList<>();

    private ReviewsAdapter reviewsAdapter;
    private ArrayList<Review> reviewList = new ArrayList<>();

    ApiInterface apiInterface;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getIntent().hasExtra(Intent.EXTRA_INTENT)) {
            movie = getIntent().getExtras().getParcelable(Intent.EXTRA_INTENT);
        }

        apiInterface = ApiClient.provideApiInterface();
        if (savedInstanceState != null) {
            trailerList = savedInstanceState.getParcelableArrayList(SAVE_INSTANCE_TRAILERS);
            reviewList = savedInstanceState.getParcelableArrayList(SAVE_INSTANCE_REVIEWS);
        } else {
            getTrailers();
            getReviews();
        }

        ivCover = (ImageView) findViewById(R.id.iv_cover);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        tvRating = (TextView) findViewById(R.id.tv_rating);
        tvOverview = (TextView) findViewById(R.id.tv_overview);
        rvTrailers = (RecyclerView) findViewById(R.id.rv_trailers);
        rvReviews = (RecyclerView) findViewById(R.id.rv_reviews);
        llTrailers = (LinearLayout) findViewById(R.id.ll_trailers);
        llReviews = (LinearLayout) findViewById(R.id.ll_reviews);
        btnMark = (Button) findViewById(R.id.btn_mark);

        LinearLayoutManager layoutManagerTrailers = new LinearLayoutManager(this);
        rvTrailers.setLayoutManager(layoutManagerTrailers);
        rvTrailers.setHasFixedSize(true);
        trailersAdapter = new TrailersAdapter(this, trailerList, this);
        rvTrailers.setAdapter(trailersAdapter);

        LinearLayoutManager layoutManagerReviews = new LinearLayoutManager(this);
        rvReviews.setLayoutManager(layoutManagerReviews);
        rvReviews.setHasFixedSize(true);
        reviewsAdapter = new ReviewsAdapter(this, reviewList);
        rvReviews.setAdapter(reviewsAdapter);

        String releaseDate = StringHelper.formateDateFromstring(movie.getRelease_date());
        String rating = String.valueOf(movie.getVote_average()) + "/10";
        Picasso.with(this).load(ApiClient.BASE_URL_IMAGE + movie.getPoster_path()).into(ivCover);
        tvTitle.setText(movie.getOriginal_title());
        tvReleaseDate.setText(releaseDate);
        tvRating.setText(rating);
        tvOverview.setText(movie.getOverview());

        checkMark();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(SAVE_INSTANCE_TRAILERS, trailerList);
        outState.putParcelableArrayList(SAVE_INSTANCE_REVIEWS, reviewList);
        super.onSaveInstanceState(outState);
    }

    private void checkMark() {
        if (!INSTANCE) {
            getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
            INSTANCE = true;
        } else {
            getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
        }
    }

    public void setTextButton(boolean marked) {
        if (marked) {
            btnMark.setText(getString(R.string.unmark));
        } else {
            btnMark.setText(getString(R.string.mark_as_favorite));
        }
    }

    private void getReviews() {
        apiInterface.getReviews(movie.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ReviewsResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ReviewsResponse reviewsResponse) {
                        refreshReviewsAdapter(reviewsResponse);
                    }
                });
    }

    private void getTrailers() {
        apiInterface.getMovies(movie.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TrailersResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(TrailersResponse trailersResponse) {
                        refreshTrailersAdapter(trailersResponse);
                    }
                });
    }

    public void refreshTrailersAdapter(TrailersResponse trailersResponse) {
        trailerList.clear();
        trailerList.addAll(trailersResponse.getResults());
        trailersAdapter.notifyDataSetChanged();
        showHideTrailers();
    }

    public void refreshReviewsAdapter(ReviewsResponse reviewsResponse) {
        reviewList.clear();
        reviewList.addAll(reviewsResponse.getResults());
        reviewsAdapter.notifyDataSetChanged();
        showHideReviews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    public static void start(Context context, Movie movie, ImageView ivMovie) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, ivMovie, "poster");

        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_INTENT, movie);

        context.startActivity(intent, options.toBundle());
    }

    @Override
    public void onClickTrailer(int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerList.get(position).getKey())));
    }

    public void onClickMark(View v) {
        isClick = true;
        checkMark();
    }

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showHideTrailers() {
        if (trailerList.size() > 0) {
            llTrailers.setVisibility(View.VISIBLE);
        } else {
            llTrailers.setVisibility(View.GONE);
        }
    }

    public void showHideReviews() {
        if (reviewList.size() > 0) {
            llReviews.setVisibility(View.VISIBLE);
        } else {
            llReviews.setVisibility(View.GONE);
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
                try {
                    Uri uri = FavoritesContract.FavoritesEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(String.valueOf(movie.getId())).build();
                    return getContentResolver().query(uri, null, null, null, null);
                } catch (Exception e) {
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
        int count = data.getCount();
        if (isClick) {
            if (count > 0) {
                deleteFavoriteMovie();
            } else {
                insertFavoriteMovie();
            }
            isClick = false;
        } else {
            if (count > 0) {
                setTextButton(true);
            } else {
                setTextButton(false);
            }
        }
    }

    private void insertFavoriteMovie() {
        ContentValues cv = new ContentValues();
        cv.put(FavoritesContract.FavoritesEntry._ID, movie.getId());
        cv.put(FavoritesContract.FavoritesEntry.VOTE_COUNT, movie.getVote_count());
        cv.put(FavoritesContract.FavoritesEntry.VIDEO, movie.isVideo() ? 1 : 0);
        cv.put(FavoritesContract.FavoritesEntry.VOTE_AVERAGE, movie.getVote_average());
        cv.put(FavoritesContract.FavoritesEntry.TITLE, movie.getTitle());
        cv.put(FavoritesContract.FavoritesEntry.POPULARITY, movie.getPopularity());
        cv.put(FavoritesContract.FavoritesEntry.POSTER_PATH, movie.getPoster_path());
        cv.put(FavoritesContract.FavoritesEntry.ORIGINAL_LANGUAGE, movie.getOriginal_language());
        cv.put(FavoritesContract.FavoritesEntry.ORIGINAL_TITLE, movie.getOriginal_title());
        cv.put(FavoritesContract.FavoritesEntry.BACKDROP_PATH, movie.getBackdrop_path());
        cv.put(FavoritesContract.FavoritesEntry.ADULT, movie.isAdult() ? 1 : 0);
        cv.put(FavoritesContract.FavoritesEntry.OVERVIEW, movie.getOverview());
        cv.put(FavoritesContract.FavoritesEntry.RELEASE_DATE, movie.getRelease_date());
        Uri uri = getContentResolver().insert(FavoritesContract.FavoritesEntry.CONTENT_URI, cv);
        if (uri != null) {
            showToast("Movies added to favorite");
            setTextButton(true);
        }
    }

    private void deleteFavoriteMovie() {
        Uri uri = FavoritesContract.FavoritesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(movie.getId())).build();
        getContentResolver().delete(uri, null, null);

        showToast("Movies unmarked");
        setTextButton(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
