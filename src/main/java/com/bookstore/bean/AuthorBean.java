package com.bookstore.bean;

import com.bookstore.model.Author;
import com.bookstore.service.AuthorService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
@Data
@NoArgsConstructor
public class AuthorBean implements Serializable {

    @Autowired
    private transient AuthorService authorService;

    private List<Author> authors;
    private Author author = new Author();
    private Long editId;

    @PostConstruct
    public void init() {
        authors = authorService.findAllAuthors();
    }

    public String save() {
        authorService.saveAuthor(author);
        return "/authors.xhtml?faces-redirect=true";
    }

    public String delete(Long id) {
        authorService.deleteAuthorById(id);
        return "/authors.xhtml?faces-redirect=true";
    }

    public void loadAuthor() {
        if(editId != null) {
            author = authorService.findAuthorById(editId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid author ID"));
        }
    }
}