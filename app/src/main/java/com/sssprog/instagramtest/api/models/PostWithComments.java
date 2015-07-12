package com.sssprog.instagramtest.api.models;

import com.sssprog.instagramtest.api.database.Comment;
import com.sssprog.instagramtest.api.database.Post;

import java.util.List;

public class PostWithComments {

    public final Post post;
    public final List<Comment> comments;

    public PostWithComments(Post post, List<Comment> comments) {
        this.post = post;
        this.comments = comments;
    }
}
