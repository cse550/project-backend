package com.cse550.projectbackend.user.model;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("user")
public class User {
    @Id
    private String userID;
    private String username;
    private String email;
    private String passwordHash;
    private String createdAt;
}
