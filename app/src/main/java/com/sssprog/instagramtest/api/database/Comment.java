package com.sssprog.instagramtest.api.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Comment.TABLE_NAME)
public class Comment {

    public static final String TABLE_NAME = "comment";
    public static final String FIELD_ID = "id";
    public static final String FIELD_POST_ID = "post_id";
    public static final String FIELD_TEXT = "text";
    public static final String FIELD_USER_NAME = "user_name";

    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;

    @DatabaseField(columnName = FIELD_POST_ID)
    private long postId;

    @DatabaseField(columnName = FIELD_TEXT)
    private String text;

    @DatabaseField(columnName = FIELD_USER_NAME)
    private String userName;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }
}
