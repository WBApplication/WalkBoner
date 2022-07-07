package com.fusoft.walkboner.utils;

import com.fusoft.walkboner.models.Post;

import java.util.Comparator;

public class PostLikesComparator implements Comparator<Post> {
    public int compare(Post left, Post right) {
        return left.getPostLikes().size() - right.getPostLikes().size();
    }
}
