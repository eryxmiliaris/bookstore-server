package com.vb.bookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
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

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private Audiobook audiobook;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private Ebook ebook;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "category_books",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    List<Wishlist> wishlists;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    List<CartItem> cartItems;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<LibraryItem> libraries;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews;

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank
    @Size(min = 5, max = 100)
    private String author;

    @NotBlank
    @Size(min = 20)
    @Column(columnDefinition="TEXT")
    private String description;

    @NotNull
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "10.00")
    @Column(precision = 4, scale = 2)
    private BigDecimal rating;

    @NotNull
    private Integer numOfReviews;

    @NotNull
    private LocalDate publicationDate;

    private Double popularityScore;

    public void addPaperBook(PaperBook paperBook) {
        this.paperBooks.add(paperBook);
    }

    public void addReview(Review review) {
        this.rating = this.rating.multiply(BigDecimal.valueOf(numOfReviews)).add(review.getRating()).divide(BigDecimal.valueOf(numOfReviews + 1), RoundingMode.HALF_EVEN);
        this.numOfReviews = numOfReviews + 1;
        reviews.add(review);
    }

    public void deleteReview(Review review) {
        if (reviews.remove(review) && numOfReviews != 1) {
            this.rating = this.rating
                    .multiply(BigDecimal.valueOf(numOfReviews))
                    .subtract(review.getRating())
                    .divide(BigDecimal.valueOf(numOfReviews - 1), RoundingMode.HALF_EVEN);
            this.numOfReviews = numOfReviews - 1;
        } else {
            this.rating = BigDecimal.ZERO;
            this.numOfReviews = 0;
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Book book = (Book) o;
        return getId() != null && Objects.equals(getId(), book.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}
