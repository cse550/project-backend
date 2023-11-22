package com.cse550.projectbackend.post.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PostDTO {
    private String content;
    private int likeCount;
}
