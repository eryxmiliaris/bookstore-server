package com.vb.bookstore.controllers;

import com.vb.bookstore.entities.Category;
import com.vb.bookstore.payloads.books.CategoryDTO;
import com.vb.bookstore.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOS = categories.stream().map(
                category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
        return ResponseEntity.ok(categoryDTOS);
    }
}
