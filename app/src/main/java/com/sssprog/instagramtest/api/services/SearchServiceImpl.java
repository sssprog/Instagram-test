package com.sssprog.instagramtest.api.services;

import com.sssprog.instagramtest.api.InstagramClient;
import com.sssprog.instagramtest.api.json.SearchItemJson;
import com.sssprog.instagramtest.api.models.SearchItem;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class SearchServiceImpl implements SearchService {

    private InstagramClient client;

    public SearchServiceImpl(InstagramClient client) {
        this.client = client;
    }

    @Override
    public Observable<List<SearchItem>> search(String userName) {
        return client.search(userName)
                .map(response -> transform(response.data != null ? response.data : new ArrayList<>()))
                .subscribeOn(Schedulers.io());
    }

    private List<SearchItem> transform(List<SearchItemJson> jsonItems) {
        List<SearchItem> result = new ArrayList<>();
        for (SearchItemJson json : jsonItems) {
            SearchItem item = new SearchItem();
            item.setId(json.id);
            item.setUserName(json.userName);
            item.setFullName(json.fullName);
            item.setImage(json.image);
            result.add(item);
        }
        return result;
    }

}
