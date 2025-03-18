package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final PublisherService publisherService;

    @Autowired
    public BookService(BookRepository bookRepository, PublisherService publisherService) {
        this.bookRepository = bookRepository;
        this.publisherService = publisherService;
    }

    @Transactional(readOnly = true)
    public List<Book> findAllBooks() {
        List<Book> books = bookRepository.findAllWithAuthors();
        for (Book book : books) {
            if (book.getPublisherId() != null) {
                book.setPublisher(publisherService.findPublisherById(book.getPublisherId()).orElse(null));
            }
        }
        return books;
    }

    @Transactional(readOnly = true)
    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Transactional
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }
}
