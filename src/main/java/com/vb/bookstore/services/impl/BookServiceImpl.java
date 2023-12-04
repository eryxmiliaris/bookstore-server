package com.vb.bookstore.services.impl;

import com.vb.bookstore.config.AppConstants;
import com.vb.bookstore.entities.*;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.PageableResponse;
import com.vb.bookstore.payloads.books.*;
import com.vb.bookstore.repositories.*;
import com.vb.bookstore.services.BookService;
import com.vb.bookstore.services.RecommendationService;
import com.vb.bookstore.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final RecommendationService recommendationService;

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final PaperBookRepository paperBookRepository;
    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;

    public BookDTO getBookById(Long id) {
        boolean isAdmin = userService.currentUserIsAdmin();
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "bookId", id));

        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
        List<PaperBookDTO> paperBookDTOs = new ArrayList<>();
        for (PaperBook paperBook : book.getPaperBooks()) {
            if (!paperBook.getIsHidden() || isAdmin) {
                paperBookDTOs.add(modelMapper.map(paperBook, PaperBookDTO.class));
            }
        }
        bookDTO.setPaperBooks(paperBookDTOs);
        if (!isAdmin && book.getEbook() != null && book.getEbook().getIsHidden()) {
            bookDTO.setEbook(null);
        }
        if (!isAdmin && book.getAudiobook() != null && book.getAudiobook().getIsHidden()) {
            bookDTO.setAudiobook(null);
        }

        List<ReviewDTO> reviewDTOS = new ArrayList<>();
        for (Review review : book.getReviews()) {
            ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
            reviewDTO.setUserName(review.getUser().getUsername());
            reviewDTOS.add(reviewDTO);
        }
        bookDTO.setReviews(reviewDTOS);
        bookDTO.setCategories(book.getCategories().stream().map(Category::getCategoryName).toList());

        return bookDTO;
    }

    public PageableResponse getBooksWithFilters(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder,
            String priceStart,
            String priceEnd,
            String[] categories,
            String[] bookTypes,
            String bookTitle
    ) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Boolean hasCategories = categories != null;
        List<String> categoriesList = null;
        if (categories != null) {
            categoriesList = Arrays.asList(categories);
        }

        double priceStartDouble = priceStart == null ? 0.0 : Double.parseDouble(priceStart);
        double priceEndDouble = priceEnd == null ? 100000.0 : Double.parseDouble(priceEnd);
        Double priceStartPB = priceStartDouble;
        Double priceEndPB = priceEndDouble;
        Double priceStartEB = priceStartDouble;
        Double priceEndEB = priceEndDouble;
        Double priceStartAB = priceStartDouble;
        Double priceEndAB = priceEndDouble;
        if (bookTypes != null) {
            List<String> bookTypesList = Arrays.asList(bookTypes);
            if (bookTypesList.contains(AppConstants.PAPER_BOOK)) {
                priceStartPB = priceStartDouble;
                priceEndPB = priceEndDouble;
            } else {
                priceStartPB = null;
                priceEndPB = null;
            }

            if (bookTypesList.contains(AppConstants.EBOOK)) {
                priceStartEB = priceStartDouble;
                priceEndEB = priceEndDouble;
            } else {
                priceStartEB = null;
                priceEndEB = null;
            }

            if (bookTypesList.contains(AppConstants.AUDIOBOOK)) {
                priceStartAB = priceStartDouble;
                priceEndAB = priceEndDouble;
            } else {
                priceStartAB = null;
                priceEndAB = null;
            }
        }

        boolean includeHidden = userService.currentUserIsAdmin();

        Page<Book> books = bookRepository.findByFilterParams(
                bookTitle
                , hasCategories
                , categoriesList
                , priceStartPB, priceEndPB
                , priceStartEB, priceEndEB
                , priceStartAB, priceEndAB
                , includeHidden
                , pageDetails
        );

        List<BookMainInfoDTO> pageContentDTO = bookStreamToBookDtoList(books.stream());

        PageableResponse bookResponse = new PageableResponse();
        bookResponse.setContent(pageContentDTO);
        bookResponse.setPageNumber(pageNumber);
        bookResponse.setPageSize(pageSize);
        bookResponse.setTotalPages(books.getTotalPages());
        bookResponse.setTotalElements(books.getTotalElements());

        return bookResponse;
    }

    public List<BookMainInfoDTO> bookStreamToBookDtoList(Stream<Book> books) {
        boolean isAdmin = userService.currentUserIsAdmin();
        return books.map(book -> {
                    BookMainInfoDTO bookResponse = modelMapper.map(book, BookMainInfoDTO.class);
                    List<String> types = new ArrayList<>();

                    if (!book.getPaperBooks().isEmpty()) {
                        for (PaperBook pb : book.getPaperBooks()) {
                            if (isAdmin || !pb.getIsHidden()) {
                                types.add(AppConstants.PAPER_BOOK);
                                break;
                            }
                        }
                    }
                    if (book.getEbook() != null && (isAdmin || !book.getEbook().getIsHidden())) {
                        types.add(AppConstants.EBOOK);
                    }
                    if (book.getAudiobook() != null && (isAdmin || !book.getAudiobook().getIsHidden())) {
                        types.add(AppConstants.AUDIOBOOK);
                    }

                    bookResponse.setBookTypes(types);
                    return bookResponse;
                })
                .toList();
    }

    public byte[] getBookCoverImage(Long id, String bookType, Long paperBookId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        String imagePath = null;
        switch (bookType) {
            case AppConstants.PAPER_BOOK -> {
                PaperBook paperBook;
                if (paperBookId != null) {
                    paperBook = paperBookRepository.findById(paperBookId)
                            .orElseThrow(() -> new ResourceNotFoundException("Paper book", "id", id));
                    if (paperBook.getBook() != book) {
                        throw new ApiRequestException("Invalid paper book id", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    paperBook = book.getPaperBooks().get(0);
                    if (paperBook == null) {
                        throw new ApiRequestException("Book with id " + id + " does not have paper version", HttpStatus.BAD_REQUEST);
                    }
                }
                imagePath = paperBook.getCoverImagePath();
            }
            case AppConstants.EBOOK -> {
                Ebook ebook = book.getEbook();
                if (ebook == null) {
                    throw new ApiRequestException("Given book doesn't have an ebook", HttpStatus.BAD_REQUEST);
                }
                imagePath = ebook.getCoverImagePath();
            }
            case AppConstants.AUDIOBOOK -> {
                Audiobook audiobook = book.getAudiobook();
                if (audiobook == null) {
                    throw new ApiRequestException("Given book doesn't have an audiobook", HttpStatus.BAD_REQUEST);
                }
                imagePath = audiobook.getCoverImagePath();
            }
        }
        try {
            Path path = Paths.get(imagePath);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new ApiRequestException("Error getting image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<BookMainInfoDTO> getPopularBooks() {
        List<Book> allBooks = bookRepository.findByPaperBooks_IsHiddenFalseOrAudiobook_IsHiddenFalseOrEbook_IsHiddenFalse();
        LocalDate oneWeekAgo = LocalDate.now().minusDays(7);

        Map<Book, Double> popularityMap = allBooks.stream()
                .collect(Collectors.toMap(
                        book -> book,
                        book -> {
                            int numberOfOrders = orderRepository.findByOrderItems_BookAndOrderDateAfter(book, oneWeekAgo).size();
                            int numberOfWishlists = wishlistRepository.findByBook(book).size();

                            double orderWeight = 0.7;
                            double wishlistWeight = 0.3;

                            return (orderWeight * numberOfOrders) + (wishlistWeight * numberOfWishlists);
                        }
                ));

        List<Book> popularBooks = popularityMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(16) // Adjust the limit based on your requirement
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<BookMainInfoDTO> result = bookStreamToBookDtoList(popularBooks.stream());

        return result;
    }

    public List<BookMainInfoDTO> getRecommendedBooks() {
        try {
            User user = userService.getCurrentUser();
            List<BookMainInfoDTO> recommendedBooks = bookStreamToBookDtoList(user.getRecommendedBooks().stream());
            if (recommendedBooks.size() == 0) {
                return getPopularBooks();
            }
            return recommendedBooks;
        } catch (Exception e) {
        }
        return getPopularBooks();
    }

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOS = categories.stream().map((element) -> modelMapper.map(element, CategoryDTO.class)).collect(Collectors.toList());
        return categoryDTOS;
    }

    public Resource downloadBookPreview(Long id, String bookType) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        Path filePath = null;

        switch (bookType) {
            case AppConstants.EBOOK -> {
                Ebook ebook = book.getEbook();
                if (ebook == null) {
                    throw new ApiRequestException("Given book has no ebook", HttpStatus.BAD_REQUEST);
                }
                filePath = Paths.get(book.getEbook().getPreviewPath());
            }
            case AppConstants.AUDIOBOOK -> {
                Audiobook audiobook = book.getAudiobook();
                if (audiobook == null) {
                    throw new ApiRequestException("Given book has no audiobook", HttpStatus.BAD_REQUEST);
                }
                filePath = Paths.get(book.getAudiobook().getPreviewPath());
            }
        }
        Resource bookFile = new FileSystemResource(filePath);
        if (!bookFile.exists()) {
            throw new ApiRequestException("File not found", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return bookFile;
    }
}
