package com.vb.bookstore.controllers;

import com.vb.bookstore.entities.Cart;
import com.vb.bookstore.entities.Role;
import com.vb.bookstore.entities.Roles;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.auth.LoginRequest;
import com.vb.bookstore.payloads.auth.ResetRequest;
import com.vb.bookstore.payloads.auth.SignupRequest;
import com.vb.bookstore.payloads.user.UserDTO;
import com.vb.bookstore.repositories.RoleRepository;
import com.vb.bookstore.repositories.UserRepository;
import com.vb.bookstore.security.jwt.JwtUtil;
import com.vb.bookstore.security.services.UserDetailsImpl;
import com.vb.bookstore.services.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${bookstore.client.base.url}")
    private String BASE_URL;

    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        ResponseCookie jwtCookie = jwtUtil.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
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
        Role userRole = roleRepository.findByName(Roles.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role is not found."));
        roles.add(userRole);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalPrice(BigDecimal.valueOf(0));
        cart.setHasPromoCode(false);
        cart.setHasPaperBooks(false);

        user.setRoles(roles);
        user.setCart(cart);
        user.setHasActiveSubscription(false);
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

        emailService.sendSimpleMail(email, "To reset your password, click the link below: \n" + BASE_URL + "/reset?token=" + resetToken, "Password Reset Request");
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
}

