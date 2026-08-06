package com.example.vending;

import com.example.vending.exception.InsufficientFundsException;
import com.example.vending.exception.OutOfStockException;
import com.example.vending.model.InventoryItem;
import com.example.vending.model.MachineStateName;
import com.example.vending.model.Product;
import com.example.vending.model.PurchaseResult;
import com.example.vending.model.RefundResult;
import com.example.vending.service.VendingMachine;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VendingMachineTest {

    private VendingMachine newMachine() {
        return new VendingMachine(
                List.of(
                        new InventoryItem(new Product("WATER", "Water", 25), 2),
                        new InventoryItem(new Product("CHIPS", "Chips", 15), 1),
                        new InventoryItem(new Product("CANDY", "Candy", 10), 0)),
                Set.of(1, 5, 10, 25));
    }

    @Test
    void exactChangeDispensesProductAndReturnsToIdle() {
        VendingMachine machine = newMachine();
        machine.insertMoney(25);
        PurchaseResult result = machine.selectProduct("WATER");
        assertEquals("WATER", result.product().getCode());
        assertEquals(List.of(), result.change());
        assertEquals(1, machine.stockOf("WATER"));
        assertEquals(MachineStateName.IDLE, machine.currentState());
    }

    @Test
    void overpaymentReturnsGreedyChange() {
        VendingMachine machine = newMachine();
        machine.insertMoney(25);
        PurchaseResult result = machine.selectProduct("CHIPS");
        assertEquals(List.of(10), result.change());
        assertEquals(0, machine.stockOf("CHIPS"));
    }

    @Test
    void selectingWithoutEnoughMoneyIsRejectedAndStaysHasMoney() {
        VendingMachine machine = newMachine();
        machine.insertMoney(10);
        assertThrows(InsufficientFundsException.class, () -> machine.selectProduct("WATER"));
        assertEquals(10, machine.getBalance());
        assertEquals(MachineStateName.HAS_MONEY, machine.currentState());
    }

    @Test
    void outOfStockProductIsRejectedAndMoneyIsPreserved() {
        VendingMachine machine = newMachine();
        machine.insertMoney(10);
        assertThrows(OutOfStockException.class, () -> machine.selectProduct("CANDY"));
        assertEquals(0, machine.stockOf("CANDY"));
        assertEquals(10, machine.getBalance());
        assertEquals(MachineStateName.HAS_MONEY, machine.currentState());
    }

    @Test
    void cancelRefundsFullInsertedAmountAndReturnsToIdle() {
        VendingMachine machine = newMachine();
        machine.insertMoney(25);
        machine.insertMoney(10);
        RefundResult refund = machine.cancel();
        assertEquals(List.of(25, 10), refund.coins());
        assertEquals(35, refund.amount());
        assertEquals(0, machine.getBalance());
        assertEquals(MachineStateName.IDLE, machine.currentState());
    }

    @Test
    void concurrentBuyersCannotOversellLastUnit() throws InterruptedException {
        int threads = 50;
        VendingMachine machine = new VendingMachine(
                List.of(new InventoryItem(new Product("WATER", "Water", 25), 1)),
                Set.of(1, 5, 10, 25));

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        ConcurrentLinkedQueue<String> products = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    PurchaseResult result = machine.purchase("WATER", List.of(25));
                    successes.incrementAndGet();
                    products.add(result.product().getCode());
                } catch (OutOfStockException ignored) {
                    // expected for all losing buyers
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(1, successes.get(), "only one buyer can get the last unit");
        assertEquals(1, products.size());
        assertEquals(0, machine.stockOf("WATER"));
    }
}
