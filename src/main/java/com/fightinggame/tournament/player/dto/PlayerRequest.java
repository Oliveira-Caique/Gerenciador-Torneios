package com.fightinggame.tournament.player.dto;


import com.fightinggame.tournament.player.model.Player;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PlayerRequest(

        @NotBlank(message = "Nicknames cannot be blank")
        @Size(min = 3, max = 20, message = "Nicknames must be between 3 and 20 characters")
        String nickname,

        @PositiveOrZero (message = "The rating value must be equal or bigger than zero")
        int rating
) {}
