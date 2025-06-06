package com.fightinggame.tournament.match.model;

import com.fightinggame.tournament.player.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Match {

    private int referenceValue;

    private Player player1;
    private Player player2;
    private Player winner;

    private Match leftMatch;
    private Match rightMatch;

    public String toString () {
        return "Match (" +
                "referenceValue: " + referenceValue +
                ", player1: " + player1 +
                ", player2: " + player2 +
                ", winner: " + winner +
                ")";
    }
}
