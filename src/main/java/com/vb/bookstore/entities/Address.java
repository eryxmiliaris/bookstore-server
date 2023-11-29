package com.vb.bookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9\\s'-]+$", message = "Invalid address name")
    private String name;

    @NotBlank
    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[A-Z][a-z]*([- ][A-Z][a-z]*)*$", message = "Invalid name")
    private String userName;

    @NotBlank
    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[A-Z][a-z]*([- ][A-Z][a-z]*)*$", message = "Invalid surname")
    private String userSurname;

    @NotBlank
    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[A-Z][a-z]*([- ][A-Z][a-z]*)*$", message = "Invalid city name")
    private String city;

    @NotBlank
    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[A-Z][a-z]*(?:[ -][A-Z][a-z]*)*\\s\\d+[A-Za-z]*$", message = "Invalid street address")
    private String street;

    @NotBlank
    @Pattern(regexp = "^[0-9]{2}-[0-9]{3}$", message = "Invalid postal code (valid example: 20-501)")
    private String postalCode;

    @NotBlank
    @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$", message = "Invalid phone number")
    private String phoneNumber;


    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @OneToOne(mappedBy = "address")
    @ToString.Exclude
    private Cart cart;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Address address = (Address) o;
        return getId() != null && Objects.equals(getId(), address.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
