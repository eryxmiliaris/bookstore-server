package com.vb.bookstore.controllers;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.auth.UserDTO;
import com.vb.bookstore.payloads.user.AddressDTO;
import com.vb.bookstore.payloads.user.UpdateUserInfoRequest;
import com.vb.bookstore.payloads.user.UpdateUserInfoResponse;
import com.vb.bookstore.payloads.user.WishlistDTO;
import com.vb.bookstore.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> getUserInfo(HttpServletRequest request) {
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

    @GetMapping("/wishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<WishlistDTO>> getWishlist() {
        List<WishlistDTO> wishlistDTOS = userService.getWishlist();

        return ResponseEntity.ok(wishlistDTOS);
    }

    @PostMapping("/wishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> addBookToWishlist(
            @Valid
            @RequestBody
            WishlistDTO request
    ) {
        MessageResponse messageResponse = userService.addBookToWishlist(request);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/wishlist/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteBookFromWishlist(@PathVariable Long id) {
        MessageResponse messageResponse = userService.deleteBookFromWishlist(id);
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/address")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addressDTOS = userService.getAllAddresses();

        return ResponseEntity.ok(addressDTOS);
    }

    @PostMapping("/address")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> addAddress(
            @Valid
            @RequestBody
            AddressDTO request
    ) {
        MessageResponse messageResponse = userService.addAddress(request);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/address/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteAddress(@PathVariable Long id) {
        MessageResponse messageResponse = userService.deleteAddress(id);
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping("/address/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> updateAddress(
            @Valid @RequestBody AddressDTO newAddress,
            @PathVariable Long id
    ) {
        MessageResponse messageResponse = userService.updateAddress(newAddress, id);
        return ResponseEntity.ok(messageResponse);
    }
}
