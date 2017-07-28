package com.example.android.popmovies.ui.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.model.Trailer;

import java.util.List;

/**
 * Created by arifinbardansyah on 7/28/17.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private Context mContext;
    private List<Trailer> mTrailers;
    private TrailerOnClickListener mListener;

    public TrailersAdapter(Context context, List<Trailer> trailers, TrailerOnClickListener listener) {
        mContext = context;
        mTrailers = trailers;
        mListener = listener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.movie_trailer_item,parent,false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public interface TrailerOnClickListener{
        void onClickTrailer(int position);
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvTrailer;
        public TrailerViewHolder(View itemView) {
            super(itemView);
            tvTrailer = (TextView) itemView.findViewById(R.id.tv_name_trailer);
            itemView.setOnClickListener(this);
        }

        public void bind(int position){
            tvTrailer.setText(mTrailers.get(position).getName());
        }

        @Override
        public void onClick(View v) {
            mListener.onClickTrailer(getAdapterPosition());
        }
    }
}
