package com.fightinggame.tournament.bracket.service.assigner;

import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.shared.MatchOperator;
import com.fightinggame.tournament.shared.TournamentMathUtils;
import com.fightinggame.tournament.player.model.Player;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Assigns players randomly without considering its ratings
 */
@Component
public class RandomPlayerAssigner extends PlayerAssigner implements AssignmentStrategy {

    public RandomPlayerAssigner(MatchOperator bracketOperator, TournamentMathUtils mathUtils) {
        super(bracketOperator, mathUtils);
    }

    /**
     * Assigns players to tournament bracket by shuffling the players before assignment.
     *
     * @param bracket the tournament bracket structure to populate
     * @param players list of players to assign
     */
    @Override
    public void assignPlayers(Bracket bracket, List<Player> players) {
        Collections.shuffle(players);   // Randomize the players order
        assignmentExecution(bracket, players);
    }

    @Override
    public boolean supports(AssignmentType type) {
        return type == AssignmentType.FULLY_RANDOM;
    }
}
