package com.varunu28.userservice.controller;

import com.varunu28.userservice.dto.LoginRequest;
import com.varunu28.userservice.dto.SignupRequest;
import com.varunu28.userservice.security.CustomUserDetailsService;
import com.varunu28.userservice.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String X_FROM_GATEWAY_HEADER = "X-From-Gateway";
    private static final String X_FROM_GATEWAY_HEADER_VALUE = "true";
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(
        AuthenticationManager authenticationManager,
        JwtUtil jwtUtil,
        CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        String token = jwtUtil.generateToken(authentication.getName());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody SignupRequest signupRequest) {
        boolean isRegistered = userDetailsService.registerUser(signupRequest.username(), signupRequest.password());
        if (isRegistered) {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signupRequest.username(), signupRequest.password()));
            String token = jwtUtil.generateToken(authentication.getName());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(400).body("User already exists");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String token) {
        // Remove "Bearer " prefix if present
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        boolean isValid = jwtUtil.validateToken(token);
        if (isValid) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(X_FROM_GATEWAY_HEADER, X_FROM_GATEWAY_HEADER_VALUE);
            return ResponseEntity.ok().headers(headers).body(true);
        } else {
            return ResponseEntity.status(401).body(false);
        }
    }
}