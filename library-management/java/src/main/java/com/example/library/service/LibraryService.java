package com.example.library.service;

import com.example.library.exception.InvalidLoanException;
import com.example.library.exception.LoanLimitExceededException;
import com.example.library.exception.NoAvailableCopyException;
import com.example.library.model.Book;
import com.example.library.model.BookItem;
import com.example.library.model.Loan;
import com.example.library.model.Member;
import com.example.library.strategy.FineStrategy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The application service (aggregate root) that ties catalog, loans, and fine strategy together.
 *
 * <p><b>Design:</b> depends only on {@link FineStrategy}, so fine calculation is swappable. A
 * {@link Clock} is injected so due-date and overdue-fine tests are deterministic.
 *
 * <p><b>Concurrency:</b> checkout is synchronized around member-limit counting and loan creation;
 * the physical copy is still the critical boundary because {@link BookItem#tryCheckout()} performs
 * the AVAILABLE -> LOANED transition atomically. Active loans live in a ConcurrentHashMap and
 * {@code remove} makes return idempotency safe.
 */
public class LibraryService {

    private final List<Book> catalog;
    private final Duration loanPeriod;
    private final FineStrategy fineStrategy;
    private final Clock clock;

    private final ConcurrentMap<String, Loan> activeLoans = new ConcurrentHashMap<>();

    public LibraryService(List<Book> catalog,
                          Duration loanPeriod,
                          FineStrategy fineStrategy,
                          Clock clock) {
        this.catalog = List.copyOf(catalog);
        this.loanPeriod = loanPeriod;
        this.fineStrategy = fineStrategy;
        this.clock = clock;
    }

    /** Case-insensitive substring search across title and author. */
    public List<Book> search(String query) {
        String q = query.toLowerCase(Locale.ROOT);
        List<Book> result = new ArrayList<>();
        for (Book book : catalog) {
            if (book.getTitle().toLowerCase(Locale.ROOT).contains(q)
                    || book.getAuthor().toLowerCase(Locale.ROOT).contains(q)) {
                result.add(book);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Checkout flow: enforce member limit, atomically claim one available copy, create a Loan.
     *
     * @throws LoanLimitExceededException if the member is already at max active loans.
     * @throws NoAvailableCopyException if no copy of this book can be claimed.
     */
    public synchronized Loan checkout(Member member, Book book) {
        long memberLoans = activeLoans.values().stream()
                .filter(loan -> loan.getMember().getId().equals(member.getId()))
                .count();
        if (memberLoans >= member.getMaxConcurrentLoans()) {
            throw new LoanLimitExceededException(member.getName() + " reached the loan limit");
        }

        for (BookItem item : book.getItems()) {
            if (item.tryCheckout()) {
                Instant checkoutTime = clock.instant();
                Loan loan = new Loan(member, item, checkoutTime, checkoutTime.plus(loanPeriod));
                activeLoans.put(loan.getId(), loan);
                return loan;
            }
        }
        throw new NoAvailableCopyException("No available copy for " + book.getTitle());
    }

    /** Return a copy, close the loan, compute fine, and make the copy available again. */
    public ReturnReceipt returnLoan(String loanId) {
        Loan loan = activeLoans.remove(loanId);
        if (loan == null) {
            throw new InvalidLoanException("Unknown or already-returned loan: " + loanId);
        }
        Instant returnTime = clock.instant();
        long fine = fineStrategy.calculateFine(loan, returnTime);
        loan.close(returnTime);
        loan.getItem().markAvailable();
        return new ReturnReceipt(loan, returnTime, fine);
    }

    public int activeLoanCount(Member member) {
        return (int) activeLoans.values().stream()
                .filter(loan -> loan.getMember().getId().equals(member.getId()))
                .count();
    }

    public List<Book> getCatalog() {
        return catalog;
    }
}
