package com.vb.bookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "books")
@ToString(exclude = "books")
@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = "categoryName")
})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private Set<Book> books;

    @NotBlank
    @Size(min = 5, max = 100)
    @Column(nullable = false)
    private String categoryName;
}
