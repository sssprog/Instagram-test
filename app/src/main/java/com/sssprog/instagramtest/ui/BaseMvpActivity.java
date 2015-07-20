package com.sssprog.instagramtest.ui;

import android.os.Bundle;

import com.sssprog.instagramtest.dialogs.ProgressDialogFragment;
import com.sssprog.instagramtest.mvp.Presenter;
import com.sssprog.instagramtest.mvp.PresenterHolder;

public abstract class BaseMvpActivity<P extends Presenter> extends BaseActivity {

    private static final String LOADING_DIALOG_TAG = "loading_dialog";

    private PresenterHolder<P> presenterHolder;

    public P getPresenter() {
        return presenterHolder.getPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenterHolder = PresenterHolder.createHolder(this, this::injectPresenter);
        presenterHolder.init(savedInstanceState);
        getPresenter().attach(this);
    }

    protected void injectPresenter() {
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().attach(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().detach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenterHolder.saveState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            presenterHolder.onDestroy();
        }
    }

    public void showLoadingDialog() {
        getSupportFragmentManager().beginTransaction()
                .add(ProgressDialogFragment.newInstance(), LOADING_DIALOG_TAG)
                .commit();
    }

    public void dismissLoadingDialog() {
        ProgressDialogFragment dialog = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(LOADING_DIALOG_TAG);
        dialog.dismiss();
    }

}
