package com.bookstore.service;

import com.bookstore.mappers.PublisherMapper;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final PublisherMapper publisherMapper;

    @Autowired
    public BookService(BookRepository bookRepository, PublisherMapper publisherMapper) {
        this.bookRepository = bookRepository;
        this.publisherMapper = publisherMapper;
    }

    public List<Book> findAllBooks() {
        List<Book> books = bookRepository.findAllWithAuthors();
        for (Book book : books) {
            if (book.getPublisherId() != null) {
                book.setPublisher(publisherMapper.findById(book.getPublisherId()));
            }
        }
        return books;
    }

    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }
}
