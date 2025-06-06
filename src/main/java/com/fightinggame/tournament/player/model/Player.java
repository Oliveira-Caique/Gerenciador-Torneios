package com.fightinggame.tournament.player.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Player implements Comparable<Player> {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    private String nickname;

    @Builder.Default
    private int rating = 0;     // Default rating value is zero

    public Player(long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    @Override
    public int compareTo(Player other) {
        return Integer.compare(this.rating, other.rating);
    }
}