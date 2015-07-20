package com.sssprog.instagramtest.api.services;

import android.test.AndroidTestCase;

import com.sssprog.instagramtest.RxAssertions;
import com.sssprog.instagramtest.TestUtils;
import com.sssprog.instagramtest.api.InstagramClient;
import com.sssprog.instagramtest.api.json.TokenResponseJson;
import com.sssprog.instagramtest.utils.Prefs;

import rx.Observable;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginServiceTest extends AndroidTestCase {

    private LoginServiceImpl loginService;
    private InstagramClient client;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.setDexCachePath(getContext());
        TestUtils.setup(getContext());
        client = mock(InstagramClient.class);
        loginService = new LoginServiceImpl(client);
    }

    public void testTokenBeingSavedAfterSuccessfulLogin() {
        TokenResponseJson response = new TokenResponseJson();
        response.accessToken = "test";
        when(client.login(anyString())).thenReturn(Observable.just(response));
        assertThat(Prefs.getAccessToken()).isNull();
        RxAssertions.subscribeAssertingThat(loginService.login(""))
                .assertCompleted();
        assertThat(Prefs.getAccessToken()).isEqualTo(response.accessToken);
    }

    public void testTokenNotSavedOnError() {
        when(client.login(anyString())).thenReturn(Observable.error(new Exception()));
        RxAssertions.subscribeAssertingThat(loginService.login(""))
                .assertError();
    }
}
