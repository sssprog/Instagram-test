package com.sssprog.instagramtest.api.services;

import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.sssprog.instagramtest.R;
import com.sssprog.instagramtest.api.ApiUtils;
import com.sssprog.instagramtest.api.InstagramClient;
import com.sssprog.instagramtest.api.SimpleRxSubscriber;
import com.sssprog.instagramtest.api.database.Comment;
import com.sssprog.instagramtest.api.database.DatabaseHelper;
import com.sssprog.instagramtest.api.database.Post;
import com.sssprog.instagramtest.api.json.CommentJson;
import com.sssprog.instagramtest.api.json.PostJson;
import com.sssprog.instagramtest.api.models.PostWithComments;
import com.sssprog.instagramtest.api.models.RecentItemsResponse;
import com.sssprog.instagramtest.utils.Prefs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PostServiceImpl implements PostService {

    private static final int ITEM_LIMIT = 10;
    private static final int MAX_COMMENTS = 5;

    private AtomicInteger lastRequestId = new AtomicInteger();

    @Override
    public Observable<RecentItemsResponse> getItems(final boolean fromStart) {
        final int requestId = lastRequestId.incrementAndGet();
        String lastId = fromStart ? null : Prefs.getString(R.string.pref_last_item_id);
        return InstagramClient.getInstance().getRecentItems(lastId, ITEM_LIMIT)
                .map(json -> {
                    if (requestId != lastRequestId.get()) {
                        throw new RuntimeException("abort request");
                    }
                    ArrayList<PostJson> jsonItems = json.data != null ? json.data : new ArrayList<>();
                    List<Post> items = saveItems(fromStart, jsonItems);
                    return new RecentItemsResponse(items, false, items.size() < ITEM_LIMIT);
                })
                .onErrorResumeNext(throwable -> {
                    if (requestId != lastRequestId.get()) {
                        throw new RuntimeException("abort request");
                    }
                    try {
                        List<Post> items = fromStart ? getItemsFromDb() : new ArrayList<>();
                        return Observable.just(new RecentItemsResponse(items, true, true));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private List<Post> saveItems(final boolean firstPage, final List<PostJson> jsonItems) {
        return ApiUtils.callInTransaction(() -> {
            if (firstPage) {
                DatabaseHelper.getInstance().clearTable(Post.class);
            }
            List<Post> result = new ArrayList<>();
            Dao<Post, Long> dao = DatabaseHelper.getInstance().getPostDao();
            for (PostJson json : jsonItems) {
                if (json.id != null) {
                    Prefs.putString(R.string.pref_last_item_id, json.id);
                }
                if (!json.isValid() || !TextUtils.equals(json.type, PostJson.TYPE_IMAGE)) {
                    continue;
                }
                Post post = new Post();
                post.setServerId(json.id);
                post.setDescription(json.caption != null ? json.caption.text : null);
                post.setThumbnail(json.images.thumbnail.url);
                post.setLowResolutionImage(json.images.lowResolution.url);
                post.setImage(json.images.main.url);
                dao.create(post);
                result.add(post);
            }
            return result;
        });
    }

    private List<Post> getItemsFromDb() throws SQLException {
        return DatabaseHelper.getInstance().getPostDao()
                .queryBuilder()
                .orderBy(Post.FIELD_ID, true)
                .query();
    }

    @Override
    public void clearCache() {
        Observable
                .create((Subscriber<? super Void> subscriber) -> {
                    ApiUtils.executeWithRuntimeException(() -> DatabaseHelper.getInstance().clearTable(Post.class));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleRxSubscriber<Void>());
    }

    @Override
    public Observable<PostWithComments> getPostWithComments(long postId) {
        return getItem(postId)
                .flatMap(this::loadComments)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<PostWithComments> loadComments(final Post post) {
        return InstagramClient.getInstance().getComments(post.getServerId())
                .map(response -> {
                    List<Comment> comments = saveCommentsToDb(post.getId(),
                            response.data != null ? response.data : new ArrayList<CommentJson>());
                    return new PostWithComments(post, comments);
                })
                .onErrorResumeNext(throwable -> {
                    try {
                        return Observable.just(new PostWithComments(post, getCommentsFromDb(post.getId())));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private List<Comment> saveCommentsToDb(final long postId, final List<CommentJson> jsonItems) {
        return ApiUtils.callInTransaction(() -> {
            deleteComments(postId);
            Dao<Comment, Long> dao = DatabaseHelper.getInstance().getCommentDao();
            List<Comment> result = new ArrayList<>();
            int count = Math.min(jsonItems.size(), MAX_COMMENTS);
            for (int i = 0; i < count; i++) {
                CommentJson json = jsonItems.get(i);
                if (!json.isValid()) {
                    continue;
                }
                Comment comment = new Comment();
                comment.setPostId(postId);
                comment.setText(json.text);
                comment.setUserName(json.from.userName);
                dao.create(comment);
                result.add(comment);
            }
            return result;
        });
    }

    private List<Comment> getCommentsFromDb(long postId) throws SQLException {
        return DatabaseHelper.getInstance().getCommentDao()
                .queryBuilder()
                .where().eq(Comment.FIELD_POST_ID, postId)
                .query();
    }

    private void deleteComments(long postId) throws SQLException {
        DeleteBuilder<Comment, Long> query = DatabaseHelper.getInstance().getCommentDao().deleteBuilder();
        query.where().eq(Comment.FIELD_POST_ID, postId);
        query.delete();
    }

    private Observable<Post> getItem(final long postId) {
        return Observable
                .create((final Subscriber<? super Post> subscriber) -> {
                    ApiUtils.executeWithRuntimeException(() -> {
                        Post item = DatabaseHelper.getInstance().getPostDao().queryForId(postId);
                        if (item == null) {
                            throw new RuntimeException("no such item in the DB");
                        }
                        subscriber.onNext(item);
                        subscriber.onCompleted();
                    });
                });
    }

}
