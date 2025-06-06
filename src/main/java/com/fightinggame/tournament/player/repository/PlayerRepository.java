package com.fightinggame.tournament.player.repository;

import com.fightinggame.tournament.player.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
