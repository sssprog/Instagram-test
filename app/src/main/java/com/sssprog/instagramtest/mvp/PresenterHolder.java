package com.sssprog.instagramtest.mvp;

import android.os.Bundle;

public class PresenterHolder<P extends Presenter> {

    private static final String STATE_PRESENTER = "presenter";

    private final Object view;
    private final PresenterFactory<P> factory;
    private P presenter;

    public PresenterHolder(Object view, PresenterFactory<P> factory) {
        this.view = view;
        this.factory = factory;
    }

    public P getPresenter() {
        return presenter;
    }

    public void init(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            presenter = PresenterCache.getInstance().get(view.getClass());
        }
        if (presenter == null) {
            presenter = factory.createPresenter();
            PresenterCache.getInstance().put(getClass(), presenter);
            if (savedInstanceState != null) {
                presenter.restoreState(savedInstanceState.getBundle(STATE_PRESENTER));
            }
        }
    }

    public void saveState(Bundle outState) {
        outState.putBundle(STATE_PRESENTER, presenter.saveState());
    }

    public void onDestroy() {
        PresenterCache.getInstance().remove(view.getClass());
    }

}
