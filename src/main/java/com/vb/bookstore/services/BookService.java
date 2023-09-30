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
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookResponse getAllBooks(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Book> pageBooks = bookRepository.findAll(pageDetails);
        List<BookMainInfoDTO> dtos = bookStreamToBookDtoList(pageBooks.stream());
        BookResponse bookResponse = new BookResponse();
        bookResponse.setContent(dtos);
        bookResponse.setPageNumber(pageBooks.getNumber());
        bookResponse.setPageSize(pageBooks.getSize());
        bookResponse.setTotalPages(pageBooks.getTotalPages());
        bookResponse.setTotalElements(pageBooks.getTotalElements());

        return bookResponse;
    }

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
            String[] bookTypes
    ) {
        List<Book> books;
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        if (categories != null) {
            books = bookRepository.findByCategories_CategoryNameIn(List.of(categories), sortByAndOrder);
        } else {
            books = bookRepository.findAll(sortByAndOrder);
        }

        boolean paperBook = false;
        boolean ebook = false;
        boolean audiobook = false;
        if (bookTypes == null) {
            paperBook = true;
            ebook = true;
            audiobook = true;
        } else {
            for (String s : bookTypes) {
                switch (s) {
                    case "Paper book" -> paperBook = true;
                    case "Ebook" -> ebook = true;
                    case "Audio book" -> audiobook = true;
                    default -> {
                    }
                }
            }
        }
        boolean finalPaperBook = paperBook;
        boolean finalEbook = ebook;
        boolean finalAudiobook = audiobook;

        double minPrice;
        double maxPrice;
        if (priceStart == null) {
            minPrice = 0;
        } else {
            minPrice = Double.parseDouble(priceStart);
        }
        if (priceEnd == null) {
            maxPrice = Double.MAX_VALUE;
        } else {
            maxPrice = Double.parseDouble(priceEnd);
        }

        books = books.stream().filter(book -> {
            if (finalPaperBook && !book.getPaperBooks().isEmpty()) {
                for (PaperBook pb : book.getPaperBooks()) {
                    if (pb.getPrice().doubleValue() > minPrice && pb.getPrice().doubleValue() < maxPrice) {
                        return true;
                    }
                }
                ;
            }
            if (finalEbook && book.getEBook() != null && book.getEBook().getPrice().doubleValue() > minPrice && book.getEBook().getPrice().doubleValue() < maxPrice) {
                return true;
            }
            if (finalAudiobook && book.getAudioBook() != null && book.getAudioBook().getPrice().doubleValue() > minPrice && book.getAudioBook().getPrice().doubleValue() < maxPrice) {
                return true;
            }
            return false;
        }).toList();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize);
        int start = (int) pageDetails.getOffset();
        int end = Math.min((start + pageDetails.getPageSize()), books.size());
        List<Book> pageContent = books.subList(start, end);
        List<BookMainInfoDTO> pageContentDTO = bookStreamToBookDtoList(pageContent.stream());

        BookResponse bookResponse = new BookResponse();
        bookResponse.setContent(pageContentDTO);
        bookResponse.setPageNumber(pageNumber);
        bookResponse.setPageSize(pageSize);
        bookResponse.setTotalPages((int) Math.ceil(books.size() * 1.0 / pageSize));
        bookResponse.setTotalElements((long) books.size());

        return bookResponse;
    }


    private List<BookMainInfoDTO> bookStreamToBookDtoList(Stream<Book> books) {
        return books.map(book -> {
                    BookMainInfoDTO bookResponse = modelMapper.map(book, BookMainInfoDTO.class);
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
}
