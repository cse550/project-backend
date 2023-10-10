package com.cse550.projectbackend.post.model;

import java.time.Instant;

import lombok.Data;

@Data
public class Post {
    private String postId;
    private String userID;
    private String content;
    private int likeCount;
    private Instant timestamp;
}
