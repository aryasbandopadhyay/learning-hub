package com.example.library;

import com.example.library.model.Book;
import com.example.library.model.BookItem;
import com.example.library.model.Loan;
import com.example.library.model.Member;
import com.example.library.service.LibraryService;
import com.example.library.service.ReturnReceipt;
import com.example.library.strategy.PerDayFineStrategy;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

/**
 * Runnable demo showing the end-to-end flow: build catalog, search, checkout, return, and print a
 * receipt. Run with {@code mvn -q compile exec:java -Dexec.mainClass=com.example.library.Main}.
 */
public class Main {

    public static void main(String[] args) {
        Book cleanCode = new Book("9780132350884", "Clean Code", "Robert C. Martin");
        cleanCode.addItem(new BookItem("BC-001", cleanCode));
        cleanCode.addItem(new BookItem("BC-002", cleanCode));
        Book designPatterns = new Book("9780201633610", "Design Patterns", "Erich Gamma");
        designPatterns.addItem(new BookItem("DP-001", designPatterns));

        LibraryService library = new LibraryService(
                List.of(cleanCode, designPatterns),
                Duration.ofDays(14),
                new PerDayFineStrategy(5),
                Clock.systemUTC());

        Member member = new Member("M1", "Asha", 2);

        System.out.println("Catalog size: " + library.getCatalog().size());
        System.out.println("Search 'code': " + library.search("code").size() + " book(s)");

        Loan loan = library.checkout(member, cleanCode);
        System.out.println("Checked out " + loan.getItem().getBarcode() + " to " + member.getName());
        System.out.println("Due in days: 14");

        ReturnReceipt receipt = library.returnLoan(loan.getId());
        System.out.println("Returned " + receipt.loan().getItem().getBarcode() + ", fine = " + receipt.fine());
        System.out.println("Active loans for member: " + library.activeLoanCount(member));
    }
}
