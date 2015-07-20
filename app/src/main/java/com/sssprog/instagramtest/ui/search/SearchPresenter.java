package com.sssprog.instagramtest.ui.search;

import com.sssprog.instagramtest.api.SimpleRxSubscriber;
import com.sssprog.instagramtest.api.models.SearchItem;
import com.sssprog.instagramtest.api.services.SearchService;
import com.sssprog.instagramtest.mvp.Presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.subjects.PublishSubject;

public class SearchPresenter extends Presenter<SearchActivity> {

    private static final long DELAY = 500;

    private SearchService searchService;
    private String lastSearch;
    private PublishSubject<String> requests = PublishSubject.create();

    @Inject
    public SearchPresenter(SearchService searchService) {
        this.searchService = searchService;
        requests = PublishSubject.create();
        requests.debounce(DELAY, TimeUnit.MILLISECONDS)
                .subscribe(new SimpleRxSubscriber<String>() {
                    @Override
                    public void onNext(String userName) {
                        searchInternal(userName);
                    }
                });
    }

    public void search(final String userName) {
        requests.onNext(userName);
    }

    private void searchInternal(final String userName) {
        lastSearch = userName;
        searchService.search(userName)
                .subscribe(new SimpleRxSubscriber<List<SearchItem>>() {
                    @Override
                    public void onNext(final List<SearchItem> items) {
                        runViewAction(() -> {
                            if (lastSearch.equals(userName)) {
                                getView().onItemsLoaded(items);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        runViewAction(() -> {
                            if (lastSearch.equals(userName)) {
                                getView().onItemsLoaded(new ArrayList<>());
                            }
                        });
                    }
                });
    }

}
