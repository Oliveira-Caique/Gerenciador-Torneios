package com.fightinggame.tournament.bracket.service.assigner;

import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.shared.MatchOperator;
import com.fightinggame.tournament.shared.TournamentMathUtils;
import com.fightinggame.tournament.player.model.Player;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Assigns players uniformly through the bracket based on the ratings
 */
@Component
public class RankedPlayerAssigner extends PlayerAssigner implements AssignmentStrategy {

    public RankedPlayerAssigner(MatchOperator bracketOperator, TournamentMathUtils mathUtils) {
        super(bracketOperator, mathUtils);
    }

    /**
     * Distributes players across bracket positions using Elo-based seeding.
     *
     * <p>Top players (highest Elo) are placed first in separate bracket segments,
     * followed by progressively weaker players. Ensures competitive balance by:
     * - Preventing early top-player matchups
     * - Handling bye matches automatically
     * - Balancing skill disparities in early rounds
     *
     * @throws IllegalArgumentException if parameters are invalid (null/insufficient players)
     *
     * @param bracket the tournament bracket structure to populate
     * @param players list of players to assign
     */
    @Override
    public void assignPlayers(Bracket bracket, List<Player> players) {

        int playersSize = players.size();
        Collections.sort(players);

        List<Player> weakerPlayers = players.subList(0, playersSize/2);
        List<Player> strongerPlayers = players.subList(playersSize/2, playersSize);

        Collections.shuffle(strongerPlayers);
        Collections.shuffle(weakerPlayers);

        players = Stream.concat(strongerPlayers.stream(), weakerPlayers.stream())
                .collect(Collectors.toList());

        assignmentExecution(bracket, players);
    }

    @Override
    public boolean supports(AssignmentType type) {
        return type == AssignmentType.RANKED_RANDOM;
    }

}
