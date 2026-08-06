package com.example.library;

import com.example.library.exception.LoanLimitExceededException;
import com.example.library.exception.NoAvailableCopyException;
import com.example.library.model.Book;
import com.example.library.model.BookItem;
import com.example.library.model.BookItemStatus;
import com.example.library.model.Loan;
import com.example.library.model.Member;
import com.example.library.service.LibraryService;
import com.example.library.service.ReturnReceipt;
import com.example.library.strategy.PerDayFineStrategy;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A mutable clock we can advance by hand, so due-date/fine tests are deterministic (no sleeps).
 */
class MutableClock extends Clock {
    private Instant now;

    MutableClock(Instant start) {
        this.now = start;
    }

    void advance(Duration d) {
        now = now.plus(d);
    }

    @Override
    public Instant instant() {
        return now;
    }

    @Override
    public ZoneOffset getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(java.time.ZoneId zone) {
        return this;
    }
}

class LibraryServiceTest {

    private Book book(String isbn, String title, String author, String... barcodes) {
        Book book = new Book(isbn, title, author);
        for (String barcode : barcodes) {
            book.addItem(new BookItem(barcode, book));
        }
        return book;
    }

    private LibraryService newLibrary(Clock clock, Book... books) {
        return new LibraryService(
                List.of(books),
                Duration.ofDays(14),
                new PerDayFineStrategy(5),
                clock);
    }

    @Test
    void searchByTitleAndAuthorReturnsExpectedBooks() {
        Book cleanCode = book("9780132350884", "Clean Code", "Robert C. Martin", "BC-001");
        Book domainDriven = book("9780321125217", "Domain-Driven Design", "Eric Evans", "DD-001");
        LibraryService library = newLibrary(Clock.systemUTC(), cleanCode, domainDriven);

        assertEquals(List.of(cleanCode), library.search("code"));
        assertEquals(List.of(domainDriven), library.search("evans"));
    }

    @Test
    void checkoutMarksCopyLoanedCreatesLoanAndFailsWhenLastCopyGone() {
        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T10:00:00Z"));
        Book cleanCode = book("9780132350884", "Clean Code", "Robert C. Martin", "BC-001");
        LibraryService library = newLibrary(clock, cleanCode);
        Member asha = new Member("M1", "Asha", 2);
        Member ben = new Member("M2", "Ben", 2);

        Loan loan = library.checkout(asha, cleanCode);

        assertNotNull(loan.getId());
        assertEquals(BookItemStatus.LOANED, loan.getItem().getStatus());
        assertEquals(Instant.parse("2024-01-15T10:00:00Z"), loan.getDueTime());
        assertThrows(NoAvailableCopyException.class, () -> library.checkout(ben, cleanCode));
    }

    @Test
    void perMemberLimitIsEnforced() {
        Book cleanCode = book("9780132350884", "Clean Code", "Robert C. Martin", "BC-001");
        Book refactoring = book("9780201485677", "Refactoring", "Martin Fowler", "RF-001");
        LibraryService library = newLibrary(Clock.systemUTC(), cleanCode, refactoring);
        Member asha = new Member("M1", "Asha", 1);

        library.checkout(asha, cleanCode);

        assertThrows(LoanLimitExceededException.class, () -> library.checkout(asha, refactoring));
    }

    @Test
    void returnFreesCopyAndComputesOverdueFine() {
        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T10:00:00Z"));
        Book cleanCode = book("9780132350884", "Clean Code", "Robert C. Martin", "BC-001");
        LibraryService library = newLibrary(clock, cleanCode);
        Member asha = new Member("M1", "Asha", 2);

        Loan loan = library.checkout(asha, cleanCode);
        clock.advance(Duration.ofDays(17)); // 14-day loan period + 3 days late

        ReturnReceipt receipt = library.returnLoan(loan.getId());
        assertEquals(3 * 5L, receipt.fine());
        assertEquals(BookItemStatus.AVAILABLE, loan.getItem().getStatus());
        assertEquals(0, library.activeLoanCount(asha));
    }

    /**
     * Concurrency test: many members race to borrow one physical copy. Exactly one Loan may be
     * created, proving BookItem.tryCheckout performs the availability check atomically.
     */
    @Test
    void concurrentCheckoutNeverDoubleLoansSingleCopy() throws InterruptedException {
        int threads = 50;
        Book cleanCode = book("9780132350884", "Clean Code", "Robert C. Martin", "BC-001");
        LibraryService library = newLibrary(Clock.systemUTC(), cleanCode);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        ConcurrentLinkedQueue<String> loanedBarcodes = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threads; i++) {
            final int id = i;
            pool.submit(() -> {
                try {
                    start.await(); // release all threads at once to maximize contention
                    Loan loan = library.checkout(new Member("M" + id, "Member " + id, 1), cleanCode);
                    successes.incrementAndGet();
                    loanedBarcodes.add(loan.getItem().getBarcode());
                } catch (NoAvailableCopyException ignored) {
                    // expected for the losers
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(1, successes.get(), "exactly one member should borrow the single copy");
        assertEquals(1, loanedBarcodes.stream().distinct().count(), "the copy barcode is unique");
        assertEquals(BookItemStatus.LOANED, cleanCode.getItems().get(0).getStatus());
    }
}
