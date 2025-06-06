package com.fightinggame.tournament.bracket.service.generator;

import com.fightinggame.tournament.bracket.model.Bracket;

public interface BracketGenerator {

    Bracket generate (int numPlayers);
}
