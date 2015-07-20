package com.sssprog.instagramtest.ui.post;

import com.sssprog.instagramtest.api.SimpleRxSubscriber;
import com.sssprog.instagramtest.api.models.PostWithComments;
import com.sssprog.instagramtest.api.services.PostService;
import com.sssprog.instagramtest.mvp.Presenter;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class PostPresenter extends Presenter<PostActivity> {

    private PostService postService;

    @Inject
    public PostPresenter(PostService postService) {
        this.postService = postService;
    }

    public void loadData(long postId) {
        postService.getPostWithComments(postId)
                .observeOn(AndroidSchedulers.mainThread())
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
