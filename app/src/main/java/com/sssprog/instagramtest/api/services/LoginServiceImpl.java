package com.sssprog.instagramtest.api.services;

import android.text.TextUtils;

import com.sssprog.instagramtest.R;
import com.sssprog.instagramtest.api.InstagramClient;
import com.sssprog.instagramtest.api.json.TokenResponseJson;
import com.sssprog.instagramtest.utils.LogHelper;
import com.sssprog.instagramtest.utils.Prefs;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginServiceImpl implements LoginService {

    private InstagramClient client;

    public LoginServiceImpl(InstagramClient client) {
        this.client = client;
    }

    public Observable<Void> login(String code) {
        return client.login(code)
                .map(new Func1<TokenResponseJson, Void>() {
                    @Override
                    public Void call(TokenResponseJson json) {
                        LogHelper.i("-tag-", "login request finished");
                        if (!TextUtils.isEmpty(json.accessToken)) {
                            Prefs.putString(R.string.pref_instagram_access_token, json.accessToken);
                        } else {
                            throw new RuntimeException("empty token");
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io());
    }

}
