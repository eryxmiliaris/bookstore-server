package com.vb.bookstore.controllers;

import com.vb.bookstore.payloads.books.BookDTO;
import com.vb.bookstore.payloads.books.BooksResponse;
import com.vb.bookstore.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class BookController {
    private final BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<BooksResponse> getAllBooks() {
        BooksResponse bookResponse = bookService.getAllBooks();

        return new ResponseEntity<>(bookResponse, HttpStatus.FOUND);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        BookDTO bookDTO = bookService.getBookById(id);

        return new ResponseEntity<>(bookDTO, HttpStatus.FOUND);
    }
}
