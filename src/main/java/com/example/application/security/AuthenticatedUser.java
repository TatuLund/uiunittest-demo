package com.example.application.security;

import java.util.Optional;

import com.example.application.data.entity.User;

public interface AuthenticatedUser {

    public Optional<User> get();
    
    public void logout();
}
