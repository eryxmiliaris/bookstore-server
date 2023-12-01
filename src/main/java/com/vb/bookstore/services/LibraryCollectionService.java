package com.vb.bookstore.services;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.library.LibraryCollectionDTO;

import java.util.List;

public interface LibraryCollectionService {
    List<LibraryCollectionDTO> getCollections();

    MessageResponse addCollection(LibraryCollectionDTO libraryCollectionDTO);

    MessageResponse deleteCollection(Long id);

    MessageResponse addLibraryItemToCollection(Long collectionId, Long libraryItemId);

    MessageResponse deleteLibraryItemFromCollection(Long collectionId, Long libraryItemId);
}
