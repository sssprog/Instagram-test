package com.sssprog.instagramtest.ui.post;

import com.sssprog.instagramtest.Config;
import com.sssprog.instagramtest.api.SimpleRxSubscriber;
import com.sssprog.instagramtest.api.models.PostWithComments;
import com.sssprog.instagramtest.mvp.Presenter;

public class PostPresenter extends Presenter<PostActivity> {

    public void loadData(long postId) {
        Config.appComponent().postService().getPostWithComments(postId)
                .subscribe(new SimpleRxSubscriber<PostWithComments>() {
                    @Override
                    public void onNext(final PostWithComments data) {
                        runViewAction(() -> getView().onDataLoaded(data));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        runViewAction(() -> getView().onError());
                    }
                });
    }

}
