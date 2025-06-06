package com.fightinggame.tournament.bracket.service.generator;

import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.match.model.Match;
import com.fightinggame.tournament.shared.TournamentMathUtils;
import org.springframework.stereotype.Component;

/**
 * Creates an empty single elimination bracket
 * The purpose is to have a base structure before committing to any kind of iteration
 */
@Component
public class SingleEliminationGenerator implements BracketGenerator {

    private final TournamentMathUtils mathUtils;


    public SingleEliminationGenerator(TournamentMathUtils mathUtils) {
        this.mathUtils = mathUtils;
    }


    /**
     * Create an empty bracket which every player can occupy a leaf node (initial matches)
     * */
    public Bracket generate(int numPlayers) {

        if (numPlayers < 2) {
            throw new IllegalArgumentException("It's not possible to generate a bracket with less than 2 players");
        }

        int numOfSpaces = mathUtils.nextPowerOfTwo(numPlayers);

        Match root = new Match();           // Initialize the root node
        root.setReferenceValue(numOfSpaces/2);

        Bracket bracket = new Bracket();    // Initialize the bracket
        bracket.setRootMatch(root);

        int depth = mathUtils.calculateDepth(numOfSpaces) - 1;  // Minus one because the root was already initialized
        populateMatches(bracket.getRootMatch(), depth);         // Fill the bracket with match nodes

        if (numOfSpaces > 2) {
            setMatchReferenceValues(root, numOfSpaces/2, numOfSpaces/4);
        }

        return bracket;
    }


    /**
     * Given the expected height of the bracket keep adding a new layer of empty nodes
     * */
    private void populateMatches(Match current, int depthRemaining) {
        if (depthRemaining == 0) return;    // End the recursion

        current.setLeftMatch(new Match());
        current.setRightMatch(new Match());

        populateMatches(current.getLeftMatch(), depthRemaining-1);
        populateMatches(current.getRightMatch(), depthRemaining-1);
    }


    /**
     * Set reference values to the nodes/match of the tree/bracket. Useful for optimal search in a balanced tree.
     */
    private void setMatchReferenceValues (Match match, int pastValue, int pastHalf) {

        Match left = match.getLeftMatch();
        Match right = match.getRightMatch();

        left.setReferenceValue(pastValue - pastHalf);
        right.setReferenceValue(pastValue + pastHalf);

        if (pastHalf == 1) return;
        else {
            setMatchReferenceValues(left, left.getReferenceValue(), pastHalf/2);
            setMatchReferenceValues(right, right.getReferenceValue(), pastHalf/2);
        }
    }

}
