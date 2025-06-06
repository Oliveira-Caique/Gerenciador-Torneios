package com.fightinggame.tournament.bracket.dto;

import com.fightinggame.tournament.bracket.model.Bracket;

public record BracketResponse(
        Bracket bracket
) {

    public static BracketResponse fromEntity(Bracket bracket) {
        return new BracketResponse(
                bracket
        );
    }
}
