package com.vb.bookstore.services;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.library.BookNoteDTO;
import com.vb.bookstore.payloads.library.LibraryItemDTO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface LibraryService {
    List<LibraryItemDTO> getLibrary(Boolean subscriptionItems);

    LibraryItemDTO getLibraryItem(Long id);

    List<LibraryItemDTO> getLibraryByCollection(Long collectionId);

    MessageResponse addSubscriptionItem(Long bookId, String bookType);

    MessageResponse deleteSubscriptionItem(Long id);

    Resource downloadBookFile(Long id);

    MessageResponse updateReadingStatus(Long id, String newPosition);

    List<BookNoteDTO> getBookNotes(Long libraryItemId);

    MessageResponse addBookNote(Long id, BookNoteDTO bookNoteDTO);

    MessageResponse deleteBookNote(Long id);
}
