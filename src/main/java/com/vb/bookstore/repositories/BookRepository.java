package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = """
            SELECT DISTINCT b.id, b.title, b.author, b.description, b.rating, b.publication_date, b.hidden
                FROM books b
                LEFT JOIN category_books cb ON b.id = cb.book_id
                LEFT JOIN categories c ON cb.category_id = c.category_id
                LEFT JOIN paper_books pb ON b.id = pb.book_id
                LEFT JOIN audio_books ab ON b.id = ab.book_id
                LEFT JOIN ebooks eb ON b.id = eb.book_id
                WHERE
                    (:title IS NULL OR b.title LIKE CONCAT('%', :title, '%'))
                    AND ((:hasCategories=FALSE OR :hasCategories IS NULL) OR c.category_name IN (:categories))
                    AND (pb.price BETWEEN :priceStartPB AND :priceEndPB
                        OR eb.price BETWEEN :priceStartEB AND :priceEndEB
                        OR ab.price BETWEEN :priceStartAB AND :priceEndAB)
            """
            ,countQuery = """
            SELECT COUNT(DISTINCT b.id)
                FROM books b
                LEFT JOIN category_books cb ON b.id = cb.book_id
                LEFT JOIN categories c ON cb.category_id = c.category_id
                LEFT JOIN paper_books pb ON b.id = pb.book_id
                LEFT JOIN audio_books ab ON b.id = ab.book_id
                LEFT JOIN ebooks eb ON b.id = eb.book_id
                WHERE
                    (:title IS NULL OR b.title LIKE CONCAT('%', :title, '%'))
                    AND ((:hasCategories=FALSE OR :hasCategories IS NULL) OR c.category_name IN (:categories))
                    AND (pb.price BETWEEN :priceStartPB AND :priceEndPB
                        OR eb.price BETWEEN :priceStartEB AND :priceEndEB
                        OR ab.price BETWEEN :priceStartAB AND :priceEndAB)
            """
    ,nativeQuery = true)
    Page<Book> findByFilterParams(
            String title,
            Boolean hasCategories,
            List<String> categories,
            Double priceStartPB, Double priceEndPB,
            Double priceStartEB, Double priceEndEB,
            Double priceStartAB, Double priceEndAB
            ,Pageable pageable
    );
}
