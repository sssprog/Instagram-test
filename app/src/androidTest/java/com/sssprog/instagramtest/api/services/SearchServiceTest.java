package com.sssprog.instagramtest.api.services;

import android.test.AndroidTestCase;

import com.sssprog.instagramtest.RxAssertions;
import com.sssprog.instagramtest.TestUtils;
import com.sssprog.instagramtest.api.InstagramClient;
import com.sssprog.instagramtest.api.json.SearchItemJson;
import com.sssprog.instagramtest.api.json.SearchResponseJson;
import com.sssprog.instagramtest.api.models.SearchItem;

import java.util.Arrays;
import java.util.List;

import rx.Observable;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchServiceTest extends AndroidTestCase {

    private SearchServiceImpl searchService;
    private InstagramClient client;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.setDexCachePath(getContext());
        TestUtils.setup(getContext());
        client = mock(InstagramClient.class);
        searchService = new SearchServiceImpl(client);
    }

    public void testReturnsEmptyListWhenDataNull() {
        when(client.search(anyString())).thenReturn(Observable.just(new SearchResponseJson()));
        RxAssertions.ObservableAssertions<List<SearchItem>> result = RxAssertions
                .subscribeAssertingThat(searchService.search(""))
                .hasSize(1);
        assertThat(result.first()).isEmpty();
    }

    public void testReturnsCorrectItems() {
        SearchResponseJson response = new SearchResponseJson();
        response.data = Arrays.asList(createItem("1"), createItem("2"));
        when(client.search(anyString())).thenReturn(Observable.just(response));
        RxAssertions.ObservableAssertions<List<SearchItem>> result = RxAssertions
                .subscribeAssertingThat(searchService.search(""))
                .hasSize(1);
        assertThat(result.first()).hasSize(2);
    }

    private SearchItemJson createItem(String id) {
        SearchItemJson result = new SearchItemJson();
        result.id = id;
        return result;
    }

    public void testErrorWhenHttpRequestFails() {
        when(client.search(anyString())).thenReturn(Observable.error(new Exception()));
        RxAssertions.subscribeAssertingThat(searchService.search(""))
                .assertError();
    }

}
