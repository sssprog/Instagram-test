package com.sssprog.instagramtest.mvp;

import android.os.Bundle;

import junit.framework.Assert;

public class PresenterHolder<P extends Presenter> {

    private static final String STATE_PRESENTER = "presenter";

    private final Class<P> presenterClass;
    private final Object view;
    private P presenter;
    private PresenterHolderListener listener;

    public PresenterHolder(Class<P> presenterClass, Object view, PresenterHolderListener listener) {
        this.presenterClass = presenterClass;
        this.view = view;
        this.listener = listener;
    }

    public P getPresenter() {
        return presenter;
    }

    public void init(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            presenter = PresenterCache.getInstance().get(view.getClass());
        }
        if (presenter == null) {
            presenter = createPresenter();
            listener.onPresenterCreated();
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

    private P createPresenter() {
        try {
            return presenterClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <P extends Presenter> PresenterHolder<P> createHolder(Object view, PresenterHolderListener listener) {
        PresenterClass annotation = view.getClass().getAnnotation(PresenterClass.class);
        Assert.assertNotNull(annotation);
        Class<P> presenterClass = (Class<P>) annotation.value();
        return new PresenterHolder<>(presenterClass, view, listener);
    }

    public interface PresenterHolderListener {
        void onPresenterCreated();
    }

}
