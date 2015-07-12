package com.sssprog.instagramtest.mvp;

import java.util.HashMap;
import java.util.Map;

public class PresenterCache {

    private Map<Class<?>, Presenter> presenters = new HashMap<>();
    private static PresenterCache instance;

    public void put(Class<?> key, Presenter presenter) {
        remove(key);
        presenters.put(key, presenter);
    }

    public <P extends Presenter> P get(Class<?> key) {
        return (P) presenters.get(key);
    }

    public void remove(Class<?> key) {
        Presenter presenter = presenters.get(key);
        if (presenter != null) {
            presenter.destroy();
        }
    }

    public static PresenterCache getInstance() {
        if (instance == null) {
            instance = new PresenterCache();
        }
        return instance;
    }

}
