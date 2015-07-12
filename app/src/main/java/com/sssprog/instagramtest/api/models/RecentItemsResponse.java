package com.sssprog.instagramtest.api.models;

import com.sssprog.instagramtest.api.database.Post;

import java.util.List;

public class RecentItemsResponse {

    public final List<Post> items;
    public final boolean fromDb;
    public final boolean allLoaded;

    public RecentItemsResponse(List<Post> items, boolean fromDb, boolean allLoaded) {
        this.items = items;
        this.fromDb = fromDb;
        this.allLoaded = allLoaded;
    }
}
