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

    @Autowired
    private EmailService emailService;

    @Autowired
    private BankingService bankingService;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthService.class);

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
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole("ROLE_USER");
        user.setEnabled(true); // User must verify email
        
        // Generate random 6-digit code
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        user.setVerificationCode(code);
        
        logger.info("Verification Code for {}: {}", user.getEmail(), code);
        
        User savedUser = userRepository.save(user);
        
        // Create default accounts for all currencies
        bankingService.createDefaultAccounts(savedUser);
        
        emailService.sendVerificationEmail(user.getEmail(), code);
        
        return savedUser;
    }

    public void verifyUser(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEnabled()) {
            throw new RuntimeException("User already verified");
        }

        if (code.equals(user.getVerificationCode())) {
            user.setEnabled(true);
            user.setVerificationCode(null);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Invalid verification code");
        }
    }

    public JwtResponse login(AuthRequest request) {
        // Try login by email
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            if (!user.isEnabled()) {
                throw new RuntimeException("Account is not verified. Please verify your email.");
            }

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
