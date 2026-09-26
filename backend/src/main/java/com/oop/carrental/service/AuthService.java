package com.oop.carrental.service;

import com.oop.carrental.dto.LoginRequest;
import com.oop.carrental.dto.LoginResponse;
import com.oop.carrental.dto.RegisterRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse register(RegisterRequest request);

    boolean validateToken(String token);

    LoginResponse getAdminByToken(String token);

    void logout(String token);

}
