package com.glentfoundation.polls.controller;

import com.glentfoundation.polls.exceptions.AppException;
import com.glentfoundation.polls.exceptions.ResourceNotFoundException;
import com.glentfoundation.polls.models.Role;
import com.glentfoundation.polls.models.RoleName;
import com.glentfoundation.polls.models.User;
import com.glentfoundation.polls.payload.requests.LoginRequest;
import com.glentfoundation.polls.payload.requests.SignUpRequest;
import com.glentfoundation.polls.payload.requests.responses.ApiResponse;
import com.glentfoundation.polls.payload.requests.responses.JwtAuthenticationResponse;
import com.glentfoundation.polls.repository.RoleRepositry;
import com.glentfoundation.polls.repository.UserRepository;
import com.glentfoundation.polls.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepositry roleRepositry;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt  = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ApiResponse(false,"username already taken"), HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false,"email already in use!"), HttpStatus.BAD_REQUEST);
        }

    User user = new User(signUpRequest.getName(), signUpRequest.getEmail(),
            signUpRequest.getUsername(), signUpRequest.getPassword());
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    Role userRole = roleRepositry.findByName(RoleName.ROLE_USER)
            .orElseThrow(() -> new AppException("User Role Not Set!!"));
    user.setRoles(Collections.singleton(userRole));
    User savedUser = userRepository.save(user);

    URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/users/{username}")
            .buildAndExpand(savedUser.getUsername()).toUri();
    return ResponseEntity.created(location).body(new ApiResponse(true,"User registered successfully"));

    }




}
