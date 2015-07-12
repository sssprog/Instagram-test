package com.sssprog.instagramtest.api;

import com.sssprog.instagramtest.utils.LogHelper;

import rx.Subscriber;

public class SimpleRxSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        LogHelper.printStackTrace(e);
    }

    @Override
    public void onNext(T item) {

    }
}
