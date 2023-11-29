package com.vb.bookstore.controllers;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.user.AddressDTO;
import com.vb.bookstore.services.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/addresses")
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addressDTOS = addressService.getAllAddresses();

        return ResponseEntity.ok(addressDTOS);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> addAddress(
            @Valid
            @RequestBody
            AddressDTO request
    ) {
        MessageResponse messageResponse = addressService.addAddress(request);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteAddress(@PathVariable Long id) {
        MessageResponse messageResponse = addressService.deleteAddress(id);
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> updateAddress(
            @Valid @RequestBody AddressDTO newAddress,
            @PathVariable Long id
    ) {
        MessageResponse messageResponse = addressService.updateAddress(newAddress, id);
        return ResponseEntity.ok(messageResponse);
    }
}
