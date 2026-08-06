package com.example.library.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bibliographic title-level entity: ISBN/title/author are shared by many physical copies.
 *
 * <p>Relationship: Book 1..* BookItem. In a real system Book may also carry publisher, subjects,
 * edition, etc.; those are omitted because lending behavior depends on copies, not metadata.
 */
public class Book {

    private final String isbn;
    private final String title;
    private final String author;
    private final List<BookItem> items = new ArrayList<>();

    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    /** Attach a physical copy to this catalog title. */
    public void addItem(BookItem item) {
        items.add(item);
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public List<BookItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public String toString() {
        return title + " by " + author + " (" + isbn + ")";
    }
}
