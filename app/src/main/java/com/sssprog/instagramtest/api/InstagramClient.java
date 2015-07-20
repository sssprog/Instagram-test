package com.sssprog.instagramtest.api;

import com.sssprog.instagramtest.api.json.CommentResponseJson;
import com.sssprog.instagramtest.api.json.RecentResponseJson;
import com.sssprog.instagramtest.api.json.SearchResponseJson;
import com.sssprog.instagramtest.api.json.TokenResponseJson;

import rx.Observable;

public interface InstagramClient {
    String getAuthUrl();
    String getCallbackUrl();
    boolean isLoggedIn();
    Observable<TokenResponseJson> login(final String code);
    Observable<RecentResponseJson> getRecentItems(String lastId, int count);
    Observable<SearchResponseJson> search(String userName);
    Observable<CommentResponseJson> getComments(String postId);
}
