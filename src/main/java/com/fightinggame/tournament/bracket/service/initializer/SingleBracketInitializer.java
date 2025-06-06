package com.fightinggame.tournament.bracket.service.initializer;

import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.bracket.service.assigner.AssignmentStrategy;
import com.fightinggame.tournament.bracket.service.assigner.AssignmentStrategyFactory;
import com.fightinggame.tournament.bracket.service.assigner.AssignmentType;
import com.fightinggame.tournament.bracket.service.generator.BracketGenerator;
import com.fightinggame.tournament.bracket.service.util.ByeMatchSimplifier;
import com.fightinggame.tournament.player.dto.PlayerResponse;
import com.fightinggame.tournament.player.model.Player;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates single-elimination brackets with player assignments and bye match handling.
 */
@AllArgsConstructor
@Component
public class SingleBracketInitializer implements BracketInitializer {

    private final BracketGenerator bracketGenerator;
    private final ByeMatchSimplifier byeMatchSimplifier;

    // Strategy factory pattern to handle the type of player assignment chosen by the user
    private final AssignmentStrategyFactory assignmentStrategyFactory;

    /**
     * Generates complete bracket with players assigned using specified strategy.
     * @param players Players to include in bracket
     * @param assignmentType Player distribution strategy (RANDOM/SKILL_BASED)
     * @return Ready-to-use tournament bracket
     */
    @Override
    public Bracket initializeBracket(List<Player> players, AssignmentType assignmentType) {

        Bracket bracket = bracketGenerator.generate(players.size());

        AssignmentStrategy assigner = assignmentStrategyFactory.getStrategy(assignmentType);
        assigner.assignPlayers(bracket, players);

        byeMatchSimplifier.simplify(bracket);

        return bracket;
    }
}
