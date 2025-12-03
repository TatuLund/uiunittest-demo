package com.example.application.security;

import java.io.Serializable;
import java.util.Optional;

import com.example.application.data.entity.User;

public interface AuthenticatedUser extends Serializable {

    public Optional<User> get();
    
    public void logout();
}
