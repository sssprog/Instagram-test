package com.sssprog.instagramtest;

import android.content.Context;

import com.sssprog.instagramtest.api.InstagramClient;
import com.sssprog.instagramtest.api.services.LoginService;
import com.sssprog.instagramtest.api.services.PostService;
import com.sssprog.instagramtest.api.services.SearchService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { AppModule.class })
public interface AppComponent {

    Context appContext();
    InstagramClient instagramClient();
    LoginService loginService();
    PostService postService();
    SearchService searchService();

}
