package com.sssprog.instagramtest.ui.post;

import com.sssprog.instagramtest.api.SimpleRxSubscriber;
import com.sssprog.instagramtest.api.models.PostWithComments;
import com.sssprog.instagramtest.api.services.PostService;
import com.sssprog.instagramtest.mvp.Presenter;

public class PostPresenter extends Presenter<PostActivity> {

    public void loadData(long postId) {
        PostService.getInstance().getPostWithComments(postId)
                .subscribe(new SimpleRxSubscriber<PostWithComments>() {
                    @Override
                    public void onNext(final PostWithComments data) {
                        runViewAction(new Runnable() {
                            @Override
                            public void run() {
                                getView().onDataLoaded(data);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        runViewAction(new Runnable() {
                            @Override
                            public void run() {
                                getView().onError();
                            }
                        });
                    }
                });
    }

}