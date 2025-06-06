package com.fightinggame.tournament.bracket.service.util;

import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.bracket.service.generator.SingleEliminationGenerator;
import com.fightinggame.tournament.match.model.Match;
import com.fightinggame.tournament.shared.MatchOperator;
import com.fightinggame.tournament.player.model.Player;
import com.fightinggame.tournament.shared.TournamentMathUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ByeMatchSimplifierTest {

    @Spy
    private MatchOperator operator;

    @InjectMocks
    private ByeMatchSimplifier simplifier;

    private SingleEliminationGenerator generator;

    @BeforeEach
    void setup() {
        generator = new SingleEliminationGenerator(new TournamentMathUtils());
    }

    @Test
    void simplify_shouldHandleEmptyBracket() {
        // Arrange
        Bracket bracket = new Bracket();
        bracket.setRootMatch(null); // Explicitly set to null

        // Act
        assertDoesNotThrow(() -> simplifier.simplify(bracket));

        // Assert
        assertNull(bracket.getRootMatch());
    }

    @Test
    void simplify_shouldNotModifyCompleteMatches() {
        // Arrange
        Bracket bracket = generator.generate(4); // 4-player bracket (no byes)
        Match root = bracket.getRootMatch();
        Match leftLeaf = root.getLeftMatch();
        Match rightLeaf = root.getRightMatch();

        // Set up complete matches (no byes)
        Player p1 = new Player(1, "Player1");
        Player p2 = new Player(2, "Player2");
        Player p3 = new Player(3, "Player3");
        Player p4 = new Player(4, "Player4");

        leftLeaf.setPlayer1(p1);
        leftLeaf.setPlayer2(p2);
        rightLeaf.setPlayer1(p3);
        rightLeaf.setPlayer2(p4);

        // Act
        simplifier.simplify(bracket);

        // Assert
        assertSame(leftLeaf, root.getLeftMatch());
        assertSame(rightLeaf, root.getRightMatch());
        assertEquals(p1, leftLeaf.getPlayer1());
        assertEquals(p2, leftLeaf.getPlayer2());
        assertEquals(p3, rightLeaf.getPlayer1());
        assertEquals(p4, rightLeaf.getPlayer2());
    }

    @Test
    void simplify_shouldHandleByeInLeftLeaf() {
        // Arrange
        Bracket bracket = generator.generate(3); // 3-player bracket (will have byes)
        Match root = bracket.getRootMatch();
        Match leftLeaf = root.getLeftMatch();
        Match rightLeaf = root.getRightMatch();

        Player p1 = new Player(1, "Player1");
        Player p2 = new Player(2, "Player2");

        leftLeaf.setPlayer1(p1);
        rightLeaf.setPlayer1(p2);
        rightLeaf.setPlayer2(null); // Explicitly set to null to simulate bye

        // Act
        simplifier.simplify(bracket);

        // Assert
        assertNull(root.getLeftMatch());
        assertNull(root.getRightMatch());
        assertEquals(p1, root.getPlayer1());
        assertEquals(p2, root.getPlayer2());
    }

    @Test
    void simplify_shouldHandleByeInRightLeaf() {
        // Arrange
        Bracket bracket = generator.generate(3); // 3-player bracket
        Match root = bracket.getRootMatch();
        Match leftLeaf = root.getLeftMatch();
        Match rightLeaf = root.getRightMatch();

        Player p1 = new Player(1, "Player1");
        Player p2 = new Player(2, "Player2");

        leftLeaf.setPlayer1(p1);
        leftLeaf.setPlayer2(null);
        rightLeaf.setPlayer1(p2);

        // Act
        simplifier.simplify(bracket);

        // Assert
        assertNull(root.getLeftMatch());
        assertNull(root.getRightMatch());
        assertEquals(p1, root.getPlayer1());
        assertEquals(p2, root.getPlayer2());
    }

    @Test
    void simplify_shouldCallAllocateWinnerForByeMatches() {
        // Arrange
        Bracket bracket = generator.generate(3);
        Match root = bracket.getRootMatch();
        Match leftLeaf = root.getLeftMatch();

        Player p1 = new Player(1, "Player1");
        leftLeaf.setPlayer1(p1);

        // Act
        simplifier.simplify(bracket);

        // Verify that the spy was called
        verify(operator, times(1)).allocateWinner(any(), any());
    }
}