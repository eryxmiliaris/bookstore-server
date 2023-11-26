package com.vb.bookstore.controllers;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.library.BookNoteDTO;
import com.vb.bookstore.payloads.library.LibraryCollectionDTO;
import com.vb.bookstore.payloads.library.LibraryItemDTO;
import com.vb.bookstore.services.LibraryCollectionService;
import com.vb.bookstore.services.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<LibraryItemDTO> getLibraryItem(
            @PathVariable Long id
    ) {
        LibraryItemDTO libraryItem = libraryService.getLibraryItem(id);
        return ResponseEntity.ok(libraryItem);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> updateReadingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> newPosition
    ) {
        MessageResponse messageResponse = libraryService.updateReadingStatus(id, newPosition.get("newPosition"));
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/bookNotes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookNoteDTO>> getBookNotes(
            @RequestParam Long libraryItemId
    ) {
        List<BookNoteDTO> bookNoteDTOS = libraryService.getBookNotes(libraryItemId);
        return ResponseEntity.ok(bookNoteDTOS);
    }

    @PostMapping("/bookNotes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> addBookNote(
            @RequestParam Long libraryItemId,
            @RequestBody BookNoteDTO bookNote
    ) {
        MessageResponse messageResponse = libraryService.addBookNote(libraryItemId, bookNote);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/bookNotes/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteBookNote(
            @PathVariable Long id
    ) {
        MessageResponse messageResponse = libraryService.deleteBookNote(id);
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping(value = {"/{id}/book.epub", "/{id}/book.mp3"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Resource> downloadBookFile(
            @PathVariable Long id
    ) {
        Resource book = libraryService.downloadBookFile(id);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + book.getFilename());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(book);
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
