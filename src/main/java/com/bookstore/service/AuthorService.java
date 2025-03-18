package com.bookstore.service;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.repository.AuthorRepository;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Author> findAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    @Transactional
    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    public void deleteAuthorById(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid author ID: " + authorId));

        for (Book book : author.getBooks()) {
            book.getAuthors().remove(author);
            bookRepository.save(book);
        }

        authorRepository.deleteById(authorId);
    }
}
