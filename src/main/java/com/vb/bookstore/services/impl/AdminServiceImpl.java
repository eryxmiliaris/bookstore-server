package com.vb.bookstore.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vb.bookstore.config.AppConstants;
import com.vb.bookstore.entities.*;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.PageableResponse;
import com.vb.bookstore.payloads.admin.*;
import com.vb.bookstore.payloads.books.CategoryDTO;
import com.vb.bookstore.payloads.books.ReviewDTO;
import com.vb.bookstore.payloads.order.OrderDTO;
import com.vb.bookstore.repositories.*;
import com.vb.bookstore.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
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
public class AdminServiceImpl implements AdminService {
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    private final CategoryRepository categoryRepository;
    private final EbookRepository ebookRepository;
    private final BookRepository bookRepository;
    private final PaperBookRepository paperBookRepository;
    private final AudiobookRepository audiobookRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Value("${bookstore.file.directory}")
    private String baseDirectory;

    public Long addNewBook(
            NewBookDTO newBookDTO,
            MultipartFile coverImage,
            MultipartFile bookFile,
            MultipartFile previewFile
    ) {
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
            case AppConstants.PAPER_BOOK -> {
                PaperBook paperBook = new PaperBook();
                paperBook.setCoverImagePath(coverImagePath);
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
                paperBook.setCoverType(newBookDTO.getCoverType());
                paperBook.setIsbn(newBookDTO.getIsbn());
                paperBook.setIsAvailable(newBookDTO.getIsAvailable());
                paperBook.setBook(book);
                paperBook.setIsHidden(newBookDTO.getIsHidden());
                book.setPaperBooks(new ArrayList<>(List.of(paperBook)));
                id = bookRepository.save(book).getId();
            }
            case AppConstants.EBOOK -> {
                String bookPath = saveBookFile(bookFile, "ebooks", false);
                String previewPath = saveBookFile(previewFile, "ebooks", true);
                Ebook ebook = new Ebook();
                ebook.setCoverImagePath(coverImagePath);
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
                ebook.setBookPath(bookPath);
                ebook.setPreviewPath(previewPath);
                ebook.setNumOfPages(newBookDTO.getNumOfPages());
                ebook.setBook(book);
                ebook.setIsHidden(newBookDTO.getIsHidden());
                book.setEbook(ebook);
                id = bookRepository.save(book).getId();
            }
            case AppConstants.AUDIOBOOK -> {
                String bookPath = saveBookFile(bookFile, "audiobooks", false);
                String previewPath = saveBookFile(previewFile, "audiobooks", true);
                Audiobook audiobook = new Audiobook();
                audiobook.setCoverImagePath(coverImagePath);
                audiobook.setPrice(newBookDTO.getPrice());
                if (!newBookDTO.getHasDiscount()) {
                    audiobook.setHasDiscount(false);
                    audiobook.setPriceWithDiscount(priceWithDiscount);
                } else {
                    audiobook.setHasDiscount(true);
                    audiobook.setDiscountPercentage(newBookDTO.getDiscountPercentage());
                    audiobook.setDiscountAmount(discountAmount);
                    audiobook.setPriceWithDiscount(priceWithDiscount);
                    audiobook.setDiscountEndDate(newBookDTO.getDiscountEndDate());
                }
                audiobook.setPublisher(newBookDTO.getPublisher());
                audiobook.setBookPath(bookPath);
                audiobook.setPreviewPath(previewPath);
                audiobook.setNarrator(newBookDTO.getNarrator());
                audiobook.setDurationSeconds(newBookDTO.getDurationSeconds());
                audiobook.setBook(book);
                audiobook.setIsHidden(newBookDTO.getIsHidden());
                book.setAudiobook(audiobook);
                id = bookRepository.save(book).getId();
            }
        }
        return id;
    }

    private String saveBookFile(MultipartFile file, String bookType, boolean preview) {
        final String directoryPath = baseDirectory + (preview ? "previews\\" : "") + bookType + "\\";

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
        final String directoryPath = baseDirectory + "cover_images\\";

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
            paperBook.setCoverImagePath(imagePath);
            if (existingPaperBook != null) {
                File fileToDelete = new File(existingPaperBook.getCoverImagePath());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        } else if (existingPaperBook != null) {
            paperBook.setId(newPaperBookDTO.getId());
            paperBook.setCoverImagePath(existingPaperBook.getCoverImagePath());
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
        paperBook.setCoverType(newPaperBookDTO.getCoverType());

        if (existingPaperBook != null) {
            List<CartItem> cartItems = cartItemRepository.findByPaperBookIdIn(Collections.singletonList(paperBook.getId()));

            Set<Cart> carts = new HashSet<>();
            cartItems.forEach(cartItem -> carts.add(cartItem.getCart()));

            cartItems.forEach(cartItem -> {
                cartItem.setHasDiscount(paperBook.getHasDiscount());
                cartItem.setPriceWithDiscount(paperBook.getPriceWithDiscount());
                cartItem.setTotalPrice(paperBook.getPriceWithDiscount().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            });

            carts.forEach(Cart::updateTotalPrice);

            cartRepository.saveAll(carts);
            cartItemRepository.saveAll(cartItems);
        }

        paperBookRepository.save(paperBook);
        book.addPaperBook(paperBook);
        bookRepository.save(book);

        return new MessageResponse(true, "Paper book has been successfully updated or added to book with id " + id);
    }

    public MessageResponse setEbook(
            Long id,
            NewEbookDTO newEbookDTO,
            MultipartFile coverImageFile,
            MultipartFile bookFile,
            MultipartFile previewFile
    ) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        Ebook ebook = modelMapper.map(newEbookDTO, Ebook.class);

        Ebook existingEbook = book.getEbook();
        if (existingEbook != null) {
            ebook.setId(existingEbook.getId());
        }

        if (coverImageFile != null) {
            String imagePath = saveImage(coverImageFile);
            ebook.setCoverImagePath(imagePath);
            if (existingEbook != null) {
                File fileToDelete = new File(existingEbook.getCoverImagePath());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        } else if (existingEbook != null) {
            ebook.setCoverImagePath(existingEbook.getCoverImagePath());
        } else {
            throw new ApiRequestException("Provide cover image file", HttpStatus.BAD_REQUEST);
        }

        if (bookFile != null) {
            String bookFilePath = saveBookFile(bookFile, "ebooks", false);
            ebook.setBookPath(bookFilePath);
            if (existingEbook != null) {
                File fileToDelete = new File(existingEbook.getBookPath());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        } else if (existingEbook != null) {
            ebook.setBookPath(existingEbook.getBookPath());
        } else {
            throw new ApiRequestException("Provide book file", HttpStatus.BAD_REQUEST);
        }

        if (previewFile != null) {
            String previewFilePath = saveBookFile(previewFile, "ebooks", true);
            ebook.setPreviewPath(previewFilePath);
            if (existingEbook != null) {
                File fileToDelete = new File(existingEbook.getPreviewPath());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        } else if (existingEbook != null) {
            ebook.setPreviewPath(existingEbook.getPreviewPath());
        } else {
            throw new ApiRequestException("Provide preview file", HttpStatus.BAD_REQUEST);
        }

        ebook.setBook(book);
        ebook.setIsHidden(newEbookDTO.getIsHidden());

        BigDecimal discountFraction;
        BigDecimal discountAmount = null;
        BigDecimal priceWithDiscount = newEbookDTO.getPrice();

        if (newEbookDTO.getHasDiscount()) {
            discountFraction = new BigDecimal(newEbookDTO.getDiscountPercentage()).divide(new BigDecimal(100));
            discountAmount = newEbookDTO.getPrice().multiply(discountFraction);
            priceWithDiscount = newEbookDTO.getPrice().subtract(discountAmount);
        }
        ebook.setDiscountAmount(discountAmount);
        ebook.setPriceWithDiscount(priceWithDiscount);

        if (existingEbook != null) {
            List<CartItem> cartItems = cartItemRepository.findByBookTypeAndBook_IdIn(AppConstants.EBOOK, Collections.singletonList(ebook.getId()));

            Set<Cart> carts = new HashSet<>();
            cartItems.forEach(cartItem -> carts.add(cartItem.getCart()));

            cartItems.forEach(cartItem -> {
                cartItem.setHasDiscount(ebook.getHasDiscount());
                cartItem.setPriceWithDiscount(ebook.getPriceWithDiscount());
                cartItem.setTotalPrice(ebook.getPriceWithDiscount());
            });

            carts.forEach(Cart::updateTotalPrice);

            cartRepository.saveAll(carts);
            cartItemRepository.saveAll(cartItems);
        }

        ebookRepository.save(ebook);
        book.setEbook(ebook);
        bookRepository.save(book);

        return new MessageResponse(true, "Ebook has been successfully set to book with id " + id);
    }

    public MessageResponse setAudiobook(
            Long id,
            NewAudiobookDTO newAudiobookDTO,
            MultipartFile coverImageFile,
            MultipartFile bookFile,
            MultipartFile previewFile
    ) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        Audiobook audiobook = modelMapper.map(newAudiobookDTO, Audiobook.class);

        Audiobook existingAudiobook = book.getAudiobook();
        if (existingAudiobook != null) {
            audiobook.setId(existingAudiobook.getId());
        }

        if (coverImageFile != null) {
            String imagePath = saveImage(coverImageFile);
            audiobook.setCoverImagePath(imagePath);
            if (existingAudiobook != null) {
                File fileToDelete = new File(existingAudiobook.getCoverImagePath());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        } else if (existingAudiobook != null) {
            audiobook.setCoverImagePath(existingAudiobook.getCoverImagePath());
        } else {
            throw new ApiRequestException("Provide cover image file", HttpStatus.BAD_REQUEST);
        }

        if (bookFile != null) {
            String bookFilePath = saveBookFile(bookFile, "audiobooks", false);
            audiobook.setBookPath(bookFilePath);
            if (existingAudiobook != null) {
                File fileToDelete = new File(existingAudiobook.getBookPath());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        } else if (existingAudiobook != null) {
            audiobook.setBookPath(existingAudiobook.getBookPath());
        } else {
            throw new ApiRequestException("Provide book file", HttpStatus.BAD_REQUEST);
        }

        if (previewFile != null) {
            String previewFilePath = saveBookFile(previewFile, "audiobooks", true);
            audiobook.setPreviewPath(previewFilePath);
            if (existingAudiobook != null) {
                File fileToDelete = new File(existingAudiobook.getPreviewPath());
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        } else if (existingAudiobook != null) {
            audiobook.setPreviewPath(existingAudiobook.getPreviewPath());
        } else {
            throw new ApiRequestException("Provide preview file", HttpStatus.BAD_REQUEST);
        }

        audiobook.setBook(book);
        audiobook.setIsHidden(newAudiobookDTO.getIsHidden());

        BigDecimal discountFraction;
        BigDecimal discountAmount = null;
        BigDecimal priceWithDiscount = newAudiobookDTO.getPrice();

        if (newAudiobookDTO.getHasDiscount()) {
            discountFraction = new BigDecimal(newAudiobookDTO.getDiscountPercentage()).divide(new BigDecimal(100));
            discountAmount = newAudiobookDTO.getPrice().multiply(discountFraction);
            priceWithDiscount = newAudiobookDTO.getPrice().subtract(discountAmount);
        }
        audiobook.setDiscountAmount(discountAmount);
        audiobook.setPriceWithDiscount(priceWithDiscount);

        if (existingAudiobook != null) {
            List<CartItem> cartItems = cartItemRepository.findByBookTypeAndBook_IdIn(AppConstants.AUDIOBOOK, Collections.singletonList(audiobook.getId()));

            Set<Cart> carts = new HashSet<>();
            cartItems.forEach(cartItem -> carts.add(cartItem.getCart()));

            cartItems.forEach(cartItem -> {
                cartItem.setHasDiscount(audiobook.getHasDiscount());
                cartItem.setPriceWithDiscount(audiobook.getPriceWithDiscount());
                cartItem.setTotalPrice(audiobook.getPriceWithDiscount());
            });

            carts.forEach(Cart::updateTotalPrice);

            cartRepository.saveAll(carts);
            cartItemRepository.saveAll(cartItems);
        }

        audiobookRepository.save(audiobook);
        book.setAudiobook(audiobook);
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

    public MessageResponse addCategory(CategoryDTO newCategoryDTO) {
        Category category = modelMapper.map(newCategoryDTO, Category.class);
        categoryRepository.save(category);
        return new MessageResponse(true, "Category has been added successfully");
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
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new ApiRequestException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
