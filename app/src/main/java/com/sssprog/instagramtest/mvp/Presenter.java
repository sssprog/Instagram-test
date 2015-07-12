package com.sssprog.instagramtest.mvp;

import android.os.Bundle;

import java.util.LinkedHashSet;

public class Presenter<V> {
    private V mView;
    protected final LinkedHashSet<Runnable> mViewActions = new LinkedHashSet<>();

    public void attach(V view) {
        this.mView = view;
        onViewAttached();
        executeViewActions();
    }

    public void detach() {
        if (mView != null) {
            onBeforeViewDetached();
        }
        mView = null;
        onViewDetached();
    }

    protected boolean isViewAttached() {
        return mView != null;
    }

    protected V getView() {
        return mView;
    }

    public void destroy() {
    }

    protected void onViewAttached() {
    }

    protected void onViewDetached() {
    }

    protected void onBeforeViewDetached() {
    }

    private void executeViewActions() {
        for (Runnable action : mViewActions) {
            action.run();
        }
        mViewActions.clear();
    }

    protected void runViewAction(Runnable action) {
        if (isViewAttached()) {
            action.run();
        } else {
            mViewActions.add(action);
        }
    }

    public Bundle saveState() {
        return new Bundle();
    }

    public void restoreState(Bundle state) {
    }

}
