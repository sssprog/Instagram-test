package com.sssprog.instagramtest.ui.login;

import com.sssprog.instagramtest.mvp.BlockingView;

public interface LoginView extends BlockingView {
    void onLoginSuccess();
    void onLoginFailed();
}
