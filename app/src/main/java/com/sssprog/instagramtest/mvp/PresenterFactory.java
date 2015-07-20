package com.sssprog.instagramtest.mvp;

public interface PresenterFactory<P extends Presenter> {
    P createPresenter();
}
