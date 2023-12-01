package com.vb.bookstore.services.impl;

import com.vb.bookstore.entities.LibraryCollection;
import com.vb.bookstore.entities.LibraryItem;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.library.LibraryCollectionDTO;
import com.vb.bookstore.repositories.LibraryCollectionRepository;
import com.vb.bookstore.repositories.LibraryItemRepository;
import com.vb.bookstore.services.LibraryCollectionService;
import com.vb.bookstore.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryCollectionServiceImpl implements LibraryCollectionService {

    private final UserService userService;
    private final ModelMapper modelMapper;

    private final LibraryCollectionRepository libraryCollectionRepository;
    private final LibraryItemRepository libraryItemRepository;

    public List<LibraryCollectionDTO> getCollections() {
        User user = userService.getCurrentUser();
        List<LibraryCollection> libraryCollections = libraryCollectionRepository.findByUser(user);
        List<LibraryCollectionDTO> libraryCollectionDTOS = libraryCollections.stream().map((element) -> modelMapper.map(element, LibraryCollectionDTO.class)).toList();
        return libraryCollectionDTOS;
    }

    public MessageResponse addCollection(LibraryCollectionDTO libraryCollectionDTO) {
        User user = userService.getCurrentUser();
        LibraryCollection libraryCollection = new LibraryCollection();
        libraryCollection.setName(libraryCollectionDTO.getName());
        libraryCollection.setUser(user);

        libraryCollectionRepository.save(libraryCollection);

        return new MessageResponse(true, "Collection has been successfully created");
    }

    public MessageResponse deleteCollection(Long id) {
        User user = userService.getCurrentUser();
        LibraryCollection libraryCollection = libraryCollectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", "id", id));
        if (libraryCollection.getUser() != user) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }

        for (LibraryItem libraryItem : libraryCollection.getLibraryItems()) {
            libraryItem.setLibraryCollection(null);
        }

        libraryCollection.getLibraryItems().clear();
        libraryCollectionRepository.delete(libraryCollection);

        return new MessageResponse(true, "Collection has been successfully deleted");
    }

    public MessageResponse addLibraryItemToCollection(Long collectionId, Long libraryItemId) {
        User user = userService.getCurrentUser();
        LibraryCollection collection = libraryCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", "id", collectionId));
        LibraryItem libraryItem = libraryItemRepository.findById(libraryItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Library item", "id", libraryItemId));
        if (collection.getUser() != user || libraryItem.getUser() != user) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }

        libraryItem.setLibraryCollection(collection);
        collection.addLibraryItem(libraryItem);

        libraryCollectionRepository.save(collection);

        return new MessageResponse(true, "Library item has been added to collection successfully");
    }

    public MessageResponse deleteLibraryItemFromCollection(Long collectionId, Long libraryItemId) {
        User user = userService.getCurrentUser();
        LibraryCollection collection = libraryCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", "id", collectionId));
        LibraryItem libraryItem = libraryItemRepository.findById(libraryItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Library item", "id", libraryItemId));
        if (collection.getUser() != user || libraryItem.getUser() != user) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }
        if (libraryItem.getLibraryCollection() == null) {
            throw new ApiRequestException("Library item doesn't have an assigned collection", HttpStatus.CONFLICT);
        }

        libraryItem.setLibraryCollection(null);
        collection.removeLibraryItem(libraryItem);

        libraryCollectionRepository.save(collection);
        libraryItemRepository.save(libraryItem);

        return new MessageResponse(true, "Library item has been deleted from collection successfully");
    }
}
