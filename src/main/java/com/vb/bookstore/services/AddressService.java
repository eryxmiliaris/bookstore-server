package com.vb.bookstore.services;

import com.vb.bookstore.entities.Address;
import com.vb.bookstore.entities.Cart;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.user.AddressDTO;
import com.vb.bookstore.repositories.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    List<AddressDTO> getAllAddresses();

    MessageResponse addAddress(AddressDTO request);

    MessageResponse deleteAddress(Long id) ;

    MessageResponse updateAddress(AddressDTO newAddress, Long id);
}
