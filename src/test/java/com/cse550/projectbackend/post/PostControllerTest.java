package com.cse550.projectbackend.post;

import com.cse550.projectbackend.post.error.PostNotFoundException;
import com.cse550.projectbackend.post.model.Post;
import com.cse550.projectbackend.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private Post testPost;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setPostId("1");
        testPost.setUserId("user1");
        testPost.setContent("Test content");
        testPost.setLikeCount(0);
        testPost.setTimestamp(Instant.now());
    }

    @Test
    public void testCreatePost() {
        when(postService.createPost(any(Post.class))).thenReturn(testPost);

        ResponseEntity<Post> response = postController.createPost(testPost);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("1", Objects.requireNonNull(response.getBody()).getPostId());
        verify(postService, times(1)).createPost(any(Post.class));
    }
    @Test
    public void testGetPostById() throws PostNotFoundException {
        when(postService.getPostById("1")).thenReturn(testPost);

        ResponseEntity<Post> response = postController.getPostById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("1", Objects.requireNonNull(response.getBody()).getPostId());
        verify(postService, times(1)).getPostById("1");
    }

    @Test
    public void testGetPostByIdNotFound() throws PostNotFoundException {
        when(postService.getPostById("2")).thenThrow(PostNotFoundException.class);

        ResponseEntity<Post> response = postController.getPostById("2");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(postService, times(1)).getPostById("2");
    }

    @Test
    public void testDeletePost() throws PostNotFoundException {
        doNothing().when(postService).deletePost("1");

        ResponseEntity<Void> response = postController.deletePost("1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(postService, times(1)).deletePost("1");
    }

    @Test
    public void testDeletePostNotFound() throws PostNotFoundException {
        doThrow(PostNotFoundException.class).when(postService).deletePost("2");

        ResponseEntity<Void> response = postController.deletePost("2");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(postService, times(1)).deletePost("2");
    }

    @Test
    public void testGetUserFeed() {
        List<Post> feed = new ArrayList<>();
        feed.add(testPost);

        when(postService.getFeedPostsByUserId("user1")).thenReturn(feed);

        ResponseEntity<List<Post>> response = postController.getUserFeed("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals("1", response.getBody().get(0).getPostId());
        verify(postService, times(1)).getFeedPostsByUserId("user1");
    }
}
