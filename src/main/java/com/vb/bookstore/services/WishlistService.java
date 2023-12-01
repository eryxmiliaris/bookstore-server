package com.vb.bookstore.services;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.wishlist.WishlistDTO;

import java.util.List;

public interface WishlistService {
    List<WishlistDTO> getWishlist();

    MessageResponse addBookToWishlist(WishlistDTO request);

    MessageResponse deleteBookFromWishlist(Long id);
}
