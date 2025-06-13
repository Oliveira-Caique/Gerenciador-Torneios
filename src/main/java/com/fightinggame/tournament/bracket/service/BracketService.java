package com.fightinggame.tournament.bracket.service;

import com.fightinggame.tournament.bracket.dto.BracketResponse;
import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.bracket.dto.BracketInitializationRequest;
import com.fightinggame.tournament.bracket.service.assigner.AssignmentType;
import com.fightinggame.tournament.bracket.service.initializer.BracketInitializer;
import com.fightinggame.tournament.bracket.service.util.BracketHolder;
import com.fightinggame.tournament.player.dto.PlayerResponse;
import com.fightinggame.tournament.player.model.Player;
import com.fightinggame.tournament.player.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for initializing, storing and returning the bracket
 */
@AllArgsConstructor
@Service
public class BracketService {

    private final PlayerService playerService;
    private final BracketInitializer bracketInitializer;

    // Basic singleton holder instead of repository persistence for now
    private final BracketHolder bracketHolder = BracketHolder.getInstance();

    /**
     * Creates and configures a tournament bracket from player data.
     *
     * <p>Performs full initialization pipeline:
     * 1. Validates ≥3 players exist
     * 2. Converts PlayerResponse → Player
     * 3. Executes facade to generate, populate and simplify a bracket
     * 4. Stores bracket
     *
     * @param initializationRequest contains assignment strategy
     * @throws IllegalArgumentException for invalid input (null, <3 players)
     */
    public void initializeBracket(BracketInitializationRequest initializationRequest) {

        if (initializationRequest == null) {
            throw new IllegalArgumentException("BracketInitializationRequest cannot be null");
        }

        AssignmentType assignmentType = initializationRequest.assignerType();
        if (assignmentType == null) {
            throw new IllegalArgumentException("AssignmentType cannot be null");
        }

        List<PlayerResponse> playerResponses = playerService.getAllPlayers();
        if (playerResponses == null) {
            throw new IllegalStateException("PlayerService returned null player list");
        }
        if (playerResponses.isEmpty()) {
            throw new IllegalArgumentException("At least 3 players required");
        }
        if (playerResponses.size() <= 2) {
            throw new IllegalArgumentException("At least 3 players required");
        }

        // Convert DTOs to domain objects
        List<Player> players = playerResponses.stream()
                .map(response -> Player.builder()
                        .id(response.id())
                        .nickname(response.nickname())
                        .rating(response.rating())
                        .build())
                .collect(Collectors.toList());

        Bracket bracket = bracketInitializer.initializeBracket(players, assignmentType);

        bracketHolder.storeBracket(bracket);
    }


    /**
     * Get the bracket stored in memory
     * @return the dto BracketResponse with the current bracket
     */
    public BracketResponse getCurrentBracket() {
        return BracketResponse.fromEntity(bracketHolder.getCurrentBracket());
    }






}