package com.sssprog.instagramtest.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.sssprog.instagramtest.Config;
import com.sssprog.instagramtest.R;
import com.sssprog.instagramtest.api.json.CommentResponseJson;
import com.sssprog.instagramtest.api.json.RecentResponseJson;
import com.sssprog.instagramtest.api.json.SearchResponseJson;
import com.sssprog.instagramtest.api.json.TokenResponseJson;
import com.sssprog.instagramtest.utils.LogHelper;
import com.sssprog.instagramtest.utils.Prefs;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okio.Buffer;
import rx.Observable;
import rx.Subscriber;

public class InstagramClient {

    private static final String TAG = LogHelper.getTag(InstagramClient.class);

    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String API_URL = "https://api.instagram.com/v1";
    private static final String REQUEST_RECENT_MEDIA = "/users/%s/media/recent/?access_token=%s&count=%d";
    private static final String REQUEST_SEARCH = "/users/search?access_token=%s&q=%s";
    private static final String REQUEST_COMMENTS = "/media/%s/comments?access_token=%s";

    private static final long CONNECTION_TIMEOUT = 30;
    private static final long READ_TIMEOUT = 30;
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static InstagramClient instance;

    private String clientId;
    private String clientSecret;
    private String callbackUrl;
    private OkHttpClient client;
    private Gson gson;

    public static synchronized InstagramClient getInstance() {
        if (instance == null) {
            instance = new InstagramClient();
        }
        return instance;
    }

    public InstagramClient() {
        gson = new Gson();
    }

    public void init(String clientId, String clientSecret, String callbackUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.callbackUrl = callbackUrl;
    }

    public String getAuthUrl() {
        return AUTH_URL + "?client_id=" + clientId + "&redirect_uri="
                + callbackUrl + "&response_type=code&display=touch&scope=likes+comments+relationships";
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public Observable<TokenResponseJson> login(final String code) {
        String body = "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&grant_type=authorization_code" +
                "&redirect_uri=" + callbackUrl +
                "&code=" + code;
        Request.Builder request = new Request.Builder()
                .url(TOKEN_URL)
                .post(RequestBody.create(MEDIA_TYPE_JSON, body));
        return makeRequest(request, new TypeToken<TokenResponseJson>() { }.getType());
    }

    private synchronized OkHttpClient getHttpClient() {
        if (client == null) {
            client = new OkHttpClient();
            client.setConnectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
            client.setReadTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        }
        return client;
    }

    private String getAccessToken() {
        return Prefs.getString(R.string.pref_instagram_access_token);
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    public Observable<RecentResponseJson> getRecentItems(String lastId, final int count) {
        String url = makeApiUrl(REQUEST_RECENT_MEDIA, Prefs.getUserId(), getAccessToken(), count);
        if (lastId != null) {
            url += "&max_id=" + lastId;
        }
        Request.Builder request = new Request.Builder().url(url);
        return makeRequest(request, new TypeToken<RecentResponseJson>() { }.getType());
    }

    public Observable<SearchResponseJson> search(String userName) {
        Request.Builder request = new Request.Builder()
                .url(makeApiUrl(REQUEST_SEARCH, getAccessToken(), userName));
        return makeRequest(request, new TypeToken<SearchResponseJson>() { }.getType());
    }

    public Observable<CommentResponseJson> getComments(String postId) {
        Request.Builder request = new Request.Builder()
                .url(makeApiUrl(REQUEST_COMMENTS, postId, getAccessToken()));
        return makeRequest(request, new TypeToken<CommentResponseJson>() { }.getType());
    }

    private String makeApiUrl(String path, Object... params) {
        return String.format(Locale.US, API_URL + path, params);
    }

    private <T> Observable<T> makeRequest(final Request.Builder builder, final Type type) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                ApiUtils.executeWithRuntimeException(new ApiUtils.ApiTask() {
                    @Override
                    public void execute() throws Exception {
                        Request request = builder.build();
                        Response response = getHttpClient().newCall(request).execute();
                        if (response.code() >= 400) {
                            throw new RuntimeException("failed");
                        }
                        String body = response.body().string();
                        logRequest(request, response, body);
                        T result = gson.fromJson(body, type);
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    protected void logRequest(Request request, Response response, String body) throws IOException {
        if (Config.LOGS_ENABLED) {
            String requestString = request.method() + " " + request.urlString();
            if (!request.method().toLowerCase().equals("get")) {
                Buffer buffer = new Buffer();
                request.body().writeTo(buffer);
                requestString += " " + buffer.readUtf8();
            }
            if (response.code() >= 400) {
                LogHelper.d(TAG, "request failed " + requestString);
            } else {
                LogHelper.d(TAG, "request success " + requestString);
            }
            LogHelper.i(TAG, "response = " + response.code() + "  " + body);
        }
    }

}
