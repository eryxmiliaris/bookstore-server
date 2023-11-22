package com.vb.bookstore.controllers;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.PageableResponse;
import com.vb.bookstore.payloads.admin.*;
import com.vb.bookstore.services.AdminService;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final Validator validator;

    @PostMapping(value = "/books",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE
            })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> addNewBook(
            @RequestPart String newBook,
            @RequestPart MultipartFile coverImageFile,
            @RequestPart(required = false) MultipartFile bookFile
    ) {
        NewBookDTO newBookDTO = adminService.getObjectFromJson(newBook, NewBookDTO.class);
        validator.validate(newBookDTO);
        Long id = adminService.addNewBook(newBookDTO, coverImageFile, bookFile);
        return ResponseEntity.ok(id);
    }

    @PostMapping(value = "/books/{id}/paperBook",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE
            })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> addPaperBook(
            @PathVariable Long id,
            @RequestPart String newPaperBook,
            @RequestPart MultipartFile coverImageFile
    ) {
        NewPaperBookDTO newPaperBookDTO = adminService.getObjectFromJson(newPaperBook, NewPaperBookDTO.class);
        validator.validate(newPaperBookDTO);
        MessageResponse messageResponse = adminService.setPaperBook(id, newPaperBookDTO, coverImageFile);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping(value = "/books/{id}/ebook",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE
            })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> addEbook(
            @PathVariable Long id,
            @RequestPart String newEbook,
            @RequestPart MultipartFile coverImageFile,
            @RequestPart MultipartFile bookFile
    ) {
        NewEBookDTO newEbookDTO = adminService.getObjectFromJson(newEbook, NewEBookDTO.class);
        validator.validate(newEbookDTO);
        MessageResponse messageResponse = adminService.setEbook(id, newEbookDTO, coverImageFile, bookFile);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping(value = "/books/{id}/audiobook",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE
            })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> addAudioBook(
            @PathVariable Long id,
            @RequestPart String newAudiobook,
            @RequestPart MultipartFile coverImageFile,
            @RequestPart MultipartFile bookFile
    ) {
        NewAudioBookDTO newAudiobookDTO = adminService.getObjectFromJson(newAudiobook, NewAudioBookDTO.class);
        validator.validate(newAudiobookDTO);
        MessageResponse messageResponse = adminService.setAudiobook(id, newAudiobookDTO, coverImageFile, bookFile);
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping("/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateBook(
            @PathVariable Long id,
            @RequestBody @Valid UpdateBookDTO updateBookDTO
    ) {
        MessageResponse messageResponse = adminService.updateBook(id, updateBookDTO);
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping(value = "/books/{id}/paperBook",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE
            })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updatePaperBook(
            @PathVariable Long id,
            @RequestPart String newPaperBook,
            @RequestPart(required = false) MultipartFile coverImageFile
    ) {
        NewPaperBookDTO newPaperBookDTO = adminService.getObjectFromJson(newPaperBook, NewPaperBookDTO.class);
        validator.validate(newPaperBookDTO);
        MessageResponse messageResponse = adminService.setPaperBook(id, newPaperBookDTO, coverImageFile);
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping(value = "/books/{id}/ebook",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE
            })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateEbook(
            @PathVariable Long id,
            @RequestPart String newEbook,
            @RequestPart(required = false) MultipartFile coverImageFile,
            @RequestPart(required = false) MultipartFile bookFile
    ) {
        NewEBookDTO newEbookDTO = adminService.getObjectFromJson(newEbook, NewEBookDTO.class);
        validator.validate(newEbookDTO);
        MessageResponse messageResponse = adminService.setEbook(id, newEbookDTO, coverImageFile, bookFile);
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping(value = "/books/{id}/audiobook",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE
            })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateAudiobook(
            @PathVariable Long id,
            @RequestPart String newAudiobook,
            @RequestPart(required = false) MultipartFile coverImageFile,
            @RequestPart(required = false) MultipartFile bookFile
    ) {
        NewAudioBookDTO newAudiobookDTO = adminService.getObjectFromJson(newAudiobook, NewAudioBookDTO.class);
        validator.validate(newAudiobookDTO);
        MessageResponse messageResponse = adminService.setAudiobook(id, newAudiobookDTO, coverImageFile, bookFile);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/promoCodes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> addPromoCode(
            @RequestBody @Valid NewPromoCodeDTO newPromoCodeDTO
    ) {
        MessageResponse messageResponse = adminService.addPromoCode(newPromoCodeDTO);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/promoCodes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePromoCode(
            @RequestParam String code
    ) {
        MessageResponse messageResponse = adminService.deletePromoCode(code);
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/reviews")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableResponse> getReviews(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "username", required = false) String username
    ) {
        PageableResponse reviews = adminService.getReviews(pageNumber, pageSize, username);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableResponse> getOrders(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "id", required = false) Long id
    ) {
        PageableResponse orders = adminService.getOrders(pageNumber, pageSize, id);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/orders/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        MessageResponse messageResponse = adminService.updateOrderStatus(id, status);
        return ResponseEntity.ok(messageResponse);
    }
}
