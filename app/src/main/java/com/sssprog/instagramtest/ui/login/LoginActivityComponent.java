package com.sssprog.instagramtest.ui.login;

import com.sssprog.instagramtest.ActivityScope;
import com.sssprog.instagramtest.AppComponent;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class})
public interface LoginActivityComponent {

    void inject(LoginActivity activity);
    void inject(LoginPresenter presenter);

}
