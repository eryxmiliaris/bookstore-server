package com.vb.bookstore.services;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.PageableResponse;
import com.vb.bookstore.payloads.admin.*;
import com.vb.bookstore.payloads.books.CategoryDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {
    Long addNewBook(NewBookDTO newBookDTO, MultipartFile coverImage, MultipartFile bookFile, MultipartFile previewFile);

    MessageResponse setPaperBook(Long id, NewPaperBookDTO newPaperBookDTO, MultipartFile coverImageFile);

    MessageResponse setEbook(Long id, NewEbookDTO newEbookDTO, MultipartFile coverImageFile, MultipartFile bookFile, MultipartFile previewFile);

    MessageResponse setAudiobook(Long id, NewAudiobookDTO newAudiobookDTO, MultipartFile coverImageFile, MultipartFile bookFile, MultipartFile previewFile);

    MessageResponse updateBook(Long id, UpdateBookDTO updateBookDTO);

    MessageResponse addCategory(CategoryDTO newCategoryDTO);

    MessageResponse addPromoCode(NewPromoCodeDTO newPromoCodeDTO);

    MessageResponse deletePromoCode(String code);

    PageableResponse getReviews(Integer pageNumber, Integer pageSize, String username);

    PageableResponse getOrders(Integer pageNumber, Integer pageSize, Long findById);

    MessageResponse updateOrderStatus(Long id, String status);

    <T> T getObjectFromJson(String json, Class<T> valueType);
}
