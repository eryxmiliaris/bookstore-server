package com.vb.bookstore.services;

import com.vb.bookstore.entities.Address;
import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.entities.Wishlist;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.auth.UserDTO;
import com.vb.bookstore.payloads.user.AddressDTO;
import com.vb.bookstore.payloads.user.UpdateUserInfoRequest;
import com.vb.bookstore.payloads.user.UpdateUserInfoResponse;
import com.vb.bookstore.payloads.user.WishlistDTO;
import com.vb.bookstore.repositories.AddressRepository;
import com.vb.bookstore.repositories.BookRepository;
import com.vb.bookstore.repositories.UserRepository;
import com.vb.bookstore.repositories.WishlistRepository;
import com.vb.bookstore.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final WishlistRepository wishlistRepository;
    private final AddressRepository addressRepository;
    private final JwtUtil jwtUtil;

    private User getUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();
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
        User user = getUser();

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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        user.setBirthDate(formatter.parse(request.getBirthDate()));

        userRepository.save(user);

        ResponseCookie jwtCookie = jwtUtil.generateJwtCookieFromUsername(request.getUsername());

        return new UpdateUserInfoResponse(jwtCookie, new MessageResponse(true, "User info has been updated"));
    }

    public List<WishlistDTO> getWishlist() {
        User user = getUser();

        List<Wishlist> wishlists = wishlistRepository.findByUserId(user.getId());
        List<WishlistDTO> wishlistDTOS = wishlists.stream().map((element) -> {
            WishlistDTO mapped = modelMapper.map(element, WishlistDTO.class);
            mapped.setBookId(element.getBook().getId());
            return mapped;
        }).toList();

        return wishlistDTOS;
    }

    public MessageResponse addBookToWishlist(WishlistDTO request) {
        User user = getUser();

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", request.getBookId()));
        Optional<Wishlist> wishlistItem = wishlistRepository.findByUserIdAndBookIdAndBookTypeAndPaperBookId(user.getId(), book.getId(), request.getBookType(), request.getPaperBookId());
        if (wishlistItem.isPresent()) {
            throw new ApiRequestException("User already has this book in his wishlist!", HttpStatus.CONFLICT);
        }
        Wishlist newWishlistItem = new Wishlist();
        newWishlistItem.setBookType(request.getBookType());
        newWishlistItem.setPaperBookId(request.getPaperBookId());
        newWishlistItem.setUser(user);
        newWishlistItem.setBook(book);
        wishlistRepository.save(newWishlistItem);

        return new MessageResponse(true, "Book has been added to your wishlist successfully!");
    }

    public MessageResponse deleteBookFromWishlist(Long id) {
        Wishlist wishlistItem = wishlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item", "id", id));
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();
        if (user.getUsername().equals(wishlistItem.getUser().getUsername())) {
            wishlistRepository.delete(wishlistItem);
        } else {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }
        return new MessageResponse(true, "Book was removed from your wishlist!");
    }

    public List<AddressDTO> getAllAddresses() {
        User user = getUser();

        List<Address> userAddresses = addressRepository.findByUser(user);
        List<AddressDTO> userAddressesDtos = userAddresses.stream().map((element) -> modelMapper.map(element, AddressDTO.class)).toList();

        return userAddressesDtos;
    }

    public MessageResponse addAddress(AddressDTO request) {
        User user = getUser();

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
        User user = getUser();

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
        User user = getUser();

        if (user.getUsername().equals(oldAddress.get().getUser().getUsername())) {
            Address newAddressEntity = modelMapper.map(newAddress, Address.class);
            newAddressEntity.setId(id);
            newAddressEntity.setUser(user);
            addressRepository.save(newAddressEntity);
        }
        return new MessageResponse(true, "Address has been successfully updated");
    }
}
