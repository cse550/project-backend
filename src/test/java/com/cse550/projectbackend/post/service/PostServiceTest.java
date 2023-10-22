package com.cse550.projectbackend.post.service;

import com.cse550.projectbackend.post.error.PostNotFoundException;
import com.cse550.projectbackend.post.model.Post;
import com.cse550.projectbackend.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private Post testPost;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setUserID("user1");
        testPost.setContent("Test content");
        testPost.setLikeCount(0);
        testPost.setTimestamp(Instant.now());
    }

    @Test
    public void testCreatePost() {
        Mockito.when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Post createdPost = postService.createPost(testPost);

        assertNotNull(createdPost);
        assertEquals("user1", createdPost.getUserID());
    }

    @Test
    public void testGetPostById() {
        Mockito.when(postRepository.findById("1")).thenReturn(Optional.of(testPost));

        Post retrievedPost = postService.getPostById("1");

        assertNotNull(retrievedPost);
        assertEquals(retrievedPost, testPost);
    }

    @Test
    public void testGetPostByIdNotFound() {
        Mockito.when(postRepository.findById("2")).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.getPostById("2"));
    }

    @Test
    public void testDeletePost() {
        Mockito.when(postRepository.findById("1")).thenReturn(Optional.of(testPost));

        assertDoesNotThrow(() -> postService.deletePost("1"));
    }

    @Test
    public void testDeletePostNotFound() {
        Mockito.when(postRepository.findById("2")).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.deletePost("2"));
    }

    @Test
    public void testGetAllPosts() {
        List<Post> testPosts = new ArrayList<>();
        testPosts.add(testPost);

        Mockito.when(postRepository.findAll()).thenReturn(testPosts);

        List<Post> allPosts = postService.getAllPosts();

        assertNotNull(allPosts);
        assertEquals(1, allPosts.size());
    }
}
