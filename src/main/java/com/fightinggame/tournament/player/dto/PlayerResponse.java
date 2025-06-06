package com.fightinggame.tournament.player.dto;

import com.fightinggame.tournament.player.model.Player;

public record PlayerResponse(
        long id,
        String nickname,
        int rating
) {
    public static PlayerResponse fromEntity(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getNickname(),
                player.getRating()
        );
    }
}
