package com.fightinggame.tournament.bracket.service.util;

import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.match.model.Match;
import com.fightinggame.tournament.shared.MatchOperator;
import com.fightinggame.tournament.player.model.Player;
import org.springframework.stereotype.Component;

/**
 * Simplifies tournament without enough players for a full bracket
 * Handles edge cases like bye matches (matches with missing players)
 */
@Component
public class ByeMatchSimplifier {

    private final MatchOperator bracketOperator;

    public ByeMatchSimplifier(MatchOperator bracketOperator) {
        this.bracketOperator = bracketOperator;
    }

    /**
     * Calls a recursive method to free the bye matches in the bracket from the root node
     * @param bracket the current one
     */
    public void simplify(Bracket bracket) {
        if (bracket == null || bracket.getRootMatch() == null) {
            return;
        }
        postorderTraversalSimplification(bracket.getRootMatch());
    }

    /**
     * Search for bye matches, if found, move the player to the next match recursively
     * @param match the current match to be (or not) simplified
     */
    private void postorderTraversalSimplification (Match match) {

        Match left = match.getLeftMatch();
        Match right = match.getRightMatch();

        // Check left, right and root respectively
        if (!isMatchLeaf(left)) postorderTraversalSimplification(left);
        if (!isMatchLeaf(right)) postorderTraversalSimplification(right);

        // Byes conditions are: having only one player AND being a leaf even after past simplifications
        // After that move the non-null player to the next match
        if (isMatchBye(left) && isMatchLeaf(left)) {
            left.setWinner(getNonNullPlayer(left));
            bracketOperator.allocateWinner(match, left);
            match.setLeftMatch(null);
        }
        if (isMatchBye(right) && isMatchLeaf(right)) {
            right.setWinner(getNonNullPlayer(right));
            bracketOperator.allocateWinner(match, right);
            match.setRightMatch(null);
        };
    }

    private boolean isMatchBye (Match match) {
        return (match.getPlayer1() == null ^ match.getPlayer2() == null);
    }

    private boolean isMatchLeaf (Match match) {
        return (match.getLeftMatch() == null && match.getRightMatch() == null);
    }

    private Player getNonNullPlayer (Match match) {
        if (match.getPlayer1() != null) return match.getPlayer1();
        else return match.getPlayer2();
    }

}
