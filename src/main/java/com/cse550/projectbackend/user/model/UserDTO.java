package com.cse550.projectbackend.user.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDTO {
    private String username;
    private String email;
    private String passwordHash;
    private List<String> following;
}
