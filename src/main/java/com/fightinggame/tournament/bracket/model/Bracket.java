package com.fightinggame.tournament.bracket.model;

import com.fightinggame.tournament.match.model.Match;
import lombok.Data;

/**
 * Representation of the bracket in our system
 * this class handles all tournament operations from its root node
 */
@Data
public class Bracket {

    private long id;

    private Match rootMatch;    // Finals

    public String toString () {

        if (rootMatch == null) {
            return "[Empty Bracket]";
        }

        StringBuilder text = new StringBuilder();

        concatMatchStringRecursive(rootMatch, 0, text);

        return text.toString();
    }

    private void concatMatchStringRecursive (Match match, int depth, StringBuilder text) {

        for (int i = 0; i < depth; i++) {
            text.append("  ");
        }

        text.append(match).append("\n");

        if (match.getLeftMatch() != null) {
            concatMatchStringRecursive (match.getLeftMatch(), depth+1, text);
        }
        if (match.getRightMatch() != null) {
            concatMatchStringRecursive (match.getRightMatch(), depth+1, text);
        }
    }
}