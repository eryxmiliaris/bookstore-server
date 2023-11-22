package com.vb.bookstore.services;

import com.vb.bookstore.entities.*;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.user.AddressDTO;
import com.vb.bookstore.payloads.user.UpdateUserInfoRequest;
import com.vb.bookstore.payloads.user.UpdateUserInfoResponse;
import com.vb.bookstore.payloads.user.UserDTO;
import com.vb.bookstore.repositories.AddressRepository;
import com.vb.bookstore.repositories.RoleRepository;
import com.vb.bookstore.repositories.UserRepository;
import com.vb.bookstore.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
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

    public UpdateUserInfoResponse updateUserInfo(UpdateUserInfoRequest request) throws ParseException {
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



    public List<AddressDTO> getAllAddresses() {
        User user = getCurrentUser();

        List<Address> userAddresses = addressRepository.findByUser(user);
        List<AddressDTO> userAddressesDtos = userAddresses.stream().map((element) -> modelMapper.map(element, AddressDTO.class)).toList();

        return userAddressesDtos;
    }

    public MessageResponse addAddress(AddressDTO request) {
        User user = getCurrentUser();

        List<Address> userAddresses = addressRepository.findByUser(user);
        if (userAddresses.size() >= 3) {
            throw new ApiRequestException("You can't create more than 3 addresses", HttpStatus.BAD_REQUEST);
        }
        Address address = modelMapper.map(request, Address.class);
        address.setUser(user);

        addressRepository.save(address);

        return new MessageResponse(true, "New address has been successfully added");
    }


    public MessageResponse deleteAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
        User user = getCurrentUser();
        Cart cart = user.getCart();

        if (cart.getAddress() == address && cart.getPaymentStatus() != null) {
            throw new ApiRequestException("This address is used in an ongoing order; complete the payment or cancel the order to delete the address", HttpStatus.FORBIDDEN);
        } else {
            cart.setAddress(null);
        }

        if (user.getUsername().equals(address.getUser().getUsername())) {
            addressRepository.delete(address);
        } else {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }

        return new MessageResponse(true, "Address was deleted!");
    }

    public MessageResponse updateAddress(AddressDTO newAddress, Long id) {
        Optional<Address> oldAddress = addressRepository.findById(id);
        if (oldAddress.isEmpty()) {
            throw new ResourceNotFoundException("Address", "id", id);
        }

        User user = getCurrentUser();

        if (user.getUsername().equals(oldAddress.get().getUser().getUsername())) {
            Address newAddressEntity = modelMapper.map(newAddress, Address.class);
            newAddressEntity.setId(id);
            newAddressEntity.setUser(user);
            addressRepository.save(newAddressEntity);
        }

        return new MessageResponse(true, "Address has been successfully updated");
    }
}
