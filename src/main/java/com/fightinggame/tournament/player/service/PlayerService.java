package com.fightinggame.tournament.player.service;

import com.fightinggame.tournament.player.dto.PlayerRequest;
import com.fightinggame.tournament.player.dto.PlayerResponse;
import com.fightinggame.tournament.player.model.Player;
import com.fightinggame.tournament.player.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public PlayerResponse createPlayer(PlayerRequest request) {
        Player player = Player.builder()
                .nickname(request.nickname())
                .rating(request.rating())
                .build();

        Player savedPlayer = playerRepository.save(player);
        return PlayerResponse.fromEntity(savedPlayer);
    }

    public Optional<PlayerResponse> getPlayerById(long id) {
        return playerRepository.findById(id)
                .map(PlayerResponse::fromEntity);
    }

    public List<PlayerResponse> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(PlayerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<PlayerResponse> updatePlayer(long id, PlayerRequest request) {
        return playerRepository.findById(id)
                .map(existing -> {
                    Player updated = Player.builder()
                            .id(existing.getId())
                            .nickname(request.nickname())
                            .rating(request.rating())
                            .build();
                    return PlayerResponse.fromEntity(playerRepository.save(updated));
                });
    }

    public void deletePlayer(long id) {
        playerRepository.deleteById(id);
    }

}