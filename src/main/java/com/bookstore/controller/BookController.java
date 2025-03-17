package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.AuthorService;
import com.bookstore.service.BookService;
import com.bookstore.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final PublisherService publisherService;

    @Autowired
    public BookController(BookService bookService, AuthorService authorService, PublisherService publisherService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.publisherService = publisherService;
    }

    @GetMapping
    public String listBooks(Model model) {
        List<Book> books = bookService.findAllBooks();
        model.addAttribute("books", books);
        return "books/list";
    }

    @GetMapping("/new")
    public String showBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAllAuthors());
        model.addAttribute("publishers", publisherService.findAllPublishers());
        return "books/form";
    }

    @PostMapping
    public String saveBook(@ModelAttribute Book book, @RequestParam("authorIds") List<Long> authorIds,
                           @RequestParam("publisherId") Long publisherId) {
        if (book.getId() != null) {
            Book existingBook = bookService.findBookById(book.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid book ID: " + book.getId()));

            existingBook.setTitle(book.getTitle());
            existingBook.setDescription(book.getDescription());
            existingBook.setIsbn(book.getIsbn());
            existingBook.setPublicationYear(book.getPublicationYear());

            existingBook.getAuthors().clear();
            if (authorIds != null && !authorIds.isEmpty()) {
                authorIds.forEach(authorId -> {
                    authorService.findAuthorById(authorId)
                            .ifPresent(existingBook.getAuthors()::add);
                });
            }

            if (publisherId != null) {
                publisherService.findPublisherById(publisherId)
                        .ifPresent(existingBook::setPublisher);
            } else {
                existingBook.setPublisher(null);
            }

            bookService.saveBook(existingBook);
        }
        else {
            if (authorIds != null && !authorIds.isEmpty()) {
                authorIds.forEach(authorId -> {
                    authorService.findAuthorById(authorId)
                            .ifPresent(book.getAuthors()::add);
                });
            }

            if (publisherId != null) {
                publisherService.findPublisherById(publisherId)
                        .ifPresent(book::setPublisher);
            }

            bookService.saveBook(book);
        }
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = bookService.findBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book ID: " + id));

        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAllAuthors());
        model.addAttribute("publishers", publisherService.findAllPublishers());
        return "books/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return "redirect:/books";
    }
}
