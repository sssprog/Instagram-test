package com.sssprog.instagramtest.ui.login;

import com.sssprog.instagramtest.api.SimpleRxSubscriber;
import com.sssprog.instagramtest.api.services.LoginService;
import com.sssprog.instagramtest.mvp.BlockingPresenter;

import javax.inject.Inject;

public class LoginPresenter extends BlockingPresenter<LoginActivity> {

    private LoginService loginService;

    @Inject
    public LoginPresenter(LoginService loginService) {
        this.loginService = loginService;
    }

    public void login(String code) {
        setExecutingRequest(true);
        loginService.login(code)
                .subscribe(new SimpleRxSubscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        runViewAction(() -> getView().onLoginSuccess());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        runViewAction(() -> getView().onLoginFailed());
                    }
                });
    }

}
