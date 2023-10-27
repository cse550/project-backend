package com.cse550.projectbackend.post;

import com.cse550.projectbackend.post.error.PostNotFoundException;
import com.cse550.projectbackend.post.model.Post;
import com.cse550.projectbackend.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

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
    public void testCreatePost() throws Exception {
        Mockito.when(postService.createPost(Mockito.any(Post.class))).thenReturn(testPost);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/post")
                        .content("{\"userId\": \"user1\", \"content\": \"Test content\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.postId").value("1"));
    }

    @Test
    public void testGetPostById() throws Exception {
        Mockito.when(postService.getPostById("1")).thenReturn(testPost);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/post/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.postId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("user1"));
    }

    @Test
    public void testGetPostByIdNotFound() throws Exception {
        Mockito.when(postService.getPostById("2")).thenThrow(PostNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/post/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeletePost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/post/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testDeletePostNotFound() throws Exception {
        Mockito.doThrow(PostNotFoundException.class).when(postService).deletePost("2");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/post/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
