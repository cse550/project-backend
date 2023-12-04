package com.cse550.projectbackend.user.model;

import lombok.Data;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;

@Data
@Document("user")
public class User {
    @MongoId
    private String id;
    @Indexed(unique = true)
    private String username;
    @Indexed(unique = true)
    private String email;
    private String passwordHash;
    private String bio;
    private Instant createdAt;
    private List<String> following;
}
