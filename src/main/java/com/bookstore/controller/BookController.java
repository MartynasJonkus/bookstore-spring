package com.bookstore.controller;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.Publisher;
import com.bookstore.service.AuthorService;
import com.bookstore.service.BookService;
import com.bookstore.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public String saveBook(@ModelAttribute Book book,
                           @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
                           @RequestParam(value = "publisherId", required = false) Long publisherId) {

        Set<Author> selectedAuthors = new HashSet<>();
        if (authorIds != null && !authorIds.isEmpty()) {
            for (Long authorId : authorIds) {
                authorService.findAuthorById(authorId).ifPresent(selectedAuthors::add);
            }
        }
        System.out.println(selectedAuthors);

        if (book.getId() != null) {
            Book existingBook = bookService.findBookById(book.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid book ID: " + book.getId()));

            existingBook.setTitle(book.getTitle());
            existingBook.setDescription(book.getDescription());
            existingBook.setIsbn(book.getIsbn());
            existingBook.setPublicationYear(book.getPublicationYear());

            if (publisherId != null) {
                existingBook.setPublisher(publisherService.findPublisherById(publisherId));
            } else {
                existingBook.setPublisher(null);
            }

            existingBook.getAuthorsReference().clear();
            existingBook.getAuthorsReference().addAll(selectedAuthors);

            bookService.saveBook(existingBook);
        } else {
            book.setAuthors(selectedAuthors);

            if (publisherId != null) {
                book.setPublisher(publisherService.findPublisherById(publisherId));
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
