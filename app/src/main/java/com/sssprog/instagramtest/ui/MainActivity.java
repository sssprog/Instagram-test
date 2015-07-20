package com.sssprog.instagramtest.ui;

import android.content.Intent;
import android.os.Bundle;

import com.sssprog.instagramtest.R;
import com.sssprog.instagramtest.api.InstagramClientImpl;
import com.sssprog.instagramtest.ui.login.LoginActivity;
import com.sssprog.instagramtest.ui.posts.PostsActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_LOGIN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if (InstagramClientImpl.getInstance().isLoggedIn()) {
            startPostsActivity();
        }
    }

    private void startPostsActivity() {
        startActivity(new Intent(this, PostsActivity.class));
        finish();
    }

    @OnClick(R.id.loginButton)
    public void onLoginClicked() {
        startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_LOGIN) {
            startPostsActivity();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
