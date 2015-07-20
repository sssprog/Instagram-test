package com.sssprog.instagramtest.api.services;

import android.test.AndroidTestCase;

import com.sssprog.instagramtest.Config;
import com.sssprog.instagramtest.R;
import com.sssprog.instagramtest.RxAssertions;
import com.sssprog.instagramtest.TestUtils;
import com.sssprog.instagramtest.api.InstagramClient;
import com.sssprog.instagramtest.api.database.DatabaseHelper;
import com.sssprog.instagramtest.api.database.Post;
import com.sssprog.instagramtest.api.json.PostJson;
import com.sssprog.instagramtest.api.json.RecentResponseJson;
import com.sssprog.instagramtest.api.models.RecentItemsResponse;
import com.sssprog.instagramtest.utils.Prefs;

import junit.framework.AssertionFailedError;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostServiceTest extends AndroidTestCase {

    private static final String LAST_ITEM_ID = "LAST_ITEM_ID";
    private static final String ITEM_ID1 = "1";

    private PostServiceImpl postService;
    private InstagramClient client;
    private DatabaseHelper database;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.setDexCachePath(getContext());
        TestUtils.setup(getContext());
        client = mock(InstagramClient.class);
        database = Config.appComponent().database();
        postService = new PostServiceImpl(client, database);
        Prefs.putString(R.string.pref_last_item_id, LAST_ITEM_ID);
    }

    private RxAssertions.ObservableAssertions<RecentItemsResponse> getItems(boolean fromStart) {
        return RxAssertions.subscribeAssertingThat(postService.getItems(fromStart));
    }

    public void testReturnsEmptyListWhenDataNull() {
        when(client.getRecentItems(anyString(), anyInt())).thenReturn(Observable.just(new RecentResponseJson()));
        assertThat(getItems(true).single().items).isEmpty();
    }

    public void testFromStartDoesNotUseLastId() {
        when(client.getRecentItems(anyString(), anyInt())).thenReturn(Observable.just(createResponse(0)));
        getItems(true);
        verify(client).getRecentItems(eq(null), anyInt());
    }

    public void testUsesLastItemIdWhenLoadingNotFirstPage() {
        when(client.getRecentItems(anyString(), anyInt())).thenReturn(Observable.just(createResponse(0)));
        getItems(false);
        verify(client).getRecentItems(eq(LAST_ITEM_ID), anyInt());
    }

    public void testReturnsAllLoadedWhenThereIsLessObjectsThenCount() {
        when(client.getRecentItems(anyString(), anyInt())).thenReturn(Observable.just(createResponse(1)));
        assertThat(getItems(true).single().allLoaded).isTrue();
    }

    public void testReturnsAllLoadedFalseWhenThereIsMoreObjectsThenCount() {
        when(client.getRecentItems(anyString(), anyInt())).thenReturn(Observable.just(createResponse(100)));
        assertThat(getItems(true).single().allLoaded).isFalse();
    }

    public void testCorrectNumberOfObjects() {
        when(client.getRecentItems(anyString(), anyInt())).thenReturn(Observable.just(createResponse(20)));
        assertThat(getItems(true).single().items).hasSize(20);
    }

    public void testUpdatesLastItemIdAfterRequest() {
        when(client.getRecentItems(anyString(), anyInt())).thenReturn(Observable.just(createResponse(20)));
        getItems(true);
        assertThat(Prefs.getString(R.string.pref_last_item_id)).isEqualTo("19");
    }

    public void testFromDbFalse() {
        when(client.getRecentItems(anyString(), anyInt())).thenReturn(Observable.just(createResponse(0)));
        assertThat(getItems(true).single().fromDb).isFalse();
    }

    public void testReturnsItemsFromDbWhenHttpRequestFails() {
        when(client.getRecentItems(anyString(), anyInt())).thenReturn(Observable.error(new Exception()));
        addDbItems(20);
        RxAssertions.ObservableAssertions<RecentItemsResponse> result = getItems(true);
        assertThat(result.single().fromDb).isTrue();
        assertThat(result.single().allLoaded).isTrue();
        assertThat(result.single().items).hasSize(20);
    }

    private void addDbItems(int count) {
        for (int i = 0; i < count; i++) {
            try {
                database.getPostDao().create(createDbItem("" + i));
            } catch (SQLException e) {
                throw new AssertionFailedError("db error");
            }
        }
    }

    private Post createDbItem(String id) {
        Post item = new Post();
        item.setServerId(id);
        return item;
    }

    private PostJson createItem(String id) {
        PostJson item = new PostJson();
        item.id = id;
        item.images = new PostJson.ImagesJson();
        item.images.lowResolution = new PostJson.ImageJson();
        item.images.lowResolution.url = "1";
        item.images.thumbnail = new PostJson.ImageJson();
        item.images.thumbnail.url = "1";
        item.images.main = new PostJson.ImageJson();
        item.images.main.url = "1";
        item.type = PostJson.TYPE_IMAGE;
        return item;
    }

    private RecentResponseJson createResponse(int size) {
        RecentResponseJson result = new RecentResponseJson();
        result.data = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.data.add(createItem("" + i));
        }
        return result;
    }

    private List<Post> getItemsFromDb() {
        try {
            return database.getPostDao().queryForAll();
        } catch (SQLException e) {
            throw new AssertionFailedError("couldn't get items from db");
        }
    }

}
