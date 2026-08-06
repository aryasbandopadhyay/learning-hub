package com.example.splitwise.strategy;

import com.example.splitwise.exception.InvalidSplitException;
import com.example.splitwise.model.Split;
import com.example.splitwise.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Equal split: divide by headcount; if cents do not divide evenly, first users get 1 extra cent. */
public class EqualSplitStrategy implements SplitStrategy {

    @Override
    public List<Split> split(long totalCents, List<User> participants, Map<User, Long> values) {
        if (totalCents <= 0) {
            throw new InvalidSplitException("Total must be positive");
        }
        if (participants == null || participants.isEmpty()) {
            throw new InvalidSplitException("At least one participant is required");
        }
        long base = totalCents / participants.size();
        long remainder = totalCents % participants.size();
        List<Split> splits = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) {
            long amount = base + (i < remainder ? 1 : 0);
            splits.add(new Split(participants.get(i), amount));
        }
        return splits;
    }
}
