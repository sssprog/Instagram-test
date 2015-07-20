package com.sssprog.instagramtest.ui.search;

import com.sssprog.instagramtest.ActivityScope;
import com.sssprog.instagramtest.AppComponent;
import com.sssprog.instagramtest.mvp.PresenterFactory;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class})
public interface SearchActivityComponent extends PresenterFactory<SearchPresenter> {

}
