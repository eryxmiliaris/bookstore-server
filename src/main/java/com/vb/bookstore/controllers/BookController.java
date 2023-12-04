package com.vb.bookstore.controllers;

import com.vb.bookstore.config.AppConstants;
import com.vb.bookstore.payloads.PageableResponse;
import com.vb.bookstore.payloads.books.BookDTO;
import com.vb.bookstore.payloads.books.BookMainInfoDTO;
import com.vb.bookstore.payloads.books.CategoryDTO;
import com.vb.bookstore.services.BookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<PageableResponse> getAllBooks(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "8", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "desc", required = false) String sortOrder,
            @RequestParam(name = "priceStart", required = false) String priceStart,
            @RequestParam(name = "priceEnd", required = false) String priceEnd,
            @RequestParam(name = "category", required = false) String[] categories,
            @RequestParam(name = "bookType", required = false) String[] bookTypes,
            @RequestParam(name = "bookTitle", required = false) String bookTitle
    ) {
        PageableResponse bookResponse = bookService.getBooksWithFilters(pageNumber, pageSize, sortBy, sortOrder, priceStart, priceEnd, categories, bookTypes, bookTitle);

        return ResponseEntity.ok(bookResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        BookDTO bookDTO = bookService.getBookById(id);
        return ResponseEntity.ok(bookDTO);
    }

    @GetMapping(
            value = "/{id}/coverImage",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] getBookCoverImage(
            @PathVariable Long id,
            @RequestParam String bookType,
            @RequestParam(required = false) Long paperBookId
    ) {
        return bookService.getBookCoverImage(id, bookType, paperBookId);
    }

    @GetMapping(value = {"/{id}/preview.epub", "/{id}/preview.mp3"})
    public ResponseEntity<Resource> downloadBookPreview(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        String bookType;
        if (request.getRequestURI().contains("preview.epub")) {
            bookType = AppConstants.EBOOK;
        } else {
            bookType = AppConstants.AUDIOBOOK;
        }
        Resource book = bookService.downloadBookPreview(id, bookType);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + book.getFilename());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(book);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<BookMainInfoDTO>> getPopularBooks() {
        List<BookMainInfoDTO> popularBooks = bookService.getPopularBooks();
        return ResponseEntity.ok(popularBooks);
    }

    @GetMapping("/recommended")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookMainInfoDTO>> getRecommendedBooks() {
        List<BookMainInfoDTO> recommendedBooks = bookService.getRecommendedBooks();
        return ResponseEntity.ok(recommendedBooks);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categoryDTOS = bookService.getAllCategories();
        return ResponseEntity.ok(categoryDTOS);
    }
}
