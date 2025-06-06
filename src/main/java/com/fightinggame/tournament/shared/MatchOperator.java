package com.fightinggame.tournament.shared;

import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.match.model.Match;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class MatchOperator {

    /**
     * Search in a balanced tree
     */
    public Match searchMatch (Bracket bracket, int targetReferenceValue) {

        Match matchPointer = bracket.getRootMatch();

        while (true) {
            if (matchPointer.getReferenceValue() == targetReferenceValue) return matchPointer; // Match found
            else if (matchPointer.getReferenceValue() > targetReferenceValue) { // if this match reference is bigger, go to the left
                if (matchPointer.getLeftMatch() == null) break;                 // if there is no match on the left break the loop
                else matchPointer = matchPointer.getLeftMatch();                // otherwise keep looking on the left side
            } else {
                if (matchPointer.getRightMatch() == null) break;
                else matchPointer = matchPointer.getRightMatch();
            }
        }
        return null; // Match not found
    }


    /**
     * Tree search for a parent node/match by its child value in a balanced tree/bracket
     */
    public Match searchParentMatch (Bracket bracket, int targetReferenceValue) {
        return searchParentMatchRecursive(bracket.getRootMatch(), targetReferenceValue);
    }

    private Match searchParentMatchRecursive(Match match, int targetReferenceValue) {

        Match left = match.getLeftMatch();
        Match right = match.getRightMatch();

        // If left or right is available, check if its value is equal the target value, if not call the next search
        // It looks like repeated code but in a method the "return null" at the end would break the logic so...

        if (left != null) {
            if (left.getReferenceValue() == targetReferenceValue) return match;
            if (targetReferenceValue < match.getReferenceValue()){
                return searchParentMatchRecursive(left, targetReferenceValue);
            }
        }

        if (right != null) {
            if (right.getReferenceValue() == targetReferenceValue) return match;
            if (targetReferenceValue > match.getReferenceValue()){
                return searchParentMatchRecursive(right, targetReferenceValue);
            }
        }

        // Not found
        return null;
    }



    /**
     * Search for leaves in the bracket (initial matches), add these to a list and return it
     */
    public List<Match> getLeaves (Bracket bracket) {
        // Breadth-First Search

        List<Match> leaves = new ArrayList<>();

        if (bracket.getRootMatch() == null) {
            // If the bracket was not initialized return an empty list
            return leaves;
        }

        // Each layer of nodes in the tree-bracket gets queued before moving to the next layer
        Queue<Match> queue = new LinkedList<>();
        queue.add(bracket.getRootMatch());
        while (!queue.isEmpty()) {

            // Get the next match in the queue
            Match currentMatch = queue.poll();

            boolean leftIsNull = currentMatch.getLeftMatch() == null;
            boolean rightIsNull = currentMatch.getRightMatch() == null;

            // If a leaf node is found add it to the list of leaves
            if (leftIsNull && rightIsNull) {
                leaves.add(currentMatch);
                continue;
            }

            // Queue this node/match children
            if (!leftIsNull) {
                queue.add(currentMatch.getLeftMatch());
            }
            if (!rightIsNull) {
                queue.add(currentMatch.getRightMatch());
            }
        }

        return leaves;
    }

    /**
     * Check appearances of a player in the bracket and clear them until the specified match reference value
     */
    public void clearPlayerWins(Bracket bracket, int targetReferenceValue, long playerId) {
        Match matchPointer = bracket.getRootMatch();

        while (true) {
            // If this player won, set the winner state to null
            if (matchPointer.getWinner() != null) {
                if (matchPointer.getWinner().getId() == playerId) {
                    matchPointer.setWinner(null);
                }
            }

            // In case this is the target match, stop the cleaning
            if (matchPointer.getReferenceValue() == targetReferenceValue) {
                break;
            }

            // if the player is in the match, remove them
            if (matchPointer.getPlayer1() != null) {
                if (matchPointer.getPlayer1().getId() == playerId) {
                    matchPointer.setPlayer1(null);
                }
            }
            if (matchPointer.getPlayer2() != null) {
                if (matchPointer.getPlayer2().getId() == playerId) {
                    matchPointer.setPlayer2(null);
                }
            }

            // Move the pointer through the bracket
            if (matchPointer.getReferenceValue() > targetReferenceValue) {
                matchPointer = matchPointer.getLeftMatch();
            } else {
                matchPointer = matchPointer.getRightMatch();
            }
        }
    }

    /**
     * Set a player to any empty space in the next match
     */
    public void allocateWinner (Match nextMatch, Match match) {

        if (nextMatch.getPlayer1() == null) {
            nextMatch.setPlayer1(match.getWinner());
        } else if (nextMatch.getPlayer2() == null) {
            nextMatch.setPlayer2(match.getWinner());
        }
    }



}
