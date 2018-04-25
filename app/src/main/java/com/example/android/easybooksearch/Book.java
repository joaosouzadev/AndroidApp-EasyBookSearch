package com.example.android.easybooksearch;

/**
 * Created by JOAO on 24-Apr-18.
 */

public class Book {

    // Global variables for book's title and author
    private String title;
    private String author;
    private String imageUrl;

    public Book() {
    }

    public Book(String title, String author, String imageUrl) {
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
    }

    // getters
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
