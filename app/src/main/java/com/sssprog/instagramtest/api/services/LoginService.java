package com.sssprog.instagramtest.api.services;

import rx.Observable;

public interface LoginService {
    Observable<Void> login(String code);
}
