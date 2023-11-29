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

@Service
@RequiredArgsConstructor
public class AddressService {

    private final ModelMapper modelMapper;
    private final UserService userService;

    private final AddressRepository addressRepository;

    public List<AddressDTO> getAllAddresses() {
        User user = userService.getCurrentUser();

        List<Address> userAddresses = user.getAddresses();
        List<AddressDTO> userAddressesDtos = userAddresses.stream().map((element) -> modelMapper.map(element, AddressDTO.class)).toList();

        return userAddressesDtos;
    }

    public MessageResponse addAddress(AddressDTO request) {
        User user = userService.getCurrentUser();

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
        User user = userService.getCurrentUser();
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

        return new MessageResponse(true, "Address has been deleted");
    }

    public MessageResponse updateAddress(AddressDTO newAddress, Long id) {
        Optional<Address> oldAddress = addressRepository.findById(id);
        if (oldAddress.isEmpty()) {
            throw new ResourceNotFoundException("Address", "id", id);
        }

        User user = userService.getCurrentUser();

        if (user.getUsername().equals(oldAddress.get().getUser().getUsername())) {
            Address newAddressEntity = modelMapper.map(newAddress, Address.class);
            newAddressEntity.setId(id);
            newAddressEntity.setUser(user);
            addressRepository.save(newAddressEntity);
        } else {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }

        return new MessageResponse(true, "Address has been successfully updated");
    }
}
