package com.example.BookTDD1021;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BookRepository extends CrudRepository<com.example.BookTDD1021.Book, Integer> {
    Optional<com.example.BookTDD1021.Book> findByTitle(String title);
    Optional<com.example.BookTDD1021.Book> findByAuthor(String author);



}
