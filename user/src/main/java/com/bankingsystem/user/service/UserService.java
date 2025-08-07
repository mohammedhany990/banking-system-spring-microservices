package com.bankingsystem.user.service;

import com.bankingsystem.user.dto.LoginRequest;
import com.bankingsystem.user.dto.LoginResponse;
import com.bankingsystem.user.dto.RegisterRequest;
import com.bankingsystem.user.dto.UserResponse;

public interface UserService {
    UserResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
    boolean isUsernameAvailable(String username);
}
