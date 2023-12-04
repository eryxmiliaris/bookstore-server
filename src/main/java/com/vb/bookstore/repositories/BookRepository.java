package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByPaperBooks_IsHiddenFalseOrAudiobook_IsHiddenFalseOrEbook_IsHiddenFalse();

    @Query(value = """
        SELECT DISTINCT b.id, b.title, b.author, b.description, b.rating, b.num_of_reviews, b.publication_date 
            FROM books b
            LEFT JOIN category_books cb ON b.id = cb.book_id
            LEFT JOIN categories c ON cb.category_id = c.id
            LEFT JOIN paper_books pb ON b.id = pb.book_id
            LEFT JOIN audiobooks ab ON b.id = ab.book_id
            LEFT JOIN ebooks eb ON b.id = eb.book_id
            WHERE
                (:title IS NULL OR b.title LIKE CONCAT('%', :title, '%'))
                AND ((:hasCategories=FALSE OR :hasCategories IS NULL) OR c.category_name IN (:categories))
                AND (
                    (pb.price_with_discount BETWEEN :priceStartPB AND :priceEndPB AND (:includeHidden=true OR NOT pb.is_hidden))
                    OR (eb.price_with_discount BETWEEN :priceStartEB AND :priceEndEB AND (:includeHidden=true OR NOT eb.is_hidden))
                    OR (ab.price_with_discount BETWEEN :priceStartAB AND :priceEndAB AND (:includeHidden=true OR NOT ab.is_hidden))
                )
        """
            ,countQuery = """
        SELECT COUNT(DISTINCT b.id)
            FROM books b
            LEFT JOIN category_books cb ON b.id = cb.book_id
            LEFT JOIN categories c ON cb.category_id = c.id
            LEFT JOIN paper_books pb ON b.id = pb.book_id
            LEFT JOIN audiobooks ab ON b.id = ab.book_id
            LEFT JOIN ebooks eb ON b.id = eb.book_id
            WHERE
                (:title IS NULL OR b.title LIKE CONCAT('%', :title, '%'))
                AND ((:hasCategories=FALSE OR :hasCategories IS NULL) OR c.category_name IN (:categories))
                AND (
                    (pb.price_with_discount BETWEEN :priceStartPB AND :priceEndPB AND (:includeHidden=true OR NOT pb.is_hidden))
                    OR (eb.price_with_discount BETWEEN :priceStartEB AND :priceEndEB AND (:includeHidden=true OR NOT eb.is_hidden))
                    OR (ab.price_with_discount BETWEEN :priceStartAB AND :priceEndAB AND (:includeHidden=true OR NOT ab.is_hidden))
                )
        """
            ,nativeQuery = true)
    Page<Book> findByFilterParams(
            String title,
            Boolean hasCategories,
            List<String> categories,
            Double priceStartPB, Double priceEndPB,
            Double priceStartEB, Double priceEndEB,
            Double priceStartAB, Double priceEndAB,
            Boolean includeHidden,
            Pageable pageable
    );
}
