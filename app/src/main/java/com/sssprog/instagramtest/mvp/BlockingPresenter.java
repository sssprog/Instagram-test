package com.sssprog.instagramtest.mvp;

import com.sssprog.instagramtest.ui.BaseMvpActivity;

public class BlockingPresenter<V extends BaseMvpActivity> extends Presenter<V> {

    protected boolean isExecutingRequest;

    protected void setExecutingRequest(boolean value) {
        isExecutingRequest = value;
        if (isViewAttached()) {
            if (isExecutingRequest) {
                getView().showLoadingDialog();
            } else {
                getView().dismissLoadingDialog();
            }
        }
    }

    @Override
    protected void onViewAttached() {
        if (isExecutingRequest) {
            getView().showLoadingDialog();
        }
    }

    @Override
    protected void onBeforeViewDetached() {
        if (isExecutingRequest) {
            getView().dismissLoadingDialog();
        }
    }
}
