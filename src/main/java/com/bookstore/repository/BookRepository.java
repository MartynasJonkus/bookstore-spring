package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph(attributePaths = {"authors", "publisher"})
    @Query("SELECT b FROM Book b")
    List<Book> findAllWithAuthorsAndPublisher();

    @EntityGraph(attributePaths = {"authors", "publisher"})
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdWithAuthorsAndPublisher(Long id);
}
