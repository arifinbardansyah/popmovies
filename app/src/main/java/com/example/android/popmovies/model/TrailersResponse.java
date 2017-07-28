package com.example.android.popmovies.model;

import java.util.List;

/**
 * Created by arifinbardansyah on 7/28/17.
 */

public class TrailersResponse {

    private int id;
    private List<Trailer> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }
}
