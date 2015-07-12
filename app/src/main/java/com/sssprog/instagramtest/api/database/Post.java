package com.sssprog.instagramtest.api.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Post.TABLE_NAME)
public class Post {

    public static final String TABLE_NAME = "post";
    public static final String FIELD_ID = "id";
    public static final String FIELD_SERVER_ID = "server_id";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_THUMBNAIL = "thumbnail";
    public static final String FIELD_LOW_RESOLUTION_IMAGE = "low_resolution_image";
    public static final String FIELD_IMAGE = "image";
    public static final String FIELD_CREATION_TIME = "creation_time";

    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;

    @DatabaseField(columnName = FIELD_SERVER_ID)
    private String serverId;

    @DatabaseField(columnName = FIELD_DESCRIPTION)
    private String description;

    @DatabaseField(columnName = FIELD_THUMBNAIL)
    private String thumbnail;

    @DatabaseField(columnName = FIELD_LOW_RESOLUTION_IMAGE)
    private String lowResolutionImage;

    @DatabaseField(columnName = FIELD_IMAGE)
    private String image;

    @DatabaseField(columnName = FIELD_CREATION_TIME)
    private long creationTime;

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getLowResolutionImage() {
        return lowResolutionImage;
    }

    public void setLowResolutionImage(String lowResolutionImage) {
        this.lowResolutionImage = lowResolutionImage;
    }
}
