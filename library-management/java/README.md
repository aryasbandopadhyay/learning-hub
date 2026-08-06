# Library Management — LLD Machine Coding (Java)

An end-to-end MVP of a library management system, built for an SDE2 machine-coding round. It
demonstrates OOP entity modelling, the **Strategy** pattern, and **thread-safe** checkout with no
double-loaning of the same copy.

> A parallel Python implementation lives in `../python` with its own README. Both produce similar
> demo output.

---

## 1. Why this MVP?

A machine-coding interviewer looks for clean OOP, a useful design pattern, correct concurrency, and
working tests — delivered quickly. This MVP is the **smallest system that still exercises all of
those**:

**In scope**
- Catalog title (`Book`) and multiple physical copies (`BookItem`) per title
- `search` by title or author substring (case-insensitive)
- `checkout` → claim an available copy → create `Loan` with due date
- Member max concurrent-loans enforcement
- `returnLoan` → free copy → compute overdue fine
- Pluggable **FineStrategy** and injected **Clock**
- Thread-safe concurrent checkout of the same copy

**Deliberately out of scope** (extension points): reservations/holds queue, multiple branches,
payments, notifications, persistence/DB, REST/UI layer. See *Extending* below.

---

## 2. UML

### Class diagram
```mermaid
classDiagram
    class Book {
      +String isbn
      +String title
      +String author
      +addItem(BookItem)
    }
    class BookItem {
      +String barcode
      +BookItemStatus status
      +tryCheckout() bool  «synchronized»
      +markAvailable() «synchronized»
    }
    class BookItemStatus {
      <<enum>>
      AVAILABLE
      LOANED
    }
    class Member {
      +String id
      +String name
      +int maxConcurrentLoans
    }
    class Loan {
      +String id
      +Instant checkoutTime
      +Instant dueTime
      +Instant returnTime
      +close(returnTime)
    }
    class LibraryService {
      +search(query) List~Book~
      +checkout(Member, Book) Loan
      +returnLoan(id) ReturnReceipt
      +activeLoanCount(Member) int
    }
    class ReturnReceipt
    class FineStrategy {
      <<interface>>
      +calculateFine(Loan, Instant) long
    }
    class PerDayFineStrategy

    Book "1" o-- "1..*" BookItem
    Member "1" --> "0..*" Loan
    Loan --> BookItem
    Loan --> Member
    BookItem --> BookItemStatus
    BookItem --> Book
    LibraryService --> FineStrategy
    FineStrategy <|.. PerDayFineStrategy
```

### Checkout sequence
```mermaid
sequenceDiagram
    participant C as Client
    participant L as LibraryService
    participant I as BookItem
    C->>L: checkout(member, book)
    L->>L: count active loans for member
    alt member at limit
        L-->>C: throw LoanLimitExceededException
    else below limit
        loop each copy until one claimed
            L->>I: tryCheckout() «synchronized»
            I-->>L: true / false
        end
        alt copy found
            L->>L: new Loan(member, item, now, now + loanPeriod)
            L-->>C: Loan
        else no copy
            L-->>C: throw NoAvailableCopyException
        end
    end
```

---

## 3. Design decisions

| Decision | Reasoning |
| --- | --- |
| **Separate `Book` from `BookItem`** | Users search a title, but lending happens on a physical copy/barcode. This models real libraries and the 1..* relationship. |
| **`Loan` as join entity** | Captures Member ↔ BookItem over time, with due/return timestamps. |
| **Strategy for fines** | Daily fines, grace periods, or premium-member waivers can change without editing `LibraryService`. |
| **Injected `Clock`** | Due date and overdue fine tests are deterministic — advance a fake clock instead of sleeping. |
| **Concurrency at the copy** | `BookItem.tryCheckout` synchronizes AVAILABLE→LOANED so two members cannot borrow the same barcode. |
| **Service lock for member limit** | Counting active loans and inserting a new loan happen together, preventing a member-limit race. |
| **In-memory catalog/loans** | Keeps the MVP focused on LLD; repositories/DB are clean follow-ups. |

### Concurrency model (the key part)
`BookItem.tryCheckout` is `synchronized`, so the check *“is this copy available?”* and the state
change are a single atomic step. The service scans copies and calls `tryCheckout`; when 50 threads
race for one copy, exactly one wins and no barcode is loaned twice — asserted in
`concurrentCheckoutNeverDoubleLoansSingleCopy`.

---

## 4. Code flow

```
Main → LibraryService.search
Main → LibraryService.checkout(member, book)
        → enforce member limit → BookItem.tryCheckout (atomic)
        → new Loan(now, due=now+period) → store in active-loans map
LibraryService.returnLoan → remove active loan → FineStrategy.calculateFine
        → loan.close → BookItem.markAvailable → ReturnReceipt
```

Package layout:
```
com.example.library
├── model/      Book, BookItem, BookItemStatus, Member, Loan
├── strategy/   FineStrategy + PerDayFineStrategy
├── service/    LibraryService, ReturnReceipt
├── exception/  NoAvailableCopyException, LoanLimitExceededException, InvalidLoanException
└── Main.java   runnable demo
```

---

## 5. How to run

Prerequisites: JDK 17+ and Maven.

```powershell
cd java

# run the test suite (5 tests incl. the concurrency race test)
mvn test

# run the demo
mvn -q compile exec:java "-Dexec.mainClass=com.example.library.Main"
```

Expected demo output:
```
Catalog size: 2
Search 'code': 1 book(s)
Checked out BC-001 to Asha
Due in days: 14
Returned BC-001, fine = 0
Active loans for member: 0
```

---

## 6. Tests

`LibraryServiceTest` covers:
- search by title and author
- checkout marks a copy LOANED, creates a Loan with correct due date, and rejects no-copy checkout
- member max concurrent-loans limit
- return frees the copy and computes a 3-days-late fine using a **mutable injected clock**
- **concurrency**: 50 threads race for 1 copy → exactly 1 succeeds

---

## 7. Extending (what a follow-up would add)
- **Reservations/holds**: wait queue per Book when all copies are loaned.
- **Multiple branches**: Branch owns its copy inventory; search can aggregate branch availability.
- **Payments**: a `PaymentProcessor` invoked for non-zero fines.
- **Notifications**: due-soon/overdue reminders.
- **Persistence**: swap in-memory collections for repository interfaces.
