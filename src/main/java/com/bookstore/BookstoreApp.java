package com.bookstore;

import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.repository.AuthorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;

@SpringBootApplication
public class BookstoreApp {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApp.class, args);

    }

    @Bean
    CommandLineRunner init(AuthorRepository authorRepository) {
        return args -> {
            Author author = Author.builder()
                    .firstName("Petras")
                    .lastName("Smith")
                    .biography("Good author")
                    .build();
            authorRepository.save(author);
        };
    }

}
