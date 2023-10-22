package com.cse550.projectbackend.post.service;

import com.cse550.projectbackend.post.error.PostNotFoundException;
import com.cse550.projectbackend.post.model.Post;
import com.cse550.projectbackend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;

    public Post createPost(Post post) {
        try {
            post.setPostId(UUID.randomUUID().toString());
            post.setTimestamp(Instant.now());
            postRepository.save(post);
            return post;
        } catch (DataAccessException e) {
            log.error("Error when creating a new post: ", e);
            throw new RuntimeException("Failed to create post", e);
        }
    }

    public Post getPostById(String postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            log.error("Post with id {} not found", postId);
            throw new PostNotFoundException(postId);
        }
        return postOptional.get();
    }

    public void deletePost(String postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            log.error("Post with id {} not found", postId);
            throw new PostNotFoundException(postId);
        }

        try {
            postRepository.deleteById(postId);
        } catch (DataAccessException e) {
            log.error("Error when deleting post with id {}: ", postId, e);
            throw new RuntimeException("Failed to delete post", e);
        }
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
}
