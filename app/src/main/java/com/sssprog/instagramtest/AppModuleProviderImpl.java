package com.sssprog.instagramtest;

import android.content.Context;

import com.sssprog.instagramtest.api.services.LoginService;
import com.sssprog.instagramtest.api.services.LoginServiceImpl;
import com.sssprog.instagramtest.api.services.PostService;
import com.sssprog.instagramtest.api.services.PostServiceImpl;
import com.sssprog.instagramtest.api.services.SearchService;
import com.sssprog.instagramtest.api.services.SearchServiceImpl;

public class AppModuleProviderImpl implements AppModuleProvider {

    private Context appContext;

    public AppModuleProviderImpl(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public Context getContext() {
        return appContext;
    }

    @Override
    public LoginService getLoginService() {
        return new LoginServiceImpl();
    }

    @Override
    public PostService getPostService() {
        return new PostServiceImpl();
    }

    @Override
    public SearchService getSearchService() {
        return new SearchServiceImpl();
    }

}
