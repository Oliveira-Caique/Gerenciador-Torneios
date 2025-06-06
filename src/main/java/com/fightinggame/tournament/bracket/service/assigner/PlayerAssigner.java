package com.fightinggame.tournament.bracket.service.assigner;

import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.shared.MatchOperator;
import com.fightinggame.tournament.shared.TournamentMathUtils;
import com.fightinggame.tournament.match.model.Match;
import com.fightinggame.tournament.player.model.Player;
import lombok.AllArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@AllArgsConstructor
public abstract class PlayerAssigner {

    private final MatchOperator bracketOperator;
    private final TournamentMathUtils mathUtils;

    /**
     * Method that assigns players to the first-round matches (leaf nodes).
     */
    public void assignmentExecution(Bracket bracket, List<Player> players) {

        Deque<Player> playerDeque = new ArrayDeque<>(players);
        List<Match> leaves = bracketOperator.getLeaves(bracket);

        // Get indexes to distribute players in a balanced way through the bracket
        List<Integer> playerOrder = mathUtils.calculateIndexesToPlayerInsertionOrder(leaves.size());

        // Distribute Players 1
        for (int i : playerOrder) {
            leaves.get(i).setPlayer1(playerDeque.pop());
        }

        // Distribute Players 2
        for (int i : playerOrder) {
            if (playerDeque.isEmpty()) break;
            else leaves.get(i).setPlayer2(playerDeque.pop());
        }

    }
}
