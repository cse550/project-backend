package com.cse550.projectbackend.post.error;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(String postId) {
        super("Post with ID " + postId + " not found.");
    }
}