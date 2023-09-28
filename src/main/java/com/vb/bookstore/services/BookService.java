package com.vb.bookstore.services;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.BookCategory;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.books.BookDTO;
import com.vb.bookstore.payloads.books.BookResponse;
import com.vb.bookstore.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(book -> {
                    BookResponse bookResponse = modelMapper.map(book, BookResponse.class);
                    List<String> types = new ArrayList<>();

                    if (!book.getPaperBooks().isEmpty()) {
                        types.add("Paper book");
                    }
                    if (book.getEBook() != null) {
                        types.add("Ebook");
                    }
                    if (book.getAudioBook() != null) {
                        types.add("Audio book");
                    }

                    String coverImageUrl = null;

                    if (types.contains("Paper book")) {
                        coverImageUrl = book.getPaperBooks().get(0).getCoverImageUrl();
                    } else if (types.contains("Ebook")) {
                        coverImageUrl = book.getEBook().getCoverImageUrl();
                    } else if (types.contains("Audio book")) {
                        coverImageUrl = book.getAudioBook().getCoverImageUrl();
                    }
                    bookResponse.setCoverImageUrl(coverImageUrl);
                    bookResponse.setBookTypes(types);
                    return bookResponse;
                })
                .toList();
    }

    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "bookId", id));

        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
        bookDTO.setCategories(book.getBookCategories().stream().map(BookCategory::getCategoryName).toList());

        return bookDTO;
    }
}
