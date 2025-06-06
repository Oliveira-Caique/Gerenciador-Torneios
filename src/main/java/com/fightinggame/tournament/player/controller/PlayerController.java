package com.fightinggame.tournament.player.controller;

import com.fightinggame.tournament.player.dto.PlayerRequest;
import com.fightinggame.tournament.player.dto.PlayerResponse;
import com.fightinggame.tournament.player.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles player lifecycle operations in the tournament system.
 */
@CrossOrigin(origins = "http://localhost:8000")
@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    /**
     * Registers a new player.
     * @param request Player details (name, rating, etc.)
     * @return 201 Created with the registered player data
     */
    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody PlayerRequest request) {
        PlayerResponse response = playerService.createPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a specific player's information.
     * @param id Player's unique identifier
     * @return 200 OK with player data
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayerById(@PathVariable long id) {
        return ResponseEntity.of(playerService.getPlayerById(id));
    }

    /**
     * Lists all registered players.
     * @return 200 OK with player list (empty if none registered)
     */
    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        List<PlayerResponse> players = playerService.getAllPlayers();
        return ResponseEntity.ok(players);
    }

    /**
     * Updates player details.
     * @param id Player's unique identifier
     * @param request Updated player details
     * @return 200 OK with updated data
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlayerResponse> updatePlayer(
            @PathVariable long id,
            @Valid @RequestBody PlayerRequest request) {
        return ResponseEntity.of(playerService.updatePlayer(id, request));
    }

    /**
     * Removes a player from the system.
     * @param id Player's unique identifier
     * @return 204 No Content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }

}