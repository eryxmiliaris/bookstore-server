package com.vb.bookstore.services.impl;

import com.vb.bookstore.entities.Role;
import com.vb.bookstore.entities.RoleEnum;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.user.UpdateUserInfoRequest;
import com.vb.bookstore.payloads.user.UpdateUserInfoResponse;
import com.vb.bookstore.payloads.user.UserDTO;
import com.vb.bookstore.repositories.RoleRepository;
import com.vb.bookstore.repositories.UserRepository;
import com.vb.bookstore.security.jwt.JwtUtil;
import com.vb.bookstore.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();
    }

    public boolean currentUserIsAdmin() {
        boolean isAdmin = false;
        Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleEnum.ROLE_ADMIN.toString()));
        try {
            User user = getCurrentUser();
            if (user.getRoles().contains(adminRole)) {
                isAdmin = true;
            }
        } catch (Exception e) {

        }
        return isAdmin;
    }

    public UserDTO getUserInfo() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        userDTO.setRoles(roles);

        return userDTO;
    }

    public UpdateUserInfoResponse updateUserInfo(UpdateUserInfoRequest request) {
        User user = getCurrentUser();

        if (!Objects.equals(request.getUsername(), user.getUsername())) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return new UpdateUserInfoResponse(null, new MessageResponse(false, "Username is already taken"));
            }
        }

        if (!Objects.equals(request.getEmail(), user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return new UpdateUserInfoResponse(null, new MessageResponse(false, "Email is already taken"));
            }
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setBirthDate(request.getBirthDate());

        userRepository.save(user);

        ResponseCookie jwtCookie = jwtUtil.generateJwtCookieFromUsername(request.getUsername());

        return new UpdateUserInfoResponse(jwtCookie, new MessageResponse(true, "User info has been updated"));
    }
}
