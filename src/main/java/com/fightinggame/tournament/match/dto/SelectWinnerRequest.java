package com.fightinggame.tournament.match.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record SelectWinnerRequest(
        @NotNull
        @Positive
        int matchReferenceValue,

        @NotNull
        @PositiveOrZero
        int playerId
) {}
