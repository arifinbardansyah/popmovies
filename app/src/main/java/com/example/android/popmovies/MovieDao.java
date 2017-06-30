package com.example.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by arifinbardansyah on 6/29/17.
 */

public class MovieDao implements Parcelable {
    private int page;
    private int total_results;
    private int total_pages;
    private List<ResultDao> results;

    public MovieDao() {
    }

    protected MovieDao(Parcel in) {
        page = in.readInt();
        total_results = in.readInt();
        total_pages = in.readInt();
        results = in.createTypedArrayList(ResultDao.CREATOR);
    }

    public static final Creator<MovieDao> CREATOR = new Creator<MovieDao>() {
        @Override
        public MovieDao createFromParcel(Parcel in) {
            return new MovieDao(in);
        }

        @Override
        public MovieDao[] newArray(int size) {
            return new MovieDao[size];
        }
    };

    public int getPage() {
        return page;
    }

    public int getTotal_results() {
        return total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public List<ResultDao> getResults() {
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
        dest.writeInt(total_results);
        dest.writeInt(total_pages);
        dest.writeTypedList(results);
    }
}
