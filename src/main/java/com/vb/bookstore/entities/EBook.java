package com.vb.bookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ebooks")
public class EBook {
    @Id
    @Column(name = "book_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "book_id")
    @ToString.Exclude
    private Book book;

    @NotNull
    @Column(nullable = false)
    private Boolean isHidden;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String coverImageUrl;

    @NotNull
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "9999.99")
    @Column(name = "price", precision = 6, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    private Boolean hasDiscount;

    @NotNull
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "9999.99")
    @Column(precision = 6, scale = 2, nullable = false)
    private BigDecimal priceWithDiscount;

    @Min(value = 0)
    @Max(value = 100)
    private Integer discountPercentage;

    @DecimalMin(value = "0.01")
    @DecimalMax(value = "9999.99")
    @Column(precision = 6, scale = 2)
    private BigDecimal discountAmount;

    private LocalDateTime discountEndDate;

    @NotBlank
    @Size(min = 5, max = 100)
    @Column(nullable = false)
    private String publisher;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String downloadLink;

    @NotNull
    @Min(value = 1)
    @Max(value = 10000)
    @Column(nullable = false)
    private Integer numOfPages;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        EBook eBook = (EBook) o;
        return getId() != null && Objects.equals(getId(), eBook.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
