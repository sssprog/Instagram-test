package com.sssprog.instagramtest;

import android.content.Context;

import com.sssprog.instagramtest.api.services.LoginService;
import com.sssprog.instagramtest.api.services.PostService;
import com.sssprog.instagramtest.api.services.SearchService;

public interface AppModuleProvider {
    Context getContext();
    LoginService getLoginService();
    PostService getPostService();
    SearchService getSearchService();
}
