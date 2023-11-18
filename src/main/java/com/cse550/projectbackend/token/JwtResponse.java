package com.cse550.projectbackend.token;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {
    private final String token;
    private final String type = "Bearer";
}
