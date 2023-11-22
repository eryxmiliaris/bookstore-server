package com.vb.bookstore.controllers;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.library.LibraryCollectionDTO;
import com.vb.bookstore.payloads.library.LibraryItemDTO;
import com.vb.bookstore.services.LibraryCollectionService;
import com.vb.bookstore.services.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/library")
public class LibraryController {
    private final LibraryService libraryService;
    private final LibraryCollectionService collectionService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<LibraryItemDTO>> getLibrary(
            @RequestParam(required = false) Long collectionId,
            @RequestParam(required = false) Boolean subscriptionItems
    ) {
        List<LibraryItemDTO> library;
        if (collectionId == null) {
            library = libraryService.getLibrary(subscriptionItems);
        } else {
            library = libraryService.getLibraryByCollection(collectionId);
        }
        return ResponseEntity.ok(library);
    }

    @PostMapping("/subscriptionItems")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> addSubscriptionItem(
            @RequestParam Long bookId,
            @RequestParam String bookType
    ) {
        MessageResponse messageResponse = libraryService.addSubscriptionItem(bookId, bookType);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/subscriptionItems/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteSubscriptionItem(
            @PathVariable Long id
    ) {
        MessageResponse messageResponse = libraryService.deleteSubscriptionItem(id);
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/collections")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<LibraryCollectionDTO>> getCollections() {
        List<LibraryCollectionDTO> collectionDTOS = libraryService.getCollections();
        return ResponseEntity.ok(collectionDTOS);
    }

    @PostMapping("/collections")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> addCollection(@RequestBody @Valid LibraryCollectionDTO libraryCollectionDTO) {
        MessageResponse messageResponse = collectionService.addCollection(libraryCollectionDTO);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/collections/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteCollection(@PathVariable Long id) {
        MessageResponse messageResponse = collectionService.deleteCollection(id);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/collections/{collectionId}/libraryItem/{libraryItemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> addLibraryItemToCollection(@PathVariable Long collectionId, @PathVariable Long libraryItemId) {
        MessageResponse messageResponse = collectionService.addLibraryItemToCollection(collectionId, libraryItemId);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/collections/{collectionId}/libraryItem/{libraryItemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteLibraryItemFromCollection(@PathVariable Long collectionId, @PathVariable Long libraryItemId) {
        MessageResponse messageResponse = collectionService.deleteLibraryItemFromCollection(collectionId, libraryItemId);
        return ResponseEntity.ok(messageResponse);
    }
}
