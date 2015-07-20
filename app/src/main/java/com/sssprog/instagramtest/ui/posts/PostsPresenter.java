package com.sssprog.instagramtest.ui.posts;

import com.sssprog.instagramtest.api.SimpleRxSubscriber;
import com.sssprog.instagramtest.api.models.RecentItemsResponse;
import com.sssprog.instagramtest.api.services.PostService;
import com.sssprog.instagramtest.mvp.Presenter;

import java.util.ArrayList;

import javax.inject.Inject;

public class PostsPresenter extends Presenter<PostsActivity> {

    private PostService postService;
    private int lastRequestId;
    private boolean isLoading;

    @Inject
    public PostsPresenter(PostService postService) {
        this.postService = postService;
    }

    public void loadItems(final boolean fromStart) {
        // Don't make more then one request for more items,
        // but if user presses the refresh button, we have to start that request right away
        if (isLoading && !fromStart) {
            return;
        }
        isLoading = true;
        lastRequestId++;
        final int requestId = lastRequestId;
        postService.getItems(fromStart)
                .subscribe(new SimpleRxSubscriber<RecentItemsResponse>() {
                    @Override
                    public void onNext(final RecentItemsResponse response) {
                        if (requestId != lastRequestId) {
                            return;
                        }
                        isLoading = false;
                        runViewAction(() -> getView().onItemsLoaded(response.items, fromStart, response.allLoaded));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (requestId != lastRequestId) {
                            return;
                        }
                        isLoading = false;
                        runViewAction(() -> getView().onItemsLoaded(new ArrayList<>(), fromStart, true));
                    }
                });
    }

}
