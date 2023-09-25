package com.vb.bookstore.controllers;

import com.vb.bookstore.entities.RoleEnum;
import com.vb.bookstore.entities.Role;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.payloads.auth.LoginRequest;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.auth.SignupRequest;
import com.vb.bookstore.payloads.auth.UserDTO;
import com.vb.bookstore.repositories.RoleRepository;
import com.vb.bookstore.repositories.UserRepository;
import com.vb.bookstore.security.jwt.JwtUtil;
import com.vb.bookstore.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
            return new ResponseEntity<>(new MessageResponse(false, "Error: Username is already taken!"), HttpStatus.CONFLICT);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new MessageResponse(false, "Error: Email is already in use!"), HttpStatus.CONFLICT);
        }

        // Create new user's account
        User user = modelMapper.map(signUpRequest, User.class);
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok()
                .body(new MessageResponse(true, "User registered successfully!"));

    }

    @PostMapping("/signout")
    public ResponseEntity<MessageResponse> logoutUser() {
        ResponseCookie cookie = jwtUtil.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse(true, "You've been signed out!"));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> getUserInfo(HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDTO userDTO = modelMapper.map(userDetails, UserDTO.class);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        userDTO.setRoles(roles);

        return ResponseEntity.ok()
                .body(userDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setSuccess(false);
        messageResponse.setMessage("Validation failed");
        messageResponse.setErrors(errors);
        return ResponseEntity.badRequest()
                .body(messageResponse);
    }
}

