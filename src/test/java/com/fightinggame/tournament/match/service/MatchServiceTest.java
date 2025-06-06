package com.fightinggame.tournament.match.service;

import com.fightinggame.tournament.bracket.dto.BracketResponse;
import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.bracket.service.BracketService;
import com.fightinggame.tournament.exception.model.MatchNotFoundException;
import com.fightinggame.tournament.match.model.Match;
import com.fightinggame.tournament.player.model.Player;
import com.fightinggame.tournament.shared.MatchOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private BracketService bracketService;

    @Spy
    private MatchOperator bracketOperator = new MatchOperator();

    @InjectMocks
    private MatchService matchService;

    // Essentials for bracket manipulation tests
    private Bracket bracket;
    private Match root;
    private Match left;
    private Match right;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        root = new Match();
        root.setReferenceValue(2);

        bracket = new Bracket();
        bracket.setRootMatch(root);

        left = new Match(); left.setReferenceValue(1);
        right = new Match(); right.setReferenceValue(3);
        root.setLeftMatch(left);
        root.setRightMatch(right);

        player1 = new Player(1, "Player 1");
        left.setPlayer1(player1);
        player2 = new Player(2, "Player 2");
        left.setPlayer2(player2);

        // Mock the get bracket response
        when(bracketService.getCurrentBracket()).thenReturn(BracketResponse.fromEntity(bracket));
    }


    @Test
    void selectWinner_validSelection_firstTimeSelection() {
        // Arrange
        when(bracketOperator.searchParentMatch(bracket, 1)).thenReturn(root);
        when(bracketOperator.searchMatch(bracket, 1)).thenReturn(left);

        // Act
        matchService.selectWinner(1, 1);

        // Assert
        assertEquals(player1, left.getWinner());
        assertEquals(player1, root.getPlayer1());
        verify(bracketOperator).allocateWinner(root, left);
    }


    @Test
    void selectWinner_selectPlayer2AsWinner() {
        // Arrange
        when(bracketOperator.searchParentMatch(bracket, 1)).thenReturn(root);
        when(bracketOperator.searchMatch(bracket, 1)).thenReturn(left);

        // Act
        matchService.selectWinner(1, 2);

        // Assert
        assertEquals(player2, left.getWinner());
        assertEquals(player2, root.getPlayer1());
        verify(bracketOperator).allocateWinner(root, left);
    }


    @Test
    void selectWinner_reselectSameWinner_noChanges() {
        // Arrange
        left.setWinner(player1);
        root.setPlayer1(player1);
        when(bracketOperator.searchMatch(bracket, 1)).thenReturn(left);

        // Act
        matchService.selectWinner(1, 1);

        // Assert
        assertEquals(player1, left.getWinner());
        verify(bracketOperator, never()).clearPlayerWins(any(Bracket.class), anyInt(), anyInt());
        verify(bracketOperator, never()).allocateWinner(any(Match.class), any(Match.class));
    }


    @Test
    void selectWinner_changeWinner_clearsPreviousPath() {
        // Arrange
        left.setWinner(player1);
        root.setPlayer1(player1);
        when(bracketOperator.searchParentMatch(bracket, 1)).thenReturn(root);
        when(bracketOperator.searchMatch(bracket, 1)).thenReturn(left);

        // Act
        matchService.selectWinner(1, 2); // Change to player2

        // Assert
        assertEquals(player2, left.getWinner());
        assertEquals(player2, root.getPlayer1());
        verify(bracketOperator).clearPlayerWins(bracket, 1, 1L);
        verify(bracketOperator).allocateWinner(root, left);
    }


    @Test
    void selectWinner_invalidMatchReference_throwsException() {
        // Arrange
        when(bracketOperator.searchMatch(bracket, 999)).thenReturn(null);

        // Act & Assert
        assertThrows(MatchNotFoundException.class,
                () -> matchService.selectWinner(999, 1));
    }


    @Test
    void selectWinner_matchMissingPlayer_throwsException() {
        // Arrange
        left.setPlayer2(null);
        when(bracketOperator.searchMatch(bracket, 1)).thenReturn(left);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> matchService.selectWinner(1, 1));
    }


    @Test
    void selectWinner_playerNotInMatch_throwsException() {
        // Arrange
        when(bracketOperator.searchMatch(bracket, 1)).thenReturn(left);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> matchService.selectWinner(1, 999));
    }


    @Test
    void selectWinner_finalMatchWithNoParent_setsWinnerOnly() {
        // Arrange
        when(bracketOperator.searchMatch(bracket, 2)).thenReturn(root);
        root.setPlayer1(player1);
        root.setPlayer2(player2);

        // Act
        matchService.selectWinner(2, 1);

        // Assert
        assertEquals(player1, root.getWinner());
        verify(bracketOperator, never()).allocateWinner(any(Match.class), any(Match.class));
    }


    @Test
    void selectWinner_nextMatchPlayer1Taken_movesToPlayer2() {
        // Arrange
        Player player3 = new Player(3, "Player 3");
        right.setPlayer1(player3);
        right.setPlayer2(null);
        when(bracketOperator.searchParentMatch(bracket, 1)).thenReturn(root);
        when(bracketOperator.searchMatch(bracket, 1)).thenReturn(left);
        root.setPlayer1(player3); // Simulate previous winner

        // Act
        matchService.selectWinner(1, 1);

        // Assert
        assertEquals(player1, left.getWinner());
        assertEquals(player3, root.getPlayer1());
        assertEquals(player1, root.getPlayer2());
    }

    @Test
    void deselectWinner_validSelection_clearsWinner() {
        // Arrange
        left.setWinner(player1);
        when(bracketOperator.searchMatch(bracket, 1)).thenReturn(left);

        // Act
        matchService.deselectWinner(1, 1);

        // Assert
        assertNull(left.getWinner());
        verify(bracketOperator).clearPlayerWins(bracket, 1, 1L);
    }

    @Test
    void deselectWinner_playerNotWinner_throwsException() {
        // Arrange
        left.setWinner(player2); // player1 is not the winner
        when(bracketOperator.searchMatch(bracket, 1)).thenReturn(left);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> matchService.deselectWinner(1, 1));
        verify(bracketOperator, never()).clearPlayerWins(any(), anyInt(), anyLong());
    }

    @Test
    void deselectWinner_invalidMatchReference_throwsException() {
        // Arrange
        when(bracketOperator.searchMatch(bracket, 999)).thenReturn(null);

        // Act & Assert
        assertThrows(MatchNotFoundException.class,
                () -> matchService.deselectWinner(999, 1));
        verify(bracketOperator, never()).clearPlayerWins(any(), anyInt(), anyLong());
    }

    @Test
    void deselectWinner_playerNotInMatch_throwsException() {
        // Arrange
        when(bracketOperator.searchMatch(bracket, 1)).thenReturn(left);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> matchService.deselectWinner(1, 999)); // Invalid player ID
        verify(bracketOperator, never()).clearPlayerWins(any(), anyInt(), anyLong());
    }

    @Test
    void deselectWinner_noCurrentBracket_throwsException() {
        // Arrange
        when(bracketService.getCurrentBracket()).thenReturn(BracketResponse.fromEntity(null));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> matchService.deselectWinner(1, 1));
        verify(bracketOperator, never()).clearPlayerWins(any(), anyInt(), anyLong());
    }

    @Test
    void deselectWinner_clearsFromParentMatch() {
        // Arrange
        left.setWinner(player1);
        root.setPlayer1(player1); // Player was advanced to parent match
        when(bracketOperator.searchMatch(bracket, 1)).thenReturn(left);

        // Act
        matchService.deselectWinner(1, 1);

        // Assert
        assertNull(left.getWinner());
        verify(bracketOperator).clearPlayerWins(bracket, 1, 1L);
    }

}