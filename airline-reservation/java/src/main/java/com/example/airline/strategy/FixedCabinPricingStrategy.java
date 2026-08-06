package com.example.airline.strategy;

import com.example.airline.model.Cabin;

import java.util.Map;

/** Simple deterministic fare table, intentionally not a full revenue-management system. */
public class FixedCabinPricingStrategy implements CabinPricingStrategy {

    private final Map<Cabin, Long> prices;

    public FixedCabinPricingStrategy() {
        this(Map.of(Cabin.ECONOMY, 5_000L, Cabin.BUSINESS, 12_000L));
    }

    public FixedCabinPricingStrategy(Map<Cabin, Long> prices) {
        this.prices = Map.copyOf(prices);
    }

    @Override
    public long priceFor(Cabin cabin) {
        return prices.getOrDefault(cabin, 0L);
    }
}
