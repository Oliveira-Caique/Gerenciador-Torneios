package com.fightinggame.tournament.shared;

import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.bracket.service.generator.SingleEliminationGenerator;
import com.fightinggame.tournament.match.model.Match;
import com.fightinggame.tournament.player.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MatchOperatorTest {

    @Spy
    private MatchOperator operator;
    private Bracket bracket;

    @BeforeEach
    void setup () {
        SingleEliminationGenerator generator = new SingleEliminationGenerator(new TournamentMathUtils());
        bracket = generator.generate(8); // for an 8-player bracket we have 7 matches
    }

    @Test
    void searchMatch_shouldReturnMatch_whenReferenceValueExists () {
        // Act
        Match match  = operator.searchMatch(bracket, 5);

        // Assert
        assertNotNull(match);
        assertEquals(5, match.getReferenceValue());
    }

    @Test
    void searchMatch_shouldReturnNull_whenReferenceValueDoesNotExists () {
        // Act
        Match match  = operator.searchMatch(bracket, 8);

        // Assert
        assertNull(match);
    }

    @Test
    void searchMatch_shouldReturnRoot_whenSearchingForRootValue () {
        // Arrange
        Match root = bracket.getRootMatch(); // In a balanced tree with 4 leaves (8 players), root should be 4

        // Act
        Match found = operator.searchMatch(bracket, root.getReferenceValue());

        // Assert
        assertSame(root, found);
    }

    @Test
    void searchParentMatch_shouldReturnParent_whenChildExists () {
        // In an 8-player bracket:
        // - Match 4 is root
        // - Match 2 is left child of 4
        // - Match 3 is right child of 2

        // Act
        Match parentOf2 = operator.searchParentMatch(bracket, 2);
        Match parentOf3 = operator.searchParentMatch(bracket, 3);

        // Assert
        assertNotNull(parentOf2);
        assertEquals(4, parentOf2.getReferenceValue());

        assertNotNull(parentOf3);
        assertEquals(2, parentOf3.getReferenceValue());
    }

    @Test
    void searchParentMatch_shouldReturnNull_whenSearchingForRoot() {
        // Act
        Match parent = operator.searchParentMatch(bracket, bracket.getRootMatch().getReferenceValue());

        // Assert
        assertNull(parent);
    }

    @Test
    void searchParentMatch_shouldReturnNull_whenReferenceValueDoesNotExist() {
        // Act
        Match parent = operator.searchParentMatch(bracket, 999);

        // Assert
        assertNull(parent);
    }

    @Test
    void getLeaves_shouldReturnAllLeafMatches() {
        // Leaves should be odd values, in an 8-player bracket: 1, 3, 5 and 7

        // Act
        List<Match> leaves = operator.getLeaves(bracket);

        // Assert
        assertEquals(4, leaves.size());
        assertTrue(leaves.stream().anyMatch(m -> m.getReferenceValue() == 1));
        assertTrue(leaves.stream().anyMatch(m -> m.getReferenceValue() == 3));
        assertTrue(leaves.stream().anyMatch(m -> m.getReferenceValue() == 5));
        assertTrue(leaves.stream().anyMatch(m -> m.getReferenceValue() == 7));
    }

    @Test
    void getLeaves_shouldReturnEmptyList_whenBracketIsEmpty() {
        // Arrange
        Bracket emptyBracket = new Bracket();

        // Act
        List<Match> leaves = operator.getLeaves(emptyBracket);

        // Assert
        assertTrue(leaves.isEmpty());
    }

    @Test
    void clearPlayerWins_shouldRemovePlayerFromMatchesUpToTarget() {
        // Arrange
        Player player = new Player(1, "Test Player");

        // Set player in matches 1 (leaf) and 2 (parent)
        Match match1 = operator.searchMatch(bracket, 1);
        Match match2 = operator.searchMatch(bracket, 2);

        match1.setPlayer1(player);
        match1.setWinner(player);
        match2.setPlayer1(player);
        match2.setWinner(player);

        // Act - clear up to match 1
        operator.clearPlayerWins(bracket, 1, 1);

        // Assert
        assertNull(match2.getPlayer1());
        assertNull(match2.getWinner());
        assertNotNull(match1.getPlayer1());
        assertNull(match1.getWinner());
    }

    @Test
    void clearPlayerWins_shouldNotAffectOtherPlayers() {
        // Arrange
        Player player1 = new Player(1, "Player 1");
        Player player2 = new Player(2, "Player 2");

        Match match1 = operator.searchMatch(bracket, 1);
        match1.setPlayer1(player1);
        match1.setPlayer2(player2);
        match1.setWinner(player1);

        // Act
        operator.clearPlayerWins(bracket, 1, 1);

        // Assert
        assertNotNull(match1.getPlayer1());
        assertNull(match1.getWinner());
        assertSame(player2, match1.getPlayer2());   // Player 2 should remain
    }

    @Test
    void clearPlayerWins_shouldRedefineMatchWithOtherWinner() {
        // Arrange
        Player player1 = new Player(1, "Player 1");
        Player player2 = new Player(2, "Player 2");

        Match match1 = operator.searchMatch(bracket, 1);
        Match match2 = operator.searchMatch(bracket, 2);
        match1.setPlayer1(player1);
        match1.setWinner(player1);
        match2.setPlayer1(player1);
        match2.setPlayer2(player2); // Player from another bracket side
        match2.setWinner(player2);

        // Act
        operator.clearPlayerWins(bracket, 1, 1);


        // Assert
        assertNull(match2.getPlayer1());
        assertEquals(match2.getPlayer2(), player2);
        assertEquals(match2.getWinner(), player2);
        assertNull(match1.getWinner());
        assertEquals(match1.getPlayer1(), player1);
    }

    @Test
    void allocateWinner_shouldSetPlayer1First() {
        // Arrange
        Match nextMatch = new Match();

        Player winner = new Player(1, "Winner");
        Match currentMatch = new Match();
        currentMatch.setWinner(winner);

        // Act
        operator.allocateWinner(nextMatch, currentMatch);

        // Assert
        assertSame(winner, nextMatch.getPlayer1());
        assertNull(nextMatch.getPlayer2());
    }

    @Test
    void allocateWinner_shouldSetPlayer2WhenPlayer1IsOccupied() {
        // Arrange
        Match nextMatch = new Match();
        nextMatch.setPlayer1(new Player(2, "Existing Player"));

        Player winner = new Player(1, "Winner");
        Match currentMatch = new Match();
        currentMatch.setWinner(winner);

        // Act
        operator.allocateWinner(nextMatch, currentMatch);

        // Assert
        assertSame(winner, nextMatch.getPlayer2());
    }

    @Test
    void allocateWinner_shouldNotChangeMatch_whenBothPlayersAreOccupied() {
        // Arrange
        Match nextMatch = new Match();
        Player existingPlayer1 = new Player(2, "Player 1");
        Player existingPlayer2 = new Player(3, "Player 2");
        nextMatch.setPlayer1(existingPlayer1);
        nextMatch.setPlayer2(existingPlayer2);

        Player winner = new Player(1, "Winner");
        Match currentMatch = new Match();
        currentMatch.setWinner(winner);

        // Act
        operator.allocateWinner(nextMatch, currentMatch);

        // Assert
        assertSame(existingPlayer1, nextMatch.getPlayer1());
        assertSame(existingPlayer2, nextMatch.getPlayer2());
    }

}