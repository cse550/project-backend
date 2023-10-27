package com.cse550.projectbackend.post.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("post")
public class Post {
    @Id
    private String postId;
    private String userID;
    private String content;
    private int likeCount;
    private Instant timestamp;
}
