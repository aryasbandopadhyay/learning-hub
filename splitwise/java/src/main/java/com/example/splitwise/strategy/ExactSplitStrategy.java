package com.example.splitwise.strategy;

import com.example.splitwise.exception.InvalidSplitException;
import com.example.splitwise.model.Split;
import com.example.splitwise.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Exact split: caller supplies each participant's share in cents; the shares must sum exactly. */
public class ExactSplitStrategy implements SplitStrategy {

    @Override
    public List<Split> split(long totalCents, List<User> participants, Map<User, Long> values) {
        if (participants == null || participants.isEmpty()) {
            throw new InvalidSplitException("At least one participant is required");
        }
        if (values == null) {
            throw new InvalidSplitException("Exact split requires amounts");
        }
        List<Split> splits = new ArrayList<>();
        long sum = 0;
        for (User user : participants) {
            Long amount = values.get(user);
            if (amount == null || amount < 0) {
                throw new InvalidSplitException("Missing or negative exact amount for " + user);
            }
            sum += amount;
            splits.add(new Split(user, amount));
        }
        if (sum != totalCents) {
            throw new InvalidSplitException("Exact split amounts must sum to total");
        }
        return splits;
    }
}
