package com.vb.bookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "book")
@Entity
@Table(name = "paper_books")
public class PaperBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaperBookEnum coverType;

    @NotBlank
    @Size(min = 13, max = 13)
    @Column(nullable = false)
    private String isbn;

    @NotNull
    @Column(nullable = false)
    private Boolean isAvailable;

    @NotNull
    @Min(value = 1)
    @Max(value = 10000)
    @Column(nullable = false)
    private Integer numOfPages;

    @Override
    public String toString() {
        return "PaperBook{" +
                "id=" + id +
//                ", book=" + book +
                ", book_id=" + book.getId() +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", price=" + price +
                ", publisher='" + publisher + '\'' +
                ", coverType=" + coverType +
                ", isbn='" + isbn + '\'' +
                ", isAvailable=" + isAvailable +
                ", numOfPages=" + numOfPages +
                '}';
    }
}
