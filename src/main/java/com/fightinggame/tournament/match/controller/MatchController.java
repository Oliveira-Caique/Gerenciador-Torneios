package com.fightinggame.tournament.match.controller;

import com.fightinggame.tournament.match.dto.SelectWinnerRequest;
import com.fightinggame.tournament.match.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles match winner operations in tournament brackets via REST API.
 */
@RestController
@RequestMapping ("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    /**
     * Declares a winner for a match and advances them in the bracket.
     * @param request Contains match reference and winning player ID
     * @return 200 OK on success
     */
    @PutMapping("/winner")
    public ResponseEntity<Void> selectWinner (
            @RequestBody SelectWinnerRequest request)
    {
        matchService.selectWinner(request.matchReferenceValue(), request.playerId());

        return ResponseEntity.ok().build();
    }

    /**
     * Reverts a previously declared match winner.
     * @param request Contains match reference and player ID to deselect
     * @return 200 OK on success
     */
    @DeleteMapping("/winner")
    public ResponseEntity<Void> deselectWinner (
            @RequestBody SelectWinnerRequest request)
    {
        matchService.deselectWinner(request.matchReferenceValue(), request.playerId());

        return ResponseEntity.ok().build();
    }
}
