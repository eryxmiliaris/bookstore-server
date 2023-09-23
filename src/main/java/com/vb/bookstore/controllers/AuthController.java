package com.vb.bookstore.controllers;


import com.vb.bookstore.entities.ERole;
import com.vb.bookstore.entities.Role;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.payloads.LoginRequest;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.SignupRequest;
import com.vb.bookstore.payloads.UserDTO;
import com.vb.bookstore.repositories.RoleRepository;
import com.vb.bookstore.repositories.UserRepository;
import com.vb.bookstore.security.jwt.JwtUtil;
import com.vb.bookstore.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/signin")
    public ResponseEntity<UserDTO> authenticateUser(
            @Valid
            @RequestBody
            LoginRequest loginRequest
    ) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtil.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        UserDTO userDTO = modelMapper.map(userDetails, UserDTO.class);
        userDTO.setRoles(roles);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(userDTO);
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(
            @Valid
            @RequestBody
            SignupRequest signUpRequest
    ) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = modelMapper.map(signUpRequest, User.class);
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

    }

    @PostMapping("/signout")
    public ResponseEntity<MessageResponse> logoutUser() {
        ResponseCookie cookie = jwtUtil.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }
}

