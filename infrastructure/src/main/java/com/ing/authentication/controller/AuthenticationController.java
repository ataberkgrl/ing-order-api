package com.ing.authentication.controller;

import com.ing.authentication.dto.AuthenticationResponse;
import com.ing.authentication.dto.LoginRequest;
import com.ing.authentication.dto.RegisterRequest;
import com.ing.authentication.util.JwtUtil;
import com.ing.user.model.User;
import com.ing.user.port.UserPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserPort userPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    UserPort userPort,
                                    PasswordEncoder passwordEncoder,
                                    JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userPort = userPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registrationRequest) {
        if (userPort.findByUsername(registrationRequest.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        User user = new User(
                UUID.randomUUID().toString(),
                registrationRequest.username(),
                passwordEncoder.encode(registrationRequest.password()),
                User.UserType.CUSTOMER
        );

        userPort.save(user);

        return ResponseEntity.ok("Registration successful. Please log in with your credentials.");
    }
}