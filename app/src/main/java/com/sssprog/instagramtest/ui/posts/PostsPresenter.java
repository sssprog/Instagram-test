package com.sssprog.instagramtest.ui.posts;

import com.sssprog.instagramtest.api.SimpleRxSubscriber;
import com.sssprog.instagramtest.api.database.Post;
import com.sssprog.instagramtest.api.models.RecentItemsResponse;
import com.sssprog.instagramtest.api.services.PostService;
import com.sssprog.instagramtest.mvp.Presenter;

import java.util.ArrayList;

public class PostsPresenter extends Presenter<PostsActivity> {

    private int lastRequestId;
    private boolean isLoading;

    public void loadItems(final boolean fromStart) {
        // Don't make more then one request for more items,
        // but if user presses the refresh button, we have to start that request right away
        if (isLoading && !fromStart) {
            return;
        }
        isLoading = true;
        lastRequestId++;
        final int requestId = lastRequestId;
        PostService.getInstance().getItems(fromStart)
                .subscribe(new SimpleRxSubscriber<RecentItemsResponse>() {
                    @Override
                    public void onNext(final RecentItemsResponse response) {
                        if (requestId != lastRequestId) {
                            return;
                        }
                        isLoading = false;
                        runViewAction(new Runnable() {
                            @Override
                            public void run() {
                                getView().onItemsLoaded(response.items, fromStart, response.allLoaded);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (requestId != lastRequestId) {
                            return;
                        }
                        isLoading = false;
                        runViewAction(new Runnable() {
                            @Override
                            public void run() {
                                getView().onItemsLoaded(new ArrayList<Post>(), fromStart, true);
                            }
                        });
                    }
                });
    }

}
