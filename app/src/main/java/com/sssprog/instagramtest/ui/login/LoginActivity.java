package com.sssprog.instagramtest.ui.login;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sssprog.instagramtest.Config;
import com.sssprog.instagramtest.R;
import com.sssprog.instagramtest.api.InstagramClient;
import com.sssprog.instagramtest.mvp.PresenterClass;
import com.sssprog.instagramtest.ui.BaseMvpActivity;
import com.sssprog.instagramtest.utils.LogHelper;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

@PresenterClass(LoginPresenter.class)
public class LoginActivity extends BaseMvpActivity<LoginPresenter> {

    private static final String TAG = LogHelper.getTag(LoginActivity.class);

    @InjectView(R.id.webView)
    WebView webView;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    private LoginActivityComponent component;
    @Inject
    InstagramClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        component = DaggerLoginActivityComponent.builder()
                .appComponent(Config.appComponent())
                .build();
        component.inject(this);

        setUpWebView();
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    @Override
    protected void injectPresenter() {
        super.injectPresenter();
        component.inject(getPresenter());
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    private void setUpWebView() {
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new OAuthWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(client.getAuthUrl());
    }

    void onLoginSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    void onLoginFailed() {
        Toast.makeText(this, R.string.general_error_message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private class OAuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogHelper.d(TAG, "Redirecting URL " + url);

            if (url.startsWith(client.getCallbackUrl())) {
                String urls[] = url.split("=");
                getPresenter().login(urls[1]);
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            LogHelper.d(TAG, "Page error: " + description);
            super.onReceivedError(view, errorCode, description, failingUrl);
            onLoginFailed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            LogHelper.d(TAG, "Loading URL: " + url);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogHelper.d(TAG, "onPageFinished URL: " + url);
            progressBar.setVisibility(View.GONE);
        }

    }

}
