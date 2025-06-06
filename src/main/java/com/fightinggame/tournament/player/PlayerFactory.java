package com.fightinggame.tournament.player;

import com.fightinggame.tournament.player.model.Player;

public final class PlayerFactory {
    private static long idSequence = 1L;

    private PlayerFactory() {} // Prevent instantiation

    public static Player createPlayer(String nickname, int rating) {
        return new Player(idSequence++, nickname, rating);
    }

    // For testing purposes
    public static void reset() {
        idSequence = 1L;
    }
}
