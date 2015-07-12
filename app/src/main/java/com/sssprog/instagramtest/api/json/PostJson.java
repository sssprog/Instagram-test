package com.sssprog.instagramtest.api.json;

import com.google.gson.annotations.SerializedName;

public class PostJson {

    public static final String TYPE_IMAGE = "image";

    public String id;
    public String type;
    public CaptionJson caption;
    @SerializedName("created_time")
    private long creationTime;
    public ImagesJson images;

    public long getCreationTime() {
        return creationTime * 1000;
    }

    public boolean isValid() {
        return images != null &&
                images.thumbnail != null && images.thumbnail.url != null &&
                images.main != null && images.main.url != null &&
                images.lowResolution != null && images.lowResolution.url != null;
    }

    public static class CaptionJson {
        public String text;
    }

    public static class ImagesJson {
        public ImageJson thumbnail;
        @SerializedName("standard_resolution")
        public ImageJson main;
        @SerializedName("low_resolution")
        public ImageJson lowResolution;
    }

    public static class ImageJson {
        public String url;
    }

}
