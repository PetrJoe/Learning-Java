package com.example.demo.service;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.JwtResponse;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public User register(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
        return userRepository.save(user);
    }

    public JwtResponse login(AuthRequest request) {
        // Try login by email
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail()) // Use email as username for Spring Security
                        .password(user.getPassword())
                        .roles(user.getRole().replace("ROLE_", ""))
                        .build();

                String jwt = jwtService.generateToken(userDetails);
                
                // Delete existing refresh tokens for user (optional, single session enforcement)
                refreshTokenService.deleteByUserId(user.getId());
                
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

                return new JwtResponse(
                        jwt,
                        refreshToken.getToken(),
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()
                );
            }
        }
        throw new RuntimeException("Invalid email or password");
    }
}
