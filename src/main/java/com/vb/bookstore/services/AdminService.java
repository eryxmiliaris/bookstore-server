package com.vb.bookstore.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vb.bookstore.entities.*;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.PageableResponse;
import com.vb.bookstore.payloads.admin.*;
import com.vb.bookstore.payloads.books.ReviewDTO;
import com.vb.bookstore.payloads.order.OrderDTO;
import com.vb.bookstore.repositories.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ModelMapper modelMapper;

    private final CategoryRepository categoryRepository;
    private final EBookRepository eBookRepository;
    private final BookRepository bookRepository;
    private final PaperBookRepository paperBookRepository;
    private final AudioBookRepository audioBookRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public Long addNewBook(NewBookDTO newBookDTO, MultipartFile coverImage, MultipartFile bookFile) {
        Book book = new Book();
        book.setTitle(newBookDTO.getTitle());
        book.setAuthor(newBookDTO.getAuthor());
        book.setDescription(newBookDTO.getDescription());
        book.setPublicationDate(newBookDTO.getPublicationDate());
        book.setRating(BigDecimal.ZERO);
        book.setNumOfReviews(0);

        Set<Category> categories = new HashSet<>();
        for (Long categoryId : newBookDTO.getCategories()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
            categories.add(category);
        }
        book.setCategories(categories);

        BigDecimal discountFraction;
        BigDecimal discountAmount = null;
        BigDecimal priceWithDiscount = newBookDTO.getPrice();

        if (newBookDTO.getHasDiscount()) {
            discountFraction = new BigDecimal(newBookDTO.getDiscountPercentage()).divide(new BigDecimal(100));
            discountAmount = newBookDTO.getPrice().multiply(discountFraction);
            priceWithDiscount = newBookDTO.getPrice().subtract(discountAmount);
        }

        Long id = 0L;

        String coverImagePath = saveImage(coverImage);
        switch (newBookDTO.getBookType()) {
            case "Paper book" -> {
                PaperBook paperBook = new PaperBook();
                paperBook.setCoverImageUrl(coverImagePath);
                paperBook.setPrice(newBookDTO.getPrice());
                if (!newBookDTO.getHasDiscount()) {
                    paperBook.setHasDiscount(false);
                    paperBook.setPriceWithDiscount(priceWithDiscount);
                } else {
                    paperBook.setHasDiscount(true);
                    paperBook.setDiscountPercentage(newBookDTO.getDiscountPercentage());
                    paperBook.setDiscountAmount(discountAmount);
                    paperBook.setPriceWithDiscount(priceWithDiscount);
                    paperBook.setDiscountEndDate(newBookDTO.getDiscountEndDate());
                }
                paperBook.setPublisher(newBookDTO.getPublisher());
                paperBook.setNumOfPages(newBookDTO.getNumOfPages());
                paperBook.setCoverType(PaperBookEnum.valueOf(newBookDTO.getCoverType()));
                paperBook.setIsbn(newBookDTO.getIsbn());
                paperBook.setIsAvailable(newBookDTO.getIsAvailable());
                paperBook.setBook(book);
                paperBook.setIsHidden(newBookDTO.getIsHidden());
//                paperBookRepository.save(paperBook);
                book.setPaperBooks(new ArrayList<>(List.of(paperBook)));
                id = bookRepository.save(book).getId();
            }
            case "Ebook" -> {
                String downloadLink = saveBookFile(bookFile, "ebooks");
                EBook ebook = new EBook();
                ebook.setCoverImageUrl(coverImagePath);
                ebook.setPrice(newBookDTO.getPrice());
                if (!newBookDTO.getHasDiscount()) {
                    ebook.setHasDiscount(false);
                    ebook.setPriceWithDiscount(priceWithDiscount);
                } else {
                    ebook.setHasDiscount(true);
                    ebook.setDiscountPercentage(newBookDTO.getDiscountPercentage());
                    ebook.setDiscountAmount(discountAmount);
                    ebook.setPriceWithDiscount(priceWithDiscount);
                    ebook.setDiscountEndDate(newBookDTO.getDiscountEndDate());
                }
                ebook.setPublisher(newBookDTO.getPublisher());
                ebook.setDownloadLink(downloadLink);
                ebook.setNumOfPages(newBookDTO.getNumOfPages());
                ebook.setBook(book);
                ebook.setIsHidden(newBookDTO.getIsHidden());
//                eBookRepository.save(ebook);
                book.setEbook(ebook);
                id = bookRepository.save(book).getId();
            }
            case "Audiobook" -> {
                String downloadLink = saveBookFile(bookFile, "audiobooks");
                AudioBook audioBook = new AudioBook();
                audioBook.setCoverImageUrl(coverImagePath);
                audioBook.setPrice(newBookDTO.getPrice());
                if (!newBookDTO.getHasDiscount()) {
                    audioBook.setHasDiscount(false);
                    audioBook.setPriceWithDiscount(priceWithDiscount);
                } else {
                    audioBook.setHasDiscount(true);
                    audioBook.setDiscountPercentage(newBookDTO.getDiscountPercentage());
                    audioBook.setDiscountAmount(discountAmount);
                    audioBook.setPriceWithDiscount(priceWithDiscount);
                    audioBook.setDiscountEndDate(newBookDTO.getDiscountEndDate());
                }
                audioBook.setPublisher(newBookDTO.getPublisher());
                audioBook.setDownloadLink(downloadLink);
                audioBook.setNarrator(newBookDTO.getNarrator());
                audioBook.setDurationSeconds(newBookDTO.getDurationSeconds());
                audioBook.setBook(book);
                audioBook.setIsHidden(newBookDTO.getIsHidden());
//                audioBookRepository.save(audioBook);
                book.setAudioBook(audioBook);
                id = bookRepository.save(book).getId();
            }
        }
        return id;
    }

    private String saveBookFile(MultipartFile file, String bookType) {
        final String directoryPath = "D:\\book_store\\" + bookType + "\\";

        if (file == null || file.isEmpty()) {
            throw new ApiRequestException("Cannot save an empty file", HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        if (bookType.equals("ebooks") && (contentType == null || !contentType.equals("application/epub+zip") || !originalFilename.toLowerCase().endsWith(".epub"))) {
            throw new ApiRequestException("Only EPUB files are allowed", HttpStatus.BAD_REQUEST);
        }

        if (bookType.equals("audiobooks") && (contentType == null || !contentType.equals("audio/mpeg") || !originalFilename.toLowerCase().endsWith(".mp3"))) {
            throw new ApiRequestException("Only MP3 files are allowed", HttpStatus.BAD_REQUEST);
        }

        String fileExtension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            fileExtension = originalFilename.substring(lastDotIndex);
        }

        String fileName = UUID.randomUUID().toString() + fileExtension;
        String filePath = directoryPath + fileName;

        try {
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            file.transferTo(new File(filePath));
            return filePath;
        } catch (IOException e) {
            throw new ApiRequestException("Failed to save the file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private String saveImage(MultipartFile file) {
        final String directoryPath = "D:\\book_store\\cover_images\\";

        if (file == null || file.isEmpty()) {
            throw new ApiRequestException("Cannot save an empty file", HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image")) {
            throw new ApiRequestException("Only images are allowed", HttpStatus.BAD_REQUEST);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null) {
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                fileExtension = originalFilename.substring(lastDotIndex);
            }
        }

        String fileName = UUID.randomUUID().toString() + fileExtension;
        String filePath = directoryPath + fileName;

        try {
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            file.transferTo(new File(filePath));
            return filePath;
        } catch (IOException e) {
            throw new ApiRequestException("Failed to save the file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public MessageResponse setPaperBook(Long id, NewPaperBookDTO newPaperBookDTO, MultipartFile coverImageFile) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        PaperBook paperBook = modelMapper.map(newPaperBookDTO, PaperBook.class);

        PaperBook existingPaperBook = null;
        if (newPaperBookDTO.getId() != null) {
            existingPaperBook = paperBookRepository.findById(newPaperBookDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Paper book", "id", newPaperBookDTO.getId()));
        }

        if (coverImageFile != null) {
            String imagePath = saveImage(coverImageFile);
            paperBook.setCoverImageUrl(imagePath);
            if (existingPaperBook != null) {
                File fileToDelete = new File(existingPaperBook.getCoverImageUrl());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        } else if (existingPaperBook != null) {
            paperBook.setId(newPaperBookDTO.getId());
            paperBook.setCoverImageUrl(existingPaperBook.getCoverImageUrl());
        } else {
            throw new ApiRequestException("Provide cover image file", HttpStatus.BAD_REQUEST);
        }

        paperBook.setBook(book);
        paperBook.setIsHidden(newPaperBookDTO.getIsHidden());

        BigDecimal discountFraction;
        BigDecimal discountAmount = null;
        BigDecimal priceWithDiscount = newPaperBookDTO.getPrice();

        if (newPaperBookDTO.getHasDiscount()) {
            discountFraction = new BigDecimal(newPaperBookDTO.getDiscountPercentage()).divide(new BigDecimal(100));
            discountAmount = newPaperBookDTO.getPrice().multiply(discountFraction);
            priceWithDiscount = newPaperBookDTO.getPrice().subtract(discountAmount);
        }
        paperBook.setDiscountAmount(discountAmount);
        paperBook.setPriceWithDiscount(priceWithDiscount);
        paperBook.setCoverType(PaperBookEnum.valueOf(newPaperBookDTO.getCoverType()));

        paperBookRepository.save(paperBook);
        book.addPaperBook(paperBook);
        bookRepository.save(book);

        return new MessageResponse(true, "Paper book has been successfully updated or added to book with id " + id);
    }

    public MessageResponse setEbook(Long id, NewEBookDTO newEBookDTO, MultipartFile coverImageFile, MultipartFile bookFile) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        EBook ebook = modelMapper.map(newEBookDTO, EBook.class);

        EBook existingEbook = book.getEbook();
        if (existingEbook != null) {
            ebook.setId(existingEbook.getId());
        }

        if (coverImageFile != null) {
            String imagePath = saveImage(coverImageFile);
            ebook.setCoverImageUrl(imagePath);
            if (existingEbook != null) {
                File fileToDelete = new File(existingEbook.getCoverImageUrl());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        } else if (existingEbook != null) {
            ebook.setCoverImageUrl(existingEbook.getCoverImageUrl());
        } else {
            throw new ApiRequestException("Provide cover image file", HttpStatus.BAD_REQUEST);
        }

        if (bookFile != null) {
            String bookFilePath = saveBookFile(bookFile, "ebooks");
            ebook.setDownloadLink(bookFilePath);
            if (existingEbook != null) {
                File fileToDelete = new File(existingEbook.getDownloadLink());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        } else if (existingEbook != null) {
            ebook.setDownloadLink(existingEbook.getDownloadLink());
        } else {
            throw new ApiRequestException("Provide book file", HttpStatus.BAD_REQUEST);
        }

        ebook.setBook(book);
        ebook.setIsHidden(newEBookDTO.getIsHidden());

        BigDecimal discountFraction;
        BigDecimal discountAmount = null;
        BigDecimal priceWithDiscount = newEBookDTO.getPrice();

        if (newEBookDTO.getHasDiscount()) {
            discountFraction = new BigDecimal(newEBookDTO.getDiscountPercentage()).divide(new BigDecimal(100));
            discountAmount = newEBookDTO.getPrice().multiply(discountFraction);
            priceWithDiscount = newEBookDTO.getPrice().subtract(discountAmount);
        }
        ebook.setDiscountAmount(discountAmount);
        ebook.setPriceWithDiscount(priceWithDiscount);

        eBookRepository.save(ebook);
        book.setEbook(ebook);
        bookRepository.save(book);

        return new MessageResponse(true, "Ebook has been successfully set to book with id " + id);
    }

    public MessageResponse setAudiobook(Long id, NewAudioBookDTO newAudiobookDTO, MultipartFile coverImageFile, MultipartFile bookFile) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        AudioBook audioBook = modelMapper.map(newAudiobookDTO, AudioBook.class);

        AudioBook existingAudiobook = book.getAudioBook();
        if (existingAudiobook != null) {
            audioBook.setId(existingAudiobook.getId());
        }

        if (coverImageFile != null) {
            String imagePath = saveImage(coverImageFile);
            audioBook.setCoverImageUrl(imagePath);
            if (existingAudiobook != null) {
                File fileToDelete = new File(existingAudiobook.getCoverImageUrl());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        } else if (existingAudiobook != null) {
            audioBook.setCoverImageUrl(existingAudiobook.getCoverImageUrl());
        } else {
            throw new ApiRequestException("Provide cover image file", HttpStatus.BAD_REQUEST);
        }

        if (bookFile != null) {
            String bookFilePath = saveBookFile(bookFile, "audiobooks");
            audioBook.setDownloadLink(bookFilePath);
            if (existingAudiobook != null) {
                File fileToDelete = new File(existingAudiobook.getDownloadLink());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        } else if (existingAudiobook != null) {
            audioBook.setDownloadLink(existingAudiobook.getDownloadLink());
        } else {
            throw new ApiRequestException("Provide book file", HttpStatus.BAD_REQUEST);
        }

        audioBook.setBook(book);
        audioBook.setIsHidden(newAudiobookDTO.getIsHidden());

        BigDecimal discountFraction;
        BigDecimal discountAmount = null;
        BigDecimal priceWithDiscount = newAudiobookDTO.getPrice();

        if (newAudiobookDTO.getHasDiscount()) {
            discountFraction = new BigDecimal(newAudiobookDTO.getDiscountPercentage()).divide(new BigDecimal(100));
            discountAmount = newAudiobookDTO.getPrice().multiply(discountFraction);
            priceWithDiscount = newAudiobookDTO.getPrice().subtract(discountAmount);
        }
        audioBook.setDiscountAmount(discountAmount);
        audioBook.setPriceWithDiscount(priceWithDiscount);

        audioBookRepository.save(audioBook);
        book.setAudioBook(audioBook);
        bookRepository.save(book);

        return new MessageResponse(true, "Audiobook has been successfully set to book with id " + id);
    }

    public MessageResponse updateBook(Long id, UpdateBookDTO updateBookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        book.setTitle(updateBookDTO.getTitle());
        book.setAuthor(updateBookDTO.getAuthor());
        book.setDescription(updateBookDTO.getDescription());
        book.setPublicationDate(updateBookDTO.getPublicationDate());
        Set<Category> categories = new HashSet<>();
        for (Long categoryId : updateBookDTO.getCategories()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
            categories.add(category);
        }
        book.setCategories(categories);
        bookRepository.save(book);
        return new MessageResponse(true, "Book has been successfully updated");
    }

    public MessageResponse addPromoCode(NewPromoCodeDTO newPromoCodeDTO) {
        PromoCode promoCode = modelMapper.map(newPromoCodeDTO, PromoCode.class);
        promoCode.setIsActive(newPromoCodeDTO.getEndDate().isAfter(LocalDateTime.now()));
        promoCodeRepository.save(promoCode);
        return new MessageResponse(true, "Promo code has been added successfully");
    }

    public MessageResponse deletePromoCode(String code) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code", "code", code));
        promoCode.setIsActive(false);
        promoCode.setEndDate(LocalDateTime.now());
        promoCodeRepository.save(promoCode);
        return new MessageResponse(true, "Promo code has been deleted successfully");
    }

    public PageableResponse getReviews(Integer pageNumber, Integer pageSize, String username) {
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize);
        Page<Review> reviewsPage;
        if (username != null) {
            reviewsPage = reviewRepository.findByUser_UsernameContains(username, pageDetails);
        } else {
            reviewsPage = reviewRepository.findAll(pageDetails);
        }
        List<ReviewDTO> reviewDTOS = reviewsPage.stream().map(
                (review) -> {
                    ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
                    reviewDTO.setUserName(review.getUser().getUsername());
                    return reviewDTO;
                }).collect(Collectors.toList()
        );
        PageableResponse response = new PageableResponse();

        response.setContent(reviewDTOS);
        response.setPageSize(pageSize);
        response.setPageNumber(pageNumber);
        response.setTotalPages(reviewsPage.getTotalPages());
        response.setTotalElements(reviewsPage.getTotalElements());

        return response;
    }

    public PageableResponse getOrders(Integer pageNumber, Integer pageSize, Long findById) {
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize);
        Page<Order> ordersPage;
        if (findById != null) {
            ordersPage = orderRepository.findById(findById, pageDetails);
        } else {
            ordersPage = orderRepository.findAllByOrderByIdDesc(pageDetails);
        }
        PageableResponse response = new PageableResponse();
        List<OrderDTO> orderDTOS = ordersPage.stream().map((order) -> {
            OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
            orderDTO.setUsername(order.getUser().getUsername());
            return orderDTO;
        }).collect(Collectors.toList());
        response.setContent(orderDTOS);
        response.setPageSize(pageSize);
        response.setPageNumber(pageNumber);
        response.setTotalPages(ordersPage.getTotalPages());
        response.setTotalElements(ordersPage.getTotalElements());
        return response;
    }

    public MessageResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        order.setOrderStatus(status);
        orderRepository.save(order);
        return new MessageResponse(true, "Order status has been changed to " + status);
    }

    public <T> T getObjectFromJson(String json, Class<T> valueType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new ApiRequestException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
