package com.fightinggame.tournament.match.service;

import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.bracket.service.BracketService;
import com.fightinggame.tournament.exception.model.MatchNotFoundException;
import com.fightinggame.tournament.match.model.Match;
import com.fightinggame.tournament.player.model.Player;
import com.fightinggame.tournament.shared.MatchOperator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MatchService {

    private final BracketService bracketService;
    private final MatchOperator bracketOperator;

    /**
     * Advances the winning player to the next match in the bracket.
     * @param matchReferenceValue Identifier for the target match
     * @param playerId ID of the winning player
     * @throws IllegalStateException if bracket not initialized or match incomplete
     * @throws MatchNotFoundException if match doesn't exist
     * @throws IllegalArgumentException if player not in match
     * */
    public void selectWinner (int matchReferenceValue, int playerId) {

        Bracket bracket = bracketService.getCurrentBracket().bracket(); // Get the current bracket
        validateBracket(bracket);

        Match match = bracketOperator.searchMatch(bracket, matchReferenceValue);    // Get the selected match
        validateMatchPlayers(match, playerId);
        if (match.getPlayer1() == null || match.getPlayer2() == null) {
            throw new IllegalStateException("Match must have two players to select a winner");
        }

        Player selectedPlayer = getSelectedPlayer(playerId, match);
        long otherPlayerId = getOtherPlayerId(playerId, match);

        if (match.getWinner() != null) {    // if there is a winner already, check ...
            if (match.getWinner().getId() == playerId) return;          // if the same player is selected as winner, then end the method
            else {                                                      // if not, then clear the other player wins along the bracket
                bracketOperator.clearPlayerWins(bracket, matchReferenceValue, otherPlayerId);
            }
        }

        match.setWinner(selectedPlayer);        // Set the selected player as winner
        if (match != bracket.getRootMatch()) {  // If not the final match, then move the winner to the next match
            Match nextMatch = bracketOperator.searchParentMatch(bracket, match.getReferenceValue());
            bracketOperator.allocateWinner(nextMatch, match);
        }
    }


    /**
     * Reverts a previously selected winner in a match.
     * @param matchReferenceValue Identifier for the target match
     * @param playerId ID of the player to deselect
     * @throws IllegalStateException if bracket not initialized
     * @throws MatchNotFoundException if match doesn't exist
     * @throws IllegalArgumentException if player wasn't the winner
     */
    public void deselectWinner (int matchReferenceValue, int playerId) {

        Bracket bracket = bracketService.getCurrentBracket().bracket(); // Get the current bracket
        validateBracket(bracket);

        Match match = bracketOperator.searchMatch(bracket, matchReferenceValue);    // Get the selected match
        validateMatchPlayers(match, playerId);
        if (match.getWinner() == null) {
            throw new IllegalArgumentException("There wasn't a defined winner in the referenced match");
        }
        if (match.getWinner().getId() != playerId) {
            throw new IllegalArgumentException("Mismatch between winner id and player id values");
        }

        bracketOperator.clearPlayerWins(bracket, matchReferenceValue, playerId);
    }

    private Player getSelectedPlayer (int playerId, Match match) {
        if (match.getPlayer1().getId() == playerId) {
            return match.getPlayer1();
        } else {
            return match.getPlayer2();
        }
    }

    private long getOtherPlayerId (int playerId, Match match) {
        if (match.getPlayer1().getId() == playerId) {
            return match.getPlayer2().getId();
        } else {
            return match.getPlayer1().getId();
        }
    }

    private void validateMatchPlayers(Match match, int playerId) {
        if (match == null) {
            throw new MatchNotFoundException("Select an existing reference value for a match in current bracket");
        }
        if (match.getPlayer1().getId() != playerId && match.getPlayer2().getId() != playerId) {
            throw new IllegalArgumentException("Player id not found in the players of the selected match");
        }
    }

    private void validateBracket(Bracket bracket) {
        if (bracket == null) {
            throw new IllegalStateException("Before any operation please initialize the bracket");
        }
    }

}
