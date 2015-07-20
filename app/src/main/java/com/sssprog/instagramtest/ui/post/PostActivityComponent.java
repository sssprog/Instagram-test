package com.sssprog.instagramtest.ui.post;

import com.sssprog.instagramtest.ActivityScope;
import com.sssprog.instagramtest.AppComponent;
import com.sssprog.instagramtest.mvp.PresenterFactory;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class})
public interface PostActivityComponent extends PresenterFactory<PostPresenter> {

}
