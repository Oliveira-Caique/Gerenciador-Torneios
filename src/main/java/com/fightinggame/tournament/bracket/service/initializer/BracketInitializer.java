package com.fightinggame.tournament.bracket.service.initializer;

import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.bracket.service.assigner.AssignmentType;
import com.fightinggame.tournament.player.model.Player;

import java.util.List;

public interface BracketInitializer {

    Bracket initializeBracket(List<Player> players, AssignmentType assignmentType);
}
