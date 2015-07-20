package com.sssprog.instagramtest;

import android.content.Context;

import com.sssprog.instagramtest.api.InstagramClient;
import com.sssprog.instagramtest.api.InstagramClientImpl;
import com.sssprog.instagramtest.api.services.LoginService;
import com.sssprog.instagramtest.api.services.LoginServiceImpl;
import com.sssprog.instagramtest.api.services.PostService;
import com.sssprog.instagramtest.api.services.PostServiceImpl;
import com.sssprog.instagramtest.api.services.SearchService;
import com.sssprog.instagramtest.api.services.SearchServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Context appContext;

    public AppModule(Context context) {
        this.appContext = context;
    }

    @Provides
    @Singleton
    Context provideAppContext() {
        return appContext;
    }

    @Provides
    @Singleton
    InstagramClient provideInstagramClient() {
        return new InstagramClientImpl(appContext.getString(R.string.instagram_client_id),
                appContext.getString(R.string.instagram_client_secret),
                appContext.getString(R.string.instagram_callback_url));
    }

    @Provides
    @Singleton
    LoginService provideLoginService(InstagramClient client) {
        return new LoginServiceImpl(client);
    }

    @Provides
    @Singleton
    PostService providePostService(InstagramClient client) {
        return new PostServiceImpl(client);
    }

    @Provides
    @Singleton
    SearchService provideSearchService(InstagramClient client) {
        return new SearchServiceImpl(client);
    }

}
