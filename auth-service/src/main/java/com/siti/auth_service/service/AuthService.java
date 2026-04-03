package com.siti.auth_service.service;

import com.siti.auth_service.dto.LoginRequest;
import com.siti.auth_service.dto.RegisterRequest;
import com.siti.auth_service.model.User;
import com.siti.auth_service.repository.UserRepository;
import com.siti.auth_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public String authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        return jwtUtil.generateToken(userDetails);
    }
    
    public User register(RegisterRequest registerRequest) {
        // Cek apakah username sudah ada
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username sudah terdaftar!");
        }
        
        // Cek apakah email sudah ada
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email sudah terdaftar!");
        }
        
        // Buat user baru
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setRole("USER");
        user.setIsActive(true);
        
        return userRepository.save(user);
    }
    
    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }
    
    public Boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}