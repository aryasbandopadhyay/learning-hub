package com.example.library.model;

/**
 * A physical copy of a Book. THIS CLASS IS THE CONCURRENCY BOUNDARY.
 *
 * <p>{@link #tryCheckout()} and {@link #markAvailable()} are {@code synchronized}, so the check
 * "is this copy available?" and the state change to LOANED happen as one atomic step. When many
 * members race for the only copy, exactly one thread flips AVAILABLE -> LOANED.
 */
public class BookItem {

    private final String barcode;
    private final Book book;

    // Guarded by 'this' monitor (synchronized methods).
    private BookItemStatus status = BookItemStatus.AVAILABLE;

    public BookItem(String barcode, Book book) {
        this.barcode = barcode;
        this.book = book;
    }

    /** Atomically claim this copy for checkout; return true only for the winning thread. */
    public synchronized boolean tryCheckout() {
        if (status != BookItemStatus.AVAILABLE) {
            return false;
        }
        status = BookItemStatus.LOANED;
        return true;
    }

    /** Atomically release the copy back to the shelf. */
    public synchronized void markAvailable() {
        status = BookItemStatus.AVAILABLE;
    }

    public synchronized BookItemStatus getStatus() {
        return status;
    }

    public String getBarcode() {
        return barcode;
    }

    public Book getBook() {
        return book;
    }
}
