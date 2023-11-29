package com.cse550.projectbackend.post.service;

import com.cse550.projectbackend.post.error.PostNotFoundException;
import com.cse550.projectbackend.post.model.Post;
import com.cse550.projectbackend.post.model.PostDTO;
import com.cse550.projectbackend.post.repository.PostRepository;
import com.cse550.projectbackend.user.model.User;
import com.cse550.projectbackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;

    private final UserService userService;

    public Post createPost(Post post) {
        try {
            post.setPostId(UUID.randomUUID().toString());
            post.setTimestamp(Instant.now());
            post.setUsername(post.getUsername());
            post = postRepository.save(post);
            log.info("Post created at {}", post.getTimestamp());
            return post;
        } catch (DataAccessException e) {
            log.error("Error when creating a new post: ", e);
            throw new RuntimeException("Failed to create post", e);
        }
    }

    public Post getPostById(String postId) {
        return getPostOrThrow(postId);
    }

    public void deletePost(String postId) {
        getPostOrThrow(postId);

        try {
            postRepository.deleteById(postId);
        } catch (DataAccessException e) {
            log.error("Error when deleting post with id {}: ", postId, e);
            throw new RuntimeException("Failed to delete post", e);
        }
    }

    public List<Post> getFeedPostsByUserId(String userId) {
        User user = userService.getUser(userId);

        List<String> followingIds = user.getFollowing();

        return postRepository.findByUserIdIn(followingIds)
                .stream()
                .sorted(Comparator.comparing(Post::getTimestamp).reversed())
                .collect(Collectors.toList());
    }


    public Post updatePost(String postId, PostDTO postDTO) {
       Post post = getPostOrThrow(postId);

        if (postDTO.getContent() != null) {
            post.setContent(postDTO.getContent());
        }
        if (postDTO.getLikeCount() > post.getLikeCount()) {
            post.setLikeCount(postDTO.getLikeCount());
        }

        return postRepository.save(post);
    }

    private Post getPostOrThrow(String postId) {
        return postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with id {} not found", postId);
            return new PostNotFoundException(postId);
        });
    }
}
