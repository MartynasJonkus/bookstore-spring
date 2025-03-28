package com.bookstore.bean;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.Publisher;
import com.bookstore.service.AuthorService;
import com.bookstore.service.BookService;
import com.bookstore.service.PublisherService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Named
@ViewScoped
@Data
@NoArgsConstructor
public class BookBean implements Serializable {

    @Autowired
    private transient BookService bookService;

    @Autowired
    private transient AuthorService authorService;

    @Autowired
    private transient PublisherService publisherService;

    private List<Book> books;
    private Book book = new Book();
    private Long editId;
    private List<Long> selectedAuthorIds = new ArrayList<>();
    private Long selectedPublisherId;

    @PostConstruct
    public void init() {
        books = bookService.findAllBooks();
    }

    public List<Author> getAllAuthors() {
        return authorService.findAllAuthors();
    }

    public List<Publisher> getAllPublishers() {
        return publisherService.findAllPublishers();
    }

    public String save() {
        Set<Author> authors = new HashSet<>();
        selectedAuthorIds.forEach(id ->
                authorService.findAuthorById(id).ifPresent(authors::add)
        );

        System.out.println("Trying to set publisher with id " + selectedPublisherId);
        Publisher publisher = null;
        if (selectedPublisherId != null) {
            publisher = publisherService.findPublisherById(selectedPublisherId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Publisher ID: " + selectedPublisherId));
        }

        if(editId != null) {
            Book existing = bookService.findBookById(editId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid book ID"));

            existing.setTitle(book.getTitle());
            existing.setDescription(book.getDescription());
            existing.setIsbn(book.getIsbn());
            existing.setPublicationYear(book.getPublicationYear());
            existing.setAuthors(authors);
            existing.setPublisherId(selectedPublisherId);
            existing.setPublisher(publisher);

            bookService.saveBook(existing);
        } else {
            book.setAuthors(authors);
            book.setPublisherId(selectedPublisherId);
            book.setPublisher(publisher);
            bookService.saveBook(book);
        }

        return "/books.xhtml?faces-redirect=true";
    }

    public String delete(Long id) {
        bookService.deleteBookById(id);
        return "/books.xhtml?faces-redirect=true";
    }

    public void loadBook() {
        if(editId != null) {
            book = bookService.findBookById(editId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid book ID"));

            selectedAuthorIds = new ArrayList<>();
            book.getAuthors().forEach(author ->
                    selectedAuthorIds.add(author.getId())
            );

            selectedPublisherId = book.getPublisherId();

            if(selectedPublisherId != null) {
                publisherService.findPublisherById(selectedPublisherId)
                        .ifPresent(book::setPublisher);
            }
        }
    }
}