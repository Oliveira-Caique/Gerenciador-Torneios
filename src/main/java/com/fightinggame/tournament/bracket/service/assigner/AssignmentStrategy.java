package com.fightinggame.tournament.bracket.service.assigner;

import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.player.model.Player;

import java.util.List;

public interface AssignmentStrategy {

    void assignPlayers (Bracket bracket, List<Player> players);

    boolean supports(AssignmentType type);
}
