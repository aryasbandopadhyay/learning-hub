package com.example.splitwise.strategy;

import com.example.splitwise.exception.InvalidSplitException;
import com.example.splitwise.model.Split;
import com.example.splitwise.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Percent split: integer percentages must sum to 100; cents are allocated without float math. */
public class PercentSplitStrategy implements SplitStrategy {

    @Override
    public List<Split> split(long totalCents, List<User> participants, Map<User, Long> values) {
        if (participants == null || participants.isEmpty()) {
            throw new InvalidSplitException("At least one participant is required");
        }
        if (values == null) {
            throw new InvalidSplitException("Percent split requires percentages");
        }
        long percentSum = 0;
        for (User user : participants) {
            Long percent = values.get(user);
            if (percent == null || percent < 0) {
                throw new InvalidSplitException("Missing or negative percent for " + user);
            }
            percentSum += percent;
        }
        if (percentSum != 100) {
            throw new InvalidSplitException("Percent split must sum to 100");
        }

        List<Split> splits = new ArrayList<>();
        long allocated = 0;
        for (int i = 0; i < participants.size(); i++) {
            User user = participants.get(i);
            long amount = totalCents * values.get(user) / 100;
            if (i == participants.size() - 1) {
                amount = totalCents - allocated; // give rounding remainder to the last participant
            }
            allocated += amount;
            splits.add(new Split(user, amount));
        }
        return splits;
    }
}
