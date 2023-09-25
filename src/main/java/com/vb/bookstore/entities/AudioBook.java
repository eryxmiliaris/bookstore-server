package com.vb.bookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "book")
@ToString(exclude = "book")
@Entity
@Table(name = "audio_books")
public class AudioBook {
    @Id
    @Column(name = "book_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "book_id")
    private Book book;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String coverImageUrl;

    @NotNull
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "9999.99")
    @Column(name = "price", precision = 6, scale = 2, nullable = false)
    private BigDecimal price;

    @NotBlank
    @Size(min = 5, max = 100)
    @Column(nullable = false)
    private String publisher;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String downloadLink;

    @NotBlank
    @Size(min = 5, max = 100)
    @Column(nullable = false)
    private String narrator;

    @NotNull
    @Min(value = 60)
    @Max(value = 360000)
    @Column(nullable = false)
    private Integer duration_seconds;
}
