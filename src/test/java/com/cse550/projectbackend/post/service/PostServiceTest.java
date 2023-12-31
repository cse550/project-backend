package com.cse550.projectbackend.post.service;

import com.cse550.projectbackend.post.error.PostNotFoundException;
import com.cse550.projectbackend.post.model.Post;
import com.cse550.projectbackend.post.model.PostDTO;
import com.cse550.projectbackend.post.repository.PostRepository;
import com.cse550.projectbackend.user.model.User;
import com.cse550.projectbackend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    private Post testPost;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setUserId("user1");
        testPost.setContent("Test content");
        testPost.setLikeCount(0);
        testPost.setTimestamp(Instant.now());
    }

    @Test
    public void testCreatePost() {
        Mockito.when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Post createdPost = postService.createPost(testPost);

        assertNotNull(createdPost);
        assertEquals("user1", createdPost.getUserId());
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
    public void getPostByIdReturnsPostWhenFound() {
        String postId = "someId";
        Post post = new Post();
        post.setPostId(postId);
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(postId);

        assertEquals(post, result);
    }

    @Test
    public void getPostByIdThrowsExceptionWhenNotFound() {
        String postId = "someId";
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.getPostById(postId));
    }

    @Test
    public void getFeedPostsByUserIdTest() {
        User user2 = new User();
        user2.setId("user2Id");
        user2.setFollowing(List.of("user1Id"));
        Post post1 = createPost("post1Id", "user1Id", Instant.now().minusSeconds(10));
        Post post2 = createPost("post2Id", "user2Id", Instant.now().minusSeconds(5));

        Mockito.when(userService.getUser("user2Id")).thenReturn(user2);
        Mockito.when(postRepository.findByUserIdIn(List.of("user1Id"))).thenReturn(Arrays.asList(post1, post2));

        List<Post> feedPosts = postService.getFeedPostsByUserId("user2Id");
        assertEquals(2, feedPosts.size());
        assertEquals("post2Id", feedPosts.get(0).getPostId());
        assertEquals("post1Id", feedPosts.get(1).getPostId());
    }

    private Post createPost(String postId, String userId, Instant timestamp) {
        Post post = new Post();
        post.setPostId(postId);
        post.setUserId(userId);
        post.setTimestamp(timestamp);
        return post;
    }

    @Test
    public void testUpdatePost() {
        PostDTO postDTO = PostDTO.builder().build();
        postDTO.setContent("Updated content");
        postDTO.setLikeCount(10);

        Mockito.when(postRepository.findById("1")).thenReturn(Optional.of(testPost));
        Mockito.when(postRepository.save(any(Post.class))).then(returnsFirstArg());

        Post updatedPost = postService.updatePost("1", postDTO);

        assertNotNull(updatedPost);
        assertEquals("Updated content", updatedPost.getContent());
        assertEquals(10, updatedPost.getLikeCount());
        verify(postRepository).save(testPost);
    }

    @Test
    public void testUpdatePostNotFound() {
        PostDTO postDTO = PostDTO.builder().build();
        Mockito.when(postRepository.findById("2")).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.updatePost("2", postDTO));
    }
}
