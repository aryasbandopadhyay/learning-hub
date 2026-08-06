package com.example.parkinglot.strategy;

import com.example.parkinglot.model.SpotType;
import com.example.parkinglot.model.Ticket;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

/**
 * Concrete {@link FeeStrategy}: charge per started hour, at a rate that depends on the spot size.
 * A partial hour is billed as a full hour, and a minimum of one hour is charged.
 */
public class HourlyFeeStrategy implements FeeStrategy {

    private final Map<SpotType, Long> hourlyRate;

    /** Default demo rates: SMALL=10, MEDIUM=20, LARGE=30 per hour. */
    public HourlyFeeStrategy() {
        this(defaultRates());
    }

    public HourlyFeeStrategy(Map<SpotType, Long> hourlyRate) {
        this.hourlyRate = new EnumMap<>(hourlyRate);
    }

    private static Map<SpotType, Long> defaultRates() {
        Map<SpotType, Long> r = new EnumMap<>(SpotType.class);
        r.put(SpotType.SMALL, 10L);
        r.put(SpotType.MEDIUM, 20L);
        r.put(SpotType.LARGE, 30L);
        return r;
    }

    @Override
    public long calculateFee(Ticket ticket, Instant exitTime) {
        Duration parked = Duration.between(ticket.getEntryTime(), exitTime);
        long minutes = Math.max(0, parked.toMinutes());
        long hours = Math.max(1, (long) Math.ceil(minutes / 60.0)); // round up, min 1 hour
        long rate = hourlyRate.getOrDefault(ticket.getSpot().getSpotType(), 0L);
        return hours * rate;
    }
}
