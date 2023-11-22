package com.vb.bookstore.controllers;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.user.AddressDTO;
import com.vb.bookstore.payloads.user.UpdateUserInfoRequest;
import com.vb.bookstore.payloads.user.UpdateUserInfoResponse;
import com.vb.bookstore.payloads.user.UserDTO;
import com.vb.bookstore.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> getUserInfo() {
        UserDTO userDTO = userService.getUserInfo();
        return ResponseEntity.ok()
                .body(userDTO);
    }

    @PutMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> updateUserInfo(
            @Valid
            @RequestBody
            UpdateUserInfoRequest request
    ) throws ParseException {
        UpdateUserInfoResponse updateUserInfoResponse = userService.updateUserInfo(request);

        if (updateUserInfoResponse.getJwtCookie() == null) {
            return ResponseEntity.badRequest().body(updateUserInfoResponse.getMessageResponse());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, updateUserInfoResponse.getJwtCookie().toString())
                .body(updateUserInfoResponse.getMessageResponse());
    }

    @GetMapping("/addresses")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addressDTOS = userService.getAllAddresses();

        return ResponseEntity.ok(addressDTOS);
    }

    @PostMapping("/addresses")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> addAddress(
            @Valid
            @RequestBody
            AddressDTO request
    ) {
        MessageResponse messageResponse = userService.addAddress(request);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/addresses/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteAddress(@PathVariable Long id) {
        MessageResponse messageResponse = userService.deleteAddress(id);
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping("/addresses/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> updateAddress(
            @Valid @RequestBody AddressDTO newAddress,
            @PathVariable Long id
    ) {
        MessageResponse messageResponse = userService.updateAddress(newAddress, id);
        return ResponseEntity.ok(messageResponse);
    }
}
