package com.sssprog.instagramtest.api.json;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class CommentJson {

    public String text;
    public CommentOwnerJson from;

    public boolean isValid() {
        return !TextUtils.isEmpty(text) &&
                from != null && !TextUtils.isEmpty(from.userName);
    }

    public static class CommentOwnerJson {
        @SerializedName("username")
        public String userName;
    }

}
