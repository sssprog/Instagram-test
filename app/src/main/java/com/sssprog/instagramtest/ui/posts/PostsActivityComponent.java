package com.sssprog.instagramtest.ui.posts;

import com.sssprog.instagramtest.ActivityScope;
import com.sssprog.instagramtest.AppComponent;
import com.sssprog.instagramtest.mvp.PresenterFactory;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class})
public interface PostsActivityComponent extends PresenterFactory<PostsPresenter> {
    void inject(PostsActivity activity);
}
