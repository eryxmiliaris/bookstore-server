package com.vb.bookstore.services.impl;

import com.vb.bookstore.entities.*;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.library.BookNoteDTO;
import com.vb.bookstore.payloads.library.LibraryItemDTO;
import com.vb.bookstore.repositories.BookNoteRepository;
import com.vb.bookstore.repositories.BookRepository;
import com.vb.bookstore.repositories.LibraryCollectionRepository;
import com.vb.bookstore.repositories.LibraryItemRepository;
import com.vb.bookstore.services.LibraryService;
import com.vb.bookstore.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {
    private final UserService userService;
    private final ModelMapper modelMapper;

    private final BookRepository bookRepository;
    private final LibraryItemRepository libraryItemRepository;
    private final LibraryCollectionRepository libraryCollectionRepository;
    private final BookNoteRepository bookNoteRepository;

    private List<LibraryItemDTO> libraryItemListToDtos(List<LibraryItem> list) {
        return list.stream().map((libraryItem) -> {
            LibraryItemDTO dto = modelMapper.map(libraryItem, LibraryItemDTO.class);
            dto.setBookId(libraryItem.getBook().getId());
            if (libraryItem.getLibraryCollection() != null) {
                dto.setCollectionId(libraryItem.getLibraryCollection().getId());
            } else {
                dto.setCollectionId(null);
            }
            dto.setTitle(libraryItem.getBook().getTitle());
            dto.setAuthor(libraryItem.getBook().getAuthor());
            dto.setDescription(libraryItem.getBook().getDescription());
            if (libraryItem.getBookType().equals("Ebook")) {
                dto.setNumOfPages(libraryItem.getBook().getEbook().getNumOfPages());
            } else {
                dto.setNarrator(libraryItem.getBook().getAudiobook().getNarrator());
                dto.setDurationSeconds(libraryItem.getBook().getAudiobook().getDurationSeconds());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    public List<LibraryItemDTO> getLibrary(Boolean subscriptionItems) {
        User user = userService.getCurrentUser();
        List<LibraryItem> library;
        if (subscriptionItems == null) {
            library = libraryItemRepository.findByUser(user);
        } else {
            library = libraryItemRepository.findByUserAndIsSubscriptionItem(user, subscriptionItems);
        }
        List<LibraryItemDTO> libraryDTO = libraryItemListToDtos(library);
        return libraryDTO;
    }

    public LibraryItemDTO getLibraryItem(Long id) {
        User user = userService.getCurrentUser();
        LibraryItem libraryItem = libraryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Library item", "id", id));
        if (libraryItem.getUser() != user) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }

        LibraryItemDTO libraryItemDTO = modelMapper.map(libraryItem, LibraryItemDTO.class);
        libraryItemDTO.setBookId(libraryItem.getBook().getId());
        if (libraryItem.getLibraryCollection() != null) {
            libraryItemDTO.setCollectionId(libraryItem.getLibraryCollection().getId());
        } else {
            libraryItemDTO.setCollectionId(null);
        }
        libraryItemDTO.setTitle(libraryItem.getBook().getTitle());
        libraryItemDTO.setAuthor(libraryItem.getBook().getAuthor());
        libraryItemDTO.setDescription(libraryItem.getBook().getDescription());
        if (libraryItem.getBookType().equals("Ebook")) {
            libraryItemDTO.setNumOfPages(libraryItem.getBook().getEbook().getNumOfPages());
        } else {
            libraryItemDTO.setNarrator(libraryItem.getBook().getAudiobook().getNarrator());
            libraryItemDTO.setDurationSeconds(libraryItem.getBook().getAudiobook().getDurationSeconds());
        }

        return libraryItemDTO;
    }

    public List<LibraryItemDTO> getLibraryByCollection(Long collectionId) {
        User user = userService.getCurrentUser();

        LibraryCollection collection = libraryCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", "id", collectionId));

        List<LibraryItem> library = libraryItemRepository.findByUserAndLibraryCollection(user, collection);

        List<LibraryItemDTO> libraryDTO = libraryItemListToDtos(library);
        return libraryDTO;
    }

    public MessageResponse addSubscriptionItem(Long bookId, String bookType) {
        User user = userService.getCurrentUser();
        if (!user.getHasActiveSubscription()) {
            throw new ApiRequestException("User has no active subscription", HttpStatus.FORBIDDEN);
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        switch (bookType) {
            case "Ebook" -> {
                if (book.getEbook() == null) {
                    throw new ApiRequestException("Book has no assigned ebook", HttpStatus.BAD_REQUEST);
                }
            }
            case "Audiobook" -> {
                if (book.getAudiobook() == null) {
                    throw new ApiRequestException("Book has no assigned audiobook", HttpStatus.BAD_REQUEST);
                }
            }
            default -> {
                throw new ApiRequestException("Wrong book type", HttpStatus.BAD_REQUEST);
            }
        }

        Optional<LibraryItem> existingLibraryItem = libraryItemRepository.findByUserAndBookAndBookType(user, book, bookType);
        if (existingLibraryItem.isPresent()) {
            throw new ApiRequestException("This book is already in your library", HttpStatus.BAD_REQUEST);
        }

        LibraryItem libraryItem = new LibraryItem();
        libraryItem.setIsSubscriptionItem(true);
        libraryItem.setUser(user);
        libraryItem.setBook(book);
        libraryItem.setBookType(bookType);
        libraryItem.setAddedDate(LocalDate.now());
        libraryItem.setLastPosition("0");

        libraryItemRepository.save(libraryItem);

        return new MessageResponse(true, "Subscription item has been successfully added to your library");
    }

    public MessageResponse deleteSubscriptionItem(Long id) {
        LibraryItem libraryItem = libraryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Library item", "id", id));

        User user = userService.getCurrentUser();
        if (libraryItem.getUser() != user) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }

        if (!libraryItem.getIsSubscriptionItem()) {
            throw new ApiRequestException("Library item is not a subscription item", HttpStatus.BAD_REQUEST);
        }

        LibraryCollection collection = libraryItem.getLibraryCollection();
        if (collection != null) {
            collection.removeLibraryItem(libraryItem);
            libraryItem.setLibraryCollection(null);
            libraryCollectionRepository.save(collection);
        }

        libraryItemRepository.delete(libraryItem);

        return new MessageResponse(true, "Subscription item has been deleted successfully");
    }

    public Resource downloadBookFile(Long id) {
        User user = userService.getCurrentUser();
        LibraryItem libraryItem = libraryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Library item", "id", id));

        if (libraryItem.getUser() != user) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }

        if (libraryItem.getIsSubscriptionItem() && !user.getHasActiveSubscription()) {
            throw new ApiRequestException("User has no active subscription to get access to subscription items", HttpStatus.FORBIDDEN);
        }

        Path filePath = null;
        switch (libraryItem.getBookType()) {
            case "Ebook" -> filePath = Paths.get(libraryItem.getBook().getEbook().getBookPath());
            case "Audiobook" -> filePath = Paths.get(libraryItem.getBook().getAudiobook().getBookPath());
        }
        Resource bookFile = new FileSystemResource(filePath);
        if (!bookFile.exists()) {
            throw new ApiRequestException("File not found", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return bookFile;
    }

    public MessageResponse updateReadingStatus(Long id, String newPosition) {
        User user = userService.getCurrentUser();
        LibraryItem libraryItem = libraryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Library item", "id", id));
        if (libraryItem.getUser() != user) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }

        libraryItem.setLastPosition(newPosition);

        libraryItemRepository.save(libraryItem);

        return new MessageResponse(true, "Reading status has been updated successfully");
    }

    public List<BookNoteDTO> getBookNotes(Long libraryItemId) {
        User user = userService.getCurrentUser();
        LibraryItem libraryItem = libraryItemRepository.findById(libraryItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Library item", "id", libraryItemId));
        if (libraryItem.getUser() != user) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }

        List<BookNote> bookNotes = bookNoteRepository.findByLibraryItem(libraryItem);
        List<BookNoteDTO> bookNoteDTOS = bookNotes.stream().map((element) -> modelMapper.map(element, BookNoteDTO.class)).collect(Collectors.toList());

        return bookNoteDTOS;
    }

    public MessageResponse addBookNote(Long id, BookNoteDTO bookNoteDTO) {
        User user = userService.getCurrentUser();
        LibraryItem libraryItem = libraryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Library item", "id", id));
        if (libraryItem.getUser() != user) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }

        BookNote bookNote = modelMapper.map(bookNoteDTO, BookNote.class);
        bookNote.setLibraryItem(libraryItem);

        bookNoteRepository.save(bookNote);

        return new MessageResponse(true, "Book note has been saved successfully");
    }

    public MessageResponse deleteBookNote(Long id) {
        User user = userService.getCurrentUser();
        BookNote bookNote = bookNoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book note", "id", id));
        if (bookNote.getLibraryItem().getUser() != user) {
            throw new ApiRequestException("Missing access", HttpStatus.FORBIDDEN);
        }

        bookNoteRepository.delete(bookNote);

        return new MessageResponse(true, "Book note has been deleted successfully");
    }
}
