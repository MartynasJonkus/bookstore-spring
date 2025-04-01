package com.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class BookstoreApp {
    public static void main(String[] args) {
        SpringApplication.run(BookstoreApp.class, args);
    }
}
