package com.vb.bookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PaperBook> paperBooks;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private AudioBook audioBook;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private EBook eBook;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "category_books",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;


    @NotBlank
    @Size(min = 5, max = 100)
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Size(min = 5, max = 100)
    @Column(nullable = false)
    private String author;

    @NotBlank
    @Size(min = 20)
    @Column(columnDefinition="TEXT", nullable = false)
    private String description;

    @NotNull
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "10.00")
    @Column(precision = 3, scale = 2, nullable = false)
    private BigDecimal rating;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(nullable = false)
    private Date publicationDate;

    @NotNull
    @Column(nullable = false)
    private Boolean hidden;
}
