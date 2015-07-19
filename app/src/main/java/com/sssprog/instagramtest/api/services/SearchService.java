package com.sssprog.instagramtest.api.services;

import com.sssprog.instagramtest.api.models.SearchItem;

import java.util.List;

import rx.Observable;

public interface SearchService {
    Observable<List<SearchItem>> search(String userName);
}
