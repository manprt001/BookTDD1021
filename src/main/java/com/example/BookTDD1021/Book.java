package com.example.BookTDD1021;

import javax.persistence.*;

@Entity
@Table(
        name = "books"
)
public class Book {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO
    )
    @Column(
            name = "ID"
    )
    int id;
    @Column(
            name = "TITLE"
    )
    String title;
    @Column(
            name = "AUTHOR"
    )
    String author;

    @Column(
            name = "RATING"
    )
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public Book() {
    }
}

