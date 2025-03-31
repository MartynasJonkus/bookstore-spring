package com.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @ManyToMany
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(
                    name = "book_id",
                    foreignKey = @ForeignKey(
                            foreignKeyDefinition = "FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "author_id",
                    foreignKey = @ForeignKey(
                            foreignKeyDefinition = "FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE"
                    )
            )
    )
    private Set<Author> authors = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "publisher_id",
            foreignKey = @ForeignKey(
                    foreignKeyDefinition = "FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE SET NULL"
            )
    )
    private Publisher publisher;
}
