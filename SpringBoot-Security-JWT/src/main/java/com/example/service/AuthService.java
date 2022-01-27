package com.example.service;

import com.example.entity.User;

public interface AuthService {
    String register(User user);
    String login(String username, String password);
}
