package com.paypal.user_service.controller;

import com.paypal.user_service.dto.JwtResponse;
import com.paypal.user_service.dto.LoginRequest;
import com.paypal.user_service.dto.SignupRequest;
import com.paypal.user_service.entity.User;
import com.paypal.user_service.repository.UserRepository;
import com.paypal.user_service.utils.JWTUtil;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        Optional<User> existingUser = userRepository.findByEmail(signupRequest.getEmail());
        if(existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }
        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setRole("ROLE_USER"); // Normal users only!
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok("User Registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("❌ User not found");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("❌ Invalid credentials");
        }

        // Add role to claims
        Map<String,Object> claims = new HashMap<>();
        claims.put("role",user.getRole());


        // Generate token with claims
        String token = jwtUtil.generateToken(claims, user.getEmail());

        return ResponseEntity.ok(new JwtResponse(token));
    }
}
