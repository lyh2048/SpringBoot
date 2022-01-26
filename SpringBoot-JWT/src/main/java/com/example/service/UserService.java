package com.example.service;

import com.example.entity.User;

public interface UserService {
    User login(String username, String password);
}
