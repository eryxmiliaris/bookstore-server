package com.vb.bookstore.services;

import com.vb.bookstore.entities.Book;
import com.vb.bookstore.entities.Category;
import com.vb.bookstore.entities.PaperBook;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.books.BookDTO;
import com.vb.bookstore.payloads.books.BookMainInfoDTO;
import com.vb.bookstore.payloads.books.BookResponse;
import com.vb.bookstore.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "bookId", id));

        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
        bookDTO.setCategories(book.getCategories().stream().map(Category::getCategoryName).toList());

        return bookDTO;
    }

    public BookResponse getBooksWithFilters(
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

        Double priceStartDouble = priceStart == null ? 0.0 : Double.parseDouble(priceStart);
        Double priceEndDouble = priceEnd == null ? 100000.0 : Double.parseDouble(priceEnd);
        Double priceStartPB = priceStartDouble;
        Double priceEndPB = priceEndDouble;
        Double priceStartEB = priceStartDouble;
        Double priceEndEB = priceEndDouble;
        Double priceStartAB = priceStartDouble;
        Double priceEndAB = priceEndDouble;
        if (bookTypes != null) {
            List<String> bookTypesList = Arrays.asList(bookTypes);
            if (bookTypesList.contains("Paper book")) {
                priceStartPB = priceStartDouble;
                priceEndPB = priceEndDouble;
            } else {
                priceStartPB = null;
                priceEndPB = null;
            }

            if (bookTypesList.contains("Ebook")) {
                priceStartEB = priceStartDouble;
                priceEndEB = priceEndDouble;
            } else {
                priceStartEB = null;
                priceEndEB = null;
            }

            if (bookTypesList.contains("Audio book")) {
                priceStartAB = priceStartDouble;
                priceEndAB = priceEndDouble;
            } else {
                priceStartAB = null;
                priceEndAB = null;
            }
        }

        Page<Book> books = bookRepository.findByFilterParams(
                bookTitle
                , hasCategories
                , categoriesList
                , priceStartPB, priceEndPB
                , priceStartEB, priceEndEB
                , priceStartAB, priceEndAB
                ,pageDetails
        );

        List<BookMainInfoDTO> pageContentDTO = bookStreamToBookDtoList(books.stream());

        BookResponse bookResponse = new BookResponse();
        bookResponse.setContent(pageContentDTO);
        bookResponse.setPageNumber(pageNumber);
        bookResponse.setPageSize(pageSize);
        bookResponse.setTotalPages(books.getTotalPages());
        bookResponse.setTotalElements(books.getTotalElements());

        return bookResponse;
    }

    private List<BookMainInfoDTO> bookStreamToBookDtoList(Stream<Book> books) {
        return books.map(book -> {
                    BookMainInfoDTO bookResponse = modelMapper.map(book, BookMainInfoDTO.class);
                    List<String> types = new ArrayList<>();

                    if (!book.getPaperBooks().isEmpty()) {
                        types.add("Paper book");
                    }
                    if (book.getEbook() != null) {
                        types.add("Ebook");
                    }
                    if (book.getAudioBook() != null) {
                        types.add("Audio book");
                    }

                    String coverImageUrl = null;

                    if (types.contains("Paper book")) {
                        coverImageUrl = book.getPaperBooks().get(0).getCoverImageUrl();
                    } else if (types.contains("Ebook")) {
                        coverImageUrl = book.getEbook().getCoverImageUrl();
                    } else if (types.contains("Audio book")) {
                        coverImageUrl = book.getAudioBook().getCoverImageUrl();
                    }
                    bookResponse.setCoverImageUrl(coverImageUrl);
                    bookResponse.setBookTypes(types);
                    return bookResponse;
                })
                .toList();
    }

}
