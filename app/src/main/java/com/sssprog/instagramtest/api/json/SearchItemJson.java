package com.sssprog.instagramtest.api.json;

import com.google.gson.annotations.SerializedName;

public class SearchItemJson {

    public String id;
    @SerializedName("username")
    public String userName;
    @SerializedName("profile_picture")
    public String image;
    @SerializedName("full_name")
    public String fullName;

}
