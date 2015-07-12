package com.sssprog.instagramtest.mvp;

import android.os.Bundle;

import junit.framework.Assert;

public class PresenterHolder<P extends Presenter> {

    private static final String STATE_PRESENTER = "presenter";

    private final Class<P> presenterClass;
    private final Object view;
    private P mPresenter;

    public PresenterHolder(Class<P> presenterClass, Object view) {
        this.presenterClass = presenterClass;
        this.view = view;
    }

    public P getPresenter() {
        return mPresenter;
    }

    public void init(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mPresenter = PresenterCache.getInstance().get(view.getClass());
        }
        if (mPresenter == null) {
            mPresenter = createPresenter();
            PresenterCache.getInstance().put(getClass(), mPresenter);
            if (savedInstanceState != null) {
                mPresenter.restoreState(savedInstanceState.getBundle(STATE_PRESENTER));
            }
        }
    }

    public void saveState(Bundle outState) {
        outState.putBundle(STATE_PRESENTER, mPresenter.saveState());
    }

    public void onDestroy() {
        PresenterCache.getInstance().remove(view.getClass());
    }

    private P createPresenter() {
        try {
            return presenterClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <P extends Presenter> PresenterHolder<P> createHolder(Object view) {
        PresenterClass annotation = view.getClass().getAnnotation(PresenterClass.class);
        Assert.assertNotNull(annotation);
        Class<P> presenterClass = (Class<P>) annotation.value();
        return new PresenterHolder<>(presenterClass, view);
    }

}
