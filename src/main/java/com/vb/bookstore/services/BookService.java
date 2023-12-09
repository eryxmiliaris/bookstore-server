package com.vb.bookstore.services;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.payloads.PageableResponse;
import com.vb.bookstore.payloads.books.BookDTO;
import com.vb.bookstore.payloads.books.BookMainInfoDTO;
import com.vb.bookstore.payloads.books.CategoryDTO;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.stream.Stream;

public interface BookService {
    BookDTO getBookById(Long id);

    PageableResponse getBooksWithFilters(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder,
            String priceStart,
            String priceEnd,
            String[] categories,
            String[] bookTypes,
            String bookTitle
    );

    List<BookMainInfoDTO> bookStreamToBookDtoList(Stream<Book> books);

    byte[] getBookCoverImage(Long id, String bookType, Long paperBookId);

    List<BookMainInfoDTO> getPopularBooks();

    void updatePopularityScore();

    List<BookMainInfoDTO> getRecommendedBooks();

    List<CategoryDTO> getAllCategories();

    Resource downloadBookPreview(Long id, String bookType);
}
