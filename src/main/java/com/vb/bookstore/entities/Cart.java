package com.vb.bookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<CartItem> cartItems;

    @NotNull
    private Boolean hasPaperBooks;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_method_id")
    private ShippingMethod shippingMethod;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "99999999.99")
    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @NotNull
    private Boolean hasPromoCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id")
    @ToString.Exclude
    private PromoCode promoCode;

    @DecimalMin(value = "0")
    @DecimalMax(value = "99999999.99")
    private BigDecimal totalPriceWithPromoCode;

    private String paymentStatus;

    private String paymentId;

    private String paymentRedirectUrl;

    public void setPromoCode(PromoCode promoCode) {
        setHasPromoCode(promoCode != null);
        this.promoCode = promoCode;
        updateTotalPrice();
    }

    public void addItem(CartItem cartItem) {
        cartItems.add(cartItem);
        updateTotalPrice();
        updateHasPaperBooks();
    }

    public void removeItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        updateTotalPrice();
        updateHasPaperBooks();
    }

    public void clear() {
        cartItems.clear();
        updateTotalPrice();
        updateHasPaperBooks();
    }

    public void updateHasPaperBooks() {
        setHasPaperBooks(cartItems.stream().anyMatch(cartItem -> cartItem.getBookType().equals("Paper book")));
    }

    public void updateTotalPrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : getCartItems()) {
            totalPrice = totalPrice.add(item.getTotalPrice());
        }
        setTotalPrice(totalPrice);
        if (hasPromoCode) {
            setTotalPriceWithPromoCode(totalPrice.multiply(BigDecimal.valueOf(1 - promoCode.getPercentage()/100.0)));
        }
        setPaymentId(null);
        setPaymentStatus(null);
        setPaymentRedirectUrl(null);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Cart cart = (Cart) o;
        return getId() != null && Objects.equals(getId(), cart.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
