package com.vb.bookstore.services;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.LibraryCollection;
import com.vb.bookstore.entities.LibraryItem;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.library.LibraryCollectionDTO;
import com.vb.bookstore.payloads.library.LibraryItemDTO;
import com.vb.bookstore.repositories.BookRepository;
import com.vb.bookstore.repositories.LibraryCollectionRepository;
import com.vb.bookstore.repositories.LibraryItemRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibraryService {
    private final UserService userService;
    private final ModelMapper modelMapper;

    private final BookRepository bookRepository;
    private final LibraryItemRepository libraryItemRepository;
    private final LibraryCollectionRepository libraryCollectionRepository;

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
                dto.setNarrator(libraryItem.getBook().getAudioBook().getNarrator());
                dto.setDurationSeconds(libraryItem.getBook().getAudioBook().getDurationSeconds());
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

    public List<LibraryItemDTO> getLibraryByCollection(Long collectionId) {
        User user = userService.getCurrentUser();

        LibraryCollection collection = libraryCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", "id", collectionId));

        List<LibraryItem> library = libraryItemRepository.findByUserAndLibraryCollection(user, collection);

        List<LibraryItemDTO> libraryDTO = libraryItemListToDtos(library);
        return libraryDTO;
    }

    public List<LibraryCollectionDTO> getCollections() {
        User user = userService.getCurrentUser();
        List<LibraryCollection> libraryCollections = libraryCollectionRepository.findByUser(user);
        List<LibraryCollectionDTO> libraryCollectionDTOS = libraryCollections.stream().map((element) -> modelMapper.map(element, LibraryCollectionDTO.class)).toList();
        return libraryCollectionDTOS;
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
                if (book.getAudioBook() == null) {
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
}
