package com.cse550.projectbackend.user.service;

import com.cse550.projectbackend.user.model.User;
import com.cse550.projectbackend.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public User saveUser(User user) {
        return user;
    }
}
