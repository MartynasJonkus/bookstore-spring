package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.model.Publisher;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;

    @Autowired
    public PublisherService(PublisherRepository publisherRepository, BookRepository bookRepository) {
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
    }

    public List<Publisher> findAllPublishers() {
        return publisherRepository.findAll();
    }

    public Optional<Publisher> findPublisherById(Long id) {
        return publisherRepository.findById(id);
    }

    @Transactional
    public Publisher savePublisher(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    @Transactional
    public void deletePublisherById(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid publisher ID: " + id));

        if (publisher.getBooks() != null) {
            for (Book book : publisher.getBooks()) {
                book.setPublisher(null);
                bookRepository.save(book);
            }
        }

        publisherRepository.deleteById(id);
    }
}
