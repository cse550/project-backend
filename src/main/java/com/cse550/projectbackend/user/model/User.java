package com.cse550.projectbackend.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userID;
    private String username;
    private String email;
    private String passwordHash;
    private String createdAt;
}
