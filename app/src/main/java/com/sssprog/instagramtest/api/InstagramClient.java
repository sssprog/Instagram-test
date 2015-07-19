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
    private static final String REQUEST_RECENT_MEDIA = "/users/%s/media/recent";
    private static final String REQUEST_SEARCH = "/users/search";
    private static final String REQUEST_COMMENTS = "/media/%s/comments";

    private static final long CONNECTION_TIMEOUT = 30;
    private static final long READ_TIMEOUT = 30;
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String PARAM_TOKEN = "access_token";

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
        QueryParams params = new QueryParams()
                .add("client_id", clientId)
                .add("redirect_uri", callbackUrl)
                .add("response_type", "code")
                .add("display", "touch")
                .add("scope", "likes+comments+relationships");
        return AUTH_URL + params.asUrlParams();
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public Observable<TokenResponseJson> login(final String code) {
        QueryParams params = new QueryParams()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("redirect_uri", callbackUrl)
                .add("redirect_uri", callbackUrl)
                .add("code", code)
                .add("grant_type", "authorization_code");
        Request.Builder request = new Request.Builder()
                .url(TOKEN_URL)
                .post(RequestBody.create(MEDIA_TYPE_JSON, params.asBody()));
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
        QueryParams params = getBasicQueryParams()
                .add("count", count);
        String url = makeApiUrl(REQUEST_RECENT_MEDIA, params, Prefs.getUserId());
        if (lastId != null) {
            params.add("max_id", lastId);
        }
        Request.Builder request = new Request.Builder().url(url);
        return makeRequest(request, new TypeToken<RecentResponseJson>() { }.getType());
    }

    public Observable<SearchResponseJson> search(String userName) {
        QueryParams params = getBasicQueryParams()
                .add("q", userName);
        Request.Builder request = new Request.Builder()
                .url(makeApiUrl(REQUEST_SEARCH, params));
        return makeRequest(request, new TypeToken<SearchResponseJson>() {
        }.getType());
    }

    public Observable<CommentResponseJson> getComments(String postId) {
        Request.Builder request = new Request.Builder()
                .url(makeApiUrl(REQUEST_COMMENTS, getBasicQueryParams(), postId));
        return makeRequest(request, new TypeToken<CommentResponseJson>() { }.getType());
    }

    private QueryParams getBasicQueryParams() {
        return new QueryParams()
                .add(PARAM_TOKEN, getAccessToken());
    }

    private String makeApiUrl(String path, QueryParams queryParams, Object... pathParams) {
        return API_URL +
                String.format(Locale.US, path, pathParams) +
                queryParams.asUrlParams();
    }

    private <T> Observable<T> makeRequest(final Request.Builder builder, final Type type) {
        return Observable
                .create((final Subscriber<? super T> subscriber) -> {
                    ApiUtils.executeWithRuntimeException(() -> {
                        Request request = builder.build();
                        Response response = getHttpClient().newCall(request).execute();
                        String body = response.body().string();
                        logRequest(request, response, body);
                        if (response.code() >= 400) {
                            throw new RuntimeException("failed");
                        }
                        T result = gson.fromJson(body, type);
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    });
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
