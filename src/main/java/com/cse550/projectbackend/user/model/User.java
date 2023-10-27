package com.cse550.projectbackend.user.model;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document("user")
public class User {
    @Id
    private String userId;
    private String username;
    private String email;
    private String passwordHash;
    private Instant createdAt;
    private List<User> following;
}
