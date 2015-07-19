package com.sssprog.instagramtest.ui.login;

import com.sssprog.instagramtest.Config;
import com.sssprog.instagramtest.api.SimpleRxSubscriber;
import com.sssprog.instagramtest.mvp.BlockingPresenter;

public class LoginPresenter extends BlockingPresenter<LoginActivity> {

    public void login(String code) {
        setExecutingRequest(true);
        Config.appComponent().loginService().login(code)
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
