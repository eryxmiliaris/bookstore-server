package com.vb.bookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @NotNull
    private LocalDate orderDate;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "99999999.99")
    @Column(precision = 10, scale = 2)
    private BigDecimal cartPrice;

    @NotNull
    private Boolean hasPaperBooks;

    @DecimalMin(value = "0")
    @DecimalMax(value = "999.99")
    @Column(precision = 5, scale = 2)
    private BigDecimal shippingPrice;

    private LocalDate shippingDate;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "99999999.99")
    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @NotBlank
    private String paymentId;

    @NotBlank
    private String orderStatus;

    private String addressUserFullName;

    private String addressLocation;

    private String addressPhoneNumber;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Order order = (Order) o;
        return getId() != null && Objects.equals(getId(), order.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
