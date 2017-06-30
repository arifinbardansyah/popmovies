package com.example.android.popmovies;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.example.android.popmovies.api.ApiClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by arifinbardansyah on 6/29/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private List<ResultDao> mMovieList;
    private Context mContext;
    private MovieClickListener mListener;

    public MoviesAdapter(Context context, List<ResultDao> movieList, MovieClickListener listener) {
        mContext = context;
        mMovieList = movieList;
        mListener = listener;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.movie_item, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public interface MovieClickListener{
        void onClick(int position, ImageView ivMovie);
    }
    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivMovie;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            ivMovie = (ImageView) itemView.findViewById(R.id.iv_movie);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            Picasso.with(mContext)
                    .load(ApiClient.BASE_URL_IMAGE+mMovieList.get(position).getPoster_path())
                    .into(ivMovie,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            scheduleStartPostponedTransition(ivMovie);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        private void scheduleStartPostponedTransition(final View sharedElement) {
            sharedElement.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                            ((AppCompatActivity)mContext).startPostponedEnterTransition();
                            return true;
                        }
                    });
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(getAdapterPosition(),ivMovie);
        }
    }
}
