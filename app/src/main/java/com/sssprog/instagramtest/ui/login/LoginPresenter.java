package com.sssprog.instagramtest.ui.login;

import com.sssprog.instagramtest.api.SimpleRxSubscriber;
import com.sssprog.instagramtest.api.services.LoginService;
import com.sssprog.instagramtest.mvp.BlockingPresenter;

public class LoginPresenter extends BlockingPresenter<LoginActivity> {

    public void login(String code) {
        setExecutingRequest(true);
        LoginService.getInstance().login(code)
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
