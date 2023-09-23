package com.vb.bookstore.controllers;


import com.vb.bookstore.payloads.LoginRequest;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.SignupRequest;
import com.vb.bookstore.payloads.UserDTO;
import com.vb.bookstore.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<UserDTO> authenticateUser(
            @Valid
            @RequestBody
            LoginRequest loginRequest
    ) {
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(
            @Valid
            @RequestBody
            SignupRequest signUpRequest
    ) {
        return authService.registerUser(signUpRequest);
    }

    @PostMapping("/signout")
    public ResponseEntity<MessageResponse> logoutUser() {
        return authService.logoutUser();
    }
}

