package com.vb.bookstore.controllers;

import com.vb.bookstore.entities.RoleEnum;
import com.vb.bookstore.entities.Role;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.payloads.auth.LoginRequest;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.auth.ResetRequest;
import com.vb.bookstore.payloads.auth.SignupRequest;
import com.vb.bookstore.payloads.auth.UserDTO;
import com.vb.bookstore.repositories.RoleRepository;
import com.vb.bookstore.repositories.UserRepository;
import com.vb.bookstore.security.jwt.JwtUtil;
import com.vb.bookstore.security.services.UserDetailsImpl;
import com.vb.bookstore.services.EmailService;
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
import org.springframework.security.authentication.BadCredentialsException;
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
    private final EmailService emailService;

    @PostMapping("/signin")
    public ResponseEntity<UserDTO> authenticateUser(
            @Valid
            @RequestBody
            LoginRequest loginRequest
    ) {
        String username;
        if (loginRequest.getLogin().contains("@")) {
            User user = userRepository.findByEmail(loginRequest.getLogin())
                    .orElseThrow(() -> new BadCredentialsException("Bad credentials"));
            username = user.getUsername();
        }  else {
            username = loginRequest.getLogin();
        }
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));

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
            return new ResponseEntity<>(new MessageResponse(false, "Username is already taken!"), HttpStatus.CONFLICT);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new MessageResponse(false, "Email is already in use!"), HttpStatus.CONFLICT);
        }

        User user = modelMapper.map(signUpRequest, User.class);
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role is not found."));
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

    @PostMapping("/forgot")
    public ResponseEntity<MessageResponse> forgotPassword(
            @RequestParam String email
    ) {
        Optional<User> optional = userRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse(false, "There is no user with given email"), HttpStatus.NOT_FOUND);
        }

        User user = optional.get();
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);

        emailService.sendSimpleMail(email, "To reset your password, click the link below: \n" + "http://localhost:5173/reset?token=" + resetToken, "Password Reset Request");
        return ResponseEntity.ok()
                .body(new MessageResponse(true, "A password reset link has been sent to: " + email));
    }

    @PostMapping("/reset")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetRequest request
    ) {
        Optional<User> optional = userRepository.findByResetToken(request.getToken());
        if (optional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse(false, "Reset token is invalid"), HttpStatus.NOT_FOUND);
        }

        User user = optional.get();
        user.setPassword(encoder.encode(request.getPassword()));
        user.setResetToken(null);
        userRepository.save(user);

        return ResponseEntity.ok()
                .body(new MessageResponse(true, "Password was successfully reset"));
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

