package com.fightinggame.tournament.bracket.controller;

import com.fightinggame.tournament.bracket.dto.BracketResponse;
import com.fightinggame.tournament.match.dto.SelectWinnerRequest;
import com.fightinggame.tournament.bracket.dto.BracketInitializationRequest;
import com.fightinggame.tournament.bracket.service.BracketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller managing tournament brackets - creation and retrieval.
 */
@RestController
@RequestMapping ("/bracket")
public class BracketController {
    
    @Autowired
    private BracketService bracketService;

    /**
     * Creates new bracket with specified assignment strategy.
     * @param initializationRequest Contains strategy type (RANDOM/SKILL_BASED)
     * @return 200 OK
     * @throws IllegalArgumentException for invalid request
     */
    @PostMapping
    public ResponseEntity<Void> createBracket (
            @RequestBody @Valid BracketInitializationRequest initializationRequest
    ) {
        bracketService.initializeBracket(initializationRequest);

        return ResponseEntity.ok().build();
    }

    /**
     * Gets current bracket with match pairings and player assignments.
     * @return 200 OK with bracket data
     */
    @GetMapping
    public ResponseEntity<BracketResponse> getBracket () {
        var currentBracket = bracketService.getCurrentBracket();
        return currentBracket != null ? ResponseEntity.ok(currentBracket)
                : ResponseEntity.noContent().build();
    }


}
