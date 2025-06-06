package com.fightinggame.tournament.bracket.service.generator;

import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.shared.TournamentMathUtils;
import com.fightinggame.tournament.match.model.Match;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SingleEliminationGeneratorTest {

    @Mock
    private TournamentMathUtils mathUtils;

    @InjectMocks
    private SingleEliminationGenerator generator;

    @ParameterizedTest
    @ValueSource(ints = {2, 4, 8, 16, 32})
    void generate_shouldCreateValidBracketForPowerOfTwoPlayers(int numPlayers) {
        // Arrange
        when(mathUtils.nextPowerOfTwo(numPlayers)).thenReturn(numPlayers);
        when(mathUtils.calculateDepth(numPlayers)).thenReturn((int) (Math.log(numPlayers) / Math.log(2)));

        // Act
        Bracket bracket = generator.generate(numPlayers);

        // Assert
        assertNotNull(bracket);
        assertNotNull(bracket.getRootMatch());
        verify(mathUtils, times(1)).nextPowerOfTwo(numPlayers);
        verify(mathUtils, times(1)).calculateDepth(numPlayers);
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 5, 6, 7, 9, 10, 15, 17, 31})
    void generate_shouldCreateValidBracketForNonPowerOfTwoPlayers(int numPlayers) {
        // Arrange
        int nextPower = calculateNextPowerOfTwo(numPlayers);
        when(mathUtils.nextPowerOfTwo(numPlayers)).thenReturn(nextPower);
        when(mathUtils.calculateDepth(nextPower)).thenReturn((int) (Math.log(nextPower) / Math.log(2)));

        // Act
        Bracket bracket = generator.generate(numPlayers);

        // Assert
        assertNotNull(bracket);
        assertNotNull(bracket.getRootMatch());
        verify(mathUtils, times(1)).nextPowerOfTwo(numPlayers);
        verify(mathUtils, times(1)).calculateDepth(nextPower);
    }

    @Test
    void generate_shouldThrowExceptionForLessThan2Players() {
        assertThrows(IllegalArgumentException.class, () -> generator.generate(1));
        assertThrows(IllegalArgumentException.class, () -> generator.generate(0));
        assertThrows(IllegalArgumentException.class, () -> generator.generate(-1));
    }

    @Test
    void generate_shouldCreateProperBracketStructureFor4Players() {
        // Arrange
        int numPlayers = 4;
        when(mathUtils.nextPowerOfTwo(numPlayers)).thenReturn(4);
        when(mathUtils.calculateDepth(4)).thenReturn(2);

        // Act
        Bracket bracket = generator.generate(numPlayers);

        // Assert
        assertNotNull(bracket.getRootMatch());
        assertEquals(2, bracket.getRootMatch().getReferenceValue());

        Match left = bracket.getRootMatch().getLeftMatch();
        Match right = bracket.getRootMatch().getRightMatch();

        assertNotNull(left);
        assertNotNull(right);
        assertEquals(1, left.getReferenceValue());
        assertEquals(3, right.getReferenceValue());

        // Verify leaves
        assertNull(left.getLeftMatch());
        assertNull(left.getRightMatch());
        assertNull(right.getLeftMatch());
        assertNull(right.getRightMatch());
    }

    @Test
    void generate_shouldCreateProperBracketStructureFor8Players() {
        // Arrange
        int numPlayers = 8;
        when(mathUtils.nextPowerOfTwo(numPlayers)).thenReturn(8);
        when(mathUtils.calculateDepth(8)).thenReturn(3);

        // Act
        Bracket bracket = generator.generate(numPlayers);

        // Assert
        assertNotNull(bracket.getRootMatch());
        assertEquals(4, bracket.getRootMatch().getReferenceValue());

        // Check first level
        Match left = bracket.getRootMatch().getLeftMatch();
        Match right = bracket.getRootMatch().getRightMatch();
        assertEquals(2, left.getReferenceValue());
        assertEquals(6, right.getReferenceValue());

        // Check second level
        Match leftLeft = left.getLeftMatch();
        Match leftRight = left.getRightMatch();
        Match rightLeft = right.getLeftMatch();
        Match rightRight = right.getRightMatch();

        assertEquals(1, leftLeft.getReferenceValue());
        assertEquals(3, leftRight.getReferenceValue());
        assertEquals(5, rightLeft.getReferenceValue());
        assertEquals(7, rightRight.getReferenceValue());

        // Verify leaves
        assertNull(leftLeft.getLeftMatch());
        assertNull(leftLeft.getRightMatch());
        assertNull(leftRight.getLeftMatch());
        assertNull(leftRight.getRightMatch());
        assertNull(rightLeft.getLeftMatch());
        assertNull(rightLeft.getRightMatch());
        assertNull(rightRight.getLeftMatch());
        assertNull(rightRight.getRightMatch());
    }

    private int calculateNextPowerOfTwo(int value) {
        int power = 2;
        while (power < value) power *= 2;
        return power;
    }
}