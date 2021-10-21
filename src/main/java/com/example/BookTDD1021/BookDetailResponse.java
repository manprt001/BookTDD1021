package com.example.BookTDD1021;

public class BookDetailResponse {
    private int status;
    private String message;
    private Book data;

    public int getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public Book getData() {
        return this.data;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setData(final Book data) {
        this.data = data;
    }

    public BookDetailResponse() {
    }

    public BookDetailResponse(final int status, final String message, final Book data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}