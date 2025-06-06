package com.fightinggame.tournament.shared;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TournamentMathUtils {


    /**
     * Calculate the depth of the bracket considering that the list of players size is a power of 2,
     * that is, since the quantity of participants may not fit perfectly in a bracket, bye matches
     * could exist.
     * */
    public int calculateDepth(int numLeaves) {
        if (numLeaves <= 0) {
            throw new IllegalArgumentException("The number of leaves must be at least 1");
        }
        return (int) Math.ceil(Math.log(numLeaves) / Math.log(2));
    }


    /**
     * Calculates tha next value of players for a full bracket
     */
    public int nextPowerOfTwo(int value) {
        int power = 2;
        while (power < value) power *= 2;
        return power;
    }

    /**
     * Create a list of indexes to assign players uniformly/balanced in the bracket
     */
    public List<Integer> calculateIndexesToPlayerInsertionOrder(int numLeaves) {

        List<Integer> order = new ArrayList<>();

        // Divide the number of leaves by two to firstly deal with the even indexes
        int qttOfEvenIndexes = numLeaves / 2;

        // Create a list of even numbers
        for (int i = 0; i < qttOfEvenIndexes; i++) {
            order.add(i * 2);
        }

        // If the list is bigger than 2, modify its odd positions on the first half as follows:
        // Switch the positions of i with [i - 1 + (half of the list size)]
        if (qttOfEvenIndexes > 2) {
            boolean isOddPosition = false;  // starts at zero
            for (int i = 0; i < qttOfEvenIndexes/2; i++) {
                if (isOddPosition) {
                    int targetIndex = (i - 1 + (qttOfEvenIndexes/2));
                    int temp = order.get(i);
                    order.set(i, order.get(targetIndex));
                    order.set(targetIndex, temp);
                }
                isOddPosition = !isOddPosition;
            }
        }

        // Add the second half of the indexes which is a copy of the first plus one
        for (int i : new ArrayList<>(order)) {
            order.add(i + 1);
        }

        return order;
    }

}
