package com.example.airline.strategy;

import com.example.airline.model.Cabin;

/** Strategy hook for fares. The MVP uses fixed cabin prices; real pricing can replace it. */
public interface CabinPricingStrategy {
    long priceFor(Cabin cabin);
}
