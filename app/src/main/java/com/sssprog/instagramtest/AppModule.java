package com.sssprog.instagramtest;

import android.content.Context;

import com.sssprog.instagramtest.api.services.LoginService;
import com.sssprog.instagramtest.api.services.PostService;
import com.sssprog.instagramtest.api.services.SearchService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private AppModuleProvider provider;

    public AppModule(AppModuleProvider provider) {
        this.provider = provider;
    }

    @Provides
    @Singleton
    Context provideAppContext() {
        return provider.getContext();
    }

    @Provides
    @Singleton
    LoginService provideLoginService() {
        return provider.getLoginService();
    }

    @Provides
    @Singleton
    PostService providePostService() {
        return provider.getPostService();
    }

    @Provides
    @Singleton
    SearchService provideSearchService() {
        return provider.getSearchService();
    }

}
