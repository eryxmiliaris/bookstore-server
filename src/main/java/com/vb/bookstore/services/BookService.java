package com.vb.bookstore.services;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.BookCategory;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.books.BookDTO;
import com.vb.bookstore.payloads.books.BooksResponse;
import com.vb.bookstore.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BooksResponse getAllBooks() {
        List<Book> books = bookRepository.findAll();
        List<BookDTO> bookDTOS = books.stream()
                .map(book -> {
                    BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                    bookDTO.setCategories(book.getBookCategories().stream().map(BookCategory::getCategoryName).toList());
                    return bookDTO;
                })
                .toList();
        BooksResponse bookResponse = new BooksResponse();
        bookResponse.setContent(bookDTOS);
        return bookResponse;
    }

    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "bookId", id));

        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
        bookDTO.setCategories(book.getBookCategories().stream().map(BookCategory::getCategoryName).toList());

        return bookDTO;
    }
}
