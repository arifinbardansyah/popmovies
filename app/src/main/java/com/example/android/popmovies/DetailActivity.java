package com.example.android.popmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popmovies.api.ApiClient;
import com.example.android.popmovies.utility.StringHelper;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private ResultDao movie;

    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvReleaseDate;
    private TextView tvRating;
    private TextView tvOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getIntent().hasExtra(Intent.EXTRA_INTENT)){
            movie = getIntent().getExtras().getParcelable(Intent.EXTRA_INTENT);
        }

        ivCover = (ImageView)findViewById(R.id.iv_cover);
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvReleaseDate = (TextView)findViewById(R.id.tv_release_date);
        tvRating = (TextView)findViewById(R.id.tv_rating);
        tvOverview = (TextView)findViewById(R.id.tv_overview);

        String releaseDate = StringHelper.formateDateFromstring(movie.getRelease_date());
        String rating = String.valueOf(movie.getVote_average())+"/10";
        Picasso.with(this).load(ApiClient.BASE_URL_IMAGE+movie.getPoster_path()).into(ivCover);
        tvTitle.setText(movie.getOriginal_title());
        tvReleaseDate.setText(releaseDate);
        tvRating.setText(rating);
        tvOverview.setText(movie.getOverview());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    public static void start(Context context, ResultDao movie, ImageView ivMovie) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, ivMovie, "poster");

        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_INTENT, movie);

        context.startActivity(intent, options.toBundle());
    }

}
