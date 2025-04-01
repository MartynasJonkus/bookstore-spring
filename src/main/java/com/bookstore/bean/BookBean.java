package com.bookstore.bean;

import com.bookstore.aspect.TrackPerformance;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Getter @Setter private String errorMessage;
    @Getter @Setter private boolean conflictDetected = false;
    private Book conflictingBook;
    @Getter private List<Long> conflictingAuthorIds = new ArrayList<>();
    @Getter private Long conflictingPublisherId;
    private Book userEditedBook;
    @Getter private List<Long> userSelectedAuthorIds = new ArrayList<>();
    @Getter private Long userSelectedPublisherId;

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

    public String getAuthorNames(List<Long> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) return "None";
        return authorIds.stream()
                .map(id -> authorService.findAuthorById(id))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .map(a -> a.getFirstName() + " " + a.getLastName())
                .collect(Collectors.joining(", "));
    }

    public String getPublisherName(Long publisherId) {
        if (publisherId == null) return "None";
        return publisherService.findPublisherById(publisherId)
                .map(Publisher::getName)
                .orElse("Unknown");
    }

    public boolean hasAuthorsDifference() {
        if (userSelectedAuthorIds.size() != conflictingAuthorIds.size()) return true;
        List<Long> sortedUser = new ArrayList<>(userSelectedAuthorIds);
        List<Long> sortedConflict = new ArrayList<>(conflictingAuthorIds);
        sortedUser.sort(Long::compare);
        sortedConflict.sort(Long::compare);
        return !sortedUser.equals(sortedConflict);
    }

    public boolean hasPublisherDifference() {
        if (userSelectedPublisherId == null && conflictingPublisherId == null) return false;
        if (userSelectedPublisherId == null || conflictingPublisherId == null) return true;
        return !userSelectedPublisherId.equals(conflictingPublisherId);
    }

    private Book cloneBook(Book original) {
        Book clone = new Book();
        clone.setId(original.getId());
        clone.setTitle(original.getTitle());
        clone.setDescription(original.getDescription());
        clone.setIsbn(original.getIsbn());
        clone.setPublicationYear(original.getPublicationYear());
        clone.setVersion(original.getVersion());
        return clone;
    }

    @TrackPerformance
    public String save() {
        try {
            Set<Author> authors = new HashSet<>();
            selectedAuthorIds.forEach(id ->
                    authorService.findAuthorById(id).ifPresent(authors::add)
            );

            Publisher publisher = (selectedPublisherId != null) ?
                    publisherService.findPublisherById(selectedPublisherId)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid Publisher ID: " + selectedPublisherId))
                    : null;

            book.setAuthors(authors);
            book.setPublisher(publisher);

            userEditedBook = cloneBook(book);
            userSelectedAuthorIds = new ArrayList<>(selectedAuthorIds);
            userSelectedPublisherId = selectedPublisherId;

            bookService.saveBook(book);

            clearConflictState();
            return "/books.xhtml?faces-redirect=true";
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockFailure();
            return null;
        }
    }

    private void handleOptimisticLockFailure() {
        conflictingBook = bookService.findBookById(book.getId()).orElse(null);
        if (conflictingBook != null) {
            conflictingAuthorIds = conflictingBook.getAuthors().stream()
                    .map(Author::getId)
                    .collect(Collectors.toList());
            conflictingPublisherId = conflictingBook.getPublisher() != null ?
                    conflictingBook.getPublisher().getId() : null;

            // Preserve user's version for display
            Book temp = book;
            book = userEditedBook;
            userEditedBook = temp;
        }
        conflictDetected = true;
        errorMessage = "Conflict detected! Please review changes.";
    }

    public String delete(Long id) {
        bookService.deleteBookById(id);
        return "/books.xhtml?faces-redirect=true";
    }

    @TrackPerformance
    public void loadBook() {
        if (editId != null && !conflictDetected) {
            book = bookService.findBookById(editId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid book ID"));

            selectedAuthorIds = book.getAuthors().stream()
                    .map(Author::getId)
                    .collect(Collectors.toList());
            selectedPublisherId = book.getPublisher() != null ?
                    book.getPublisher().getId() : null;
        }
    }

    public String resolveConflict(boolean useLatestVersion) {
        try {
            if (useLatestVersion) {
                // Adopt database version
                book = cloneBook(conflictingBook);
                selectedAuthorIds = new ArrayList<>(conflictingAuthorIds);
                selectedPublisherId = conflictingPublisherId;
            } else {
                // Keep user's changes: Update version and FORCE SAVE
                book.setVersion(conflictingBook.getVersion());

                // Re-process relationships to ensure consistency
                Set<Author> authors = new HashSet<>();
                userSelectedAuthorIds.forEach(id ->
                        authorService.findAuthorById(id).ifPresent(authors::add)
                );
                Publisher publisher = (userSelectedPublisherId != null) ?
                        publisherService.findPublisherById(userSelectedPublisherId).orElse(null)
                        : null;

                book.setAuthors(authors);
                book.setPublisher(publisher);

                // Explicitly save the user's resolved version
                bookService.saveBook(book);
            }

            clearConflictState();
            return "/books.xhtml?faces-redirect=true"; // Force refresh
        } catch (ObjectOptimisticLockingFailureException e) {
            errorMessage = "Conflict occurred again. Please try again.";
            return null;
        }
    }

    private void clearConflictState() {
        conflictDetected = false;
        errorMessage = null;
        conflictingBook = null;
        userEditedBook = null;
        conflictingAuthorIds.clear();
        conflictingPublisherId = null;
        userSelectedAuthorIds.clear();
        userSelectedPublisherId = null;
    }
}