package com.sssprog.instagramtest.api.services;

import com.sssprog.instagramtest.api.models.PostWithComments;
import com.sssprog.instagramtest.api.models.RecentItemsResponse;

import rx.Observable;

public interface PostService {
    Observable<RecentItemsResponse> getItems(boolean fromStart);
    void clearCache();
    Observable<PostWithComments> getPostWithComments(long postId);
}
