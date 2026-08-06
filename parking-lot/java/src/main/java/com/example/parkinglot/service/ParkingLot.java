package com.example.parkinglot.service;

import com.example.parkinglot.exception.InvalidTicketException;
import com.example.parkinglot.exception.NoAvailableSpotException;
import com.example.parkinglot.model.ParkingSpot;
import com.example.parkinglot.model.Ticket;
import com.example.parkinglot.model.Vehicle;
import com.example.parkinglot.strategy.FeeStrategy;
import com.example.parkinglot.strategy.SpotAssignmentStrategy;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The application service (aggregate root) that ties the model + strategies together.
 *
 * <p><b>Design:</b> depends only on the {@link FeeStrategy} and {@link SpotAssignmentStrategy}
 * abstractions (Dependency Inversion), so pricing and placement policy are swappable. A
 * {@link Clock} is injected so fee calculation is deterministic in tests.
 *
 * <p><b>Concurrency:</b> there is no coarse lot-wide lock. Reservation is delegated to
 * {@link SpotAssignmentStrategy}, which relies on each spot's atomic {@code tryOccupy}. Active
 * tickets live in a {@link ConcurrentHashMap}. This lets many threads park/unpark in parallel while
 * guaranteeing no spot is double-allocated.
 */
public class ParkingLot {

    private final List<Level> levels;
    private final SpotAssignmentStrategy assignmentStrategy;
    private final FeeStrategy feeStrategy;
    private final Clock clock;

    private final ConcurrentMap<String, Ticket> activeTickets = new ConcurrentHashMap<>();

    public ParkingLot(List<Level> levels,
                      SpotAssignmentStrategy assignmentStrategy,
                      FeeStrategy feeStrategy,
                      Clock clock) {
        this.levels = List.copyOf(levels);
        this.assignmentStrategy = assignmentStrategy;
        this.feeStrategy = feeStrategy;
        this.clock = clock;
    }

    /**
     * Park a vehicle: atomically reserve a compatible spot and issue a ticket.
     *
     * @throws NoAvailableSpotException if the lot is full for this vehicle size.
     */
    public Ticket park(Vehicle vehicle) {
        ParkingSpot spot = assignmentStrategy.assign(levels, vehicle)
                .orElseThrow(() -> new NoAvailableSpotException(
                        "No available spot for " + vehicle));
        Ticket ticket = new Ticket(vehicle, spot, clock.instant());
        activeTickets.put(ticket.getId(), ticket);
        return ticket;
    }

    /**
     * Unpark by ticket id: free the spot, compute the fee, and invalidate the ticket.
     *
     * <p>{@code remove} on the concurrent map is atomic, so if two threads present the same ticket
     * simultaneously, only one succeeds and frees the spot exactly once.
     *
     * @throws InvalidTicketException if the ticket is unknown or already used.
     */
    public Receipt unpark(String ticketId) {
        Ticket ticket = activeTickets.remove(ticketId);
        if (ticket == null) {
            throw new InvalidTicketException("Unknown or already-used ticket: " + ticketId);
        }
        var exitTime = clock.instant();
        long fee = feeStrategy.calculateFee(ticket, exitTime);
        ticket.getSpot().free();
        return new Receipt(ticket, exitTime, fee);
    }

    /** Total free spots across all levels (snapshot). */
    public long availableSpots() {
        return levels.stream().mapToLong(Level::availableCount).sum();
    }

    public Optional<Ticket> findTicket(String ticketId) {
        return Optional.ofNullable(activeTickets.get(ticketId));
    }

    public List<Level> getLevels() {
        return levels;
    }
}
