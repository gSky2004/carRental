package com.oop.carrental.service.impl;

import com.oop.carrental.dto.LoginRequest;
import com.oop.carrental.dto.LoginResponse;
import com.oop.carrental.dto.RegisterRequest;
import com.oop.carrental.entity.Admin;
import com.oop.carrental.repository.AdminRepository;
import com.oop.carrental.service.AuthService;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements AuthService {

    private final AdminRepository adminRepository;
    private final ConcurrentHashMap<String, Long> tokenStore = new ConcurrentHashMap<>();

    public AuthServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        String hashedInput = hashPassword(request.getPassword());

        if (!admin.getPassword().equals(hashedInput)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        String token = UUID.randomUUID().toString();
        tokenStore.put(token, admin.getId());

        return new LoginResponse(token, admin.getFullName(), admin.getUsername());

    }

    @Override
    public LoginResponse register(RegisterRequest request) {

        if (adminRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken.");
        }

        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setPassword(hashPassword(request.getPassword()));
        admin.setFullName(request.getFullName());

        Admin saved = adminRepository.save(admin);

        String token = UUID.randomUUID().toString();
        tokenStore.put(token, saved.getId());

        return new LoginResponse(token, saved.getFullName(), saved.getUsername());

    }

    @Override
    public boolean validateToken(String token) {
        return token != null && tokenStore.containsKey(token);
    }

    @Override
    public LoginResponse getAdminByToken(String token) {

        Long adminId = tokenStore.get(token);

        if (adminId == null) {
            return null;
        }

        Admin admin = adminRepository.findById(adminId).orElse(null);

        if (admin == null) {
            tokenStore.remove(token);
            return null;
        }

        return new LoginResponse(token, admin.getFullName(), admin.getUsername());

    }

    @Override
    public void logout(String token) {
        if (token != null) {
            tokenStore.remove(token);
        }
    }

    public static String hashPassword(String password) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }

    }

}
