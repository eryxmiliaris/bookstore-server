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
@Table(name = "audiobooks")
public class Audiobook {
    @Id
    @Column(name = "book_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "book_id")
    @ToString.Exclude
    private Book book;

    @NotNull
    private Boolean isHidden;

    @NotBlank
    @Size(max = 255)
    private String coverImagePath;

    @NotNull
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "9999.99")
    @Column(name = "price", precision = 6, scale = 2)
    private BigDecimal price;

    @NotNull
    private Boolean hasDiscount;

    @DecimalMin(value = "0.01")
    @DecimalMax(value = "9999.99")
    @Column(precision = 6, scale = 2)
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
    private String publisher;

    @NotBlank
    @Size(max = 255)
    private String bookPath;

    @NotBlank
    private String previewPath;

    @NotBlank
    @Size(min = 5, max = 100)
    private String narrator;

    @NotNull
    @Min(value = 60)
    @Max(value = 360000)
    private Integer durationSeconds;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Audiobook audiobook = (Audiobook) o;
        return getId() != null && Objects.equals(getId(), audiobook.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
