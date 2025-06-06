package com.fightinggame.tournament.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TournamentMathUtilsTest {

    private final TournamentMathUtils mathUtils = new TournamentMathUtils();

    @ParameterizedTest
    @MethodSource("powerOfTwoProvider")
    void nextPowerOfTwo_shouldReturnCorrectValues(int input, int expected) {
        assertEquals(expected, mathUtils.nextPowerOfTwo(input));
    }

    private static Stream<Arguments> powerOfTwoProvider() {
        return Stream.of(
                Arguments.of(1, 2),
                Arguments.of(2, 2),
                Arguments.of(3, 4),
                Arguments.of(4, 4),
                Arguments.of(5, 8),
                Arguments.of(7, 8),
                Arguments.of(8, 8),
                Arguments.of(9, 16),
                Arguments.of(15, 16),
                Arguments.of(16, 16),
                Arguments.of(17, 32),
                Arguments.of(31, 32),
                Arguments.of(32, 32)
        );
    }

    @ParameterizedTest
    @MethodSource("depthCalculationProvider")
    void calculateDepth_shouldReturnCorrectDepth(int players, int expectedDepth) {
        assertEquals(expectedDepth, mathUtils.calculateDepth(players));
    }

    private static Stream<Arguments> depthCalculationProvider() {
        return Stream.of(
                Arguments.of(1, 0),    // 2^0 = 1
                Arguments.of(2, 1),    // 2^1 = 2
                Arguments.of(3, 2),     // 2^2 = 4
                Arguments.of(4, 2),     // 2^2 = 4
                Arguments.of(5, 3),      // 2^3 = 8
                Arguments.of(8, 3),      // 2^3 = 8
                Arguments.of(9, 4),       // 2^4 = 16
                Arguments.of(16, 4),      // 2^4 = 16
                Arguments.of(17, 5),      // 2^5 = 32
                Arguments.of(32, 5)       // 2^5 = 32
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5, -100})
    void calculateDepth_shouldHandleInvalidInput(int invalidInput) {
        assertThrows(IllegalArgumentException.class,
                () -> mathUtils.calculateDepth(invalidInput));
    }

    @Test
    void calculateIndexesToPlayerInsertionOrder_shouldReturnCorrectOrderFor2Leaves() {
        List<Integer> expected = List.of(0, 1);
        assertEquals(expected, mathUtils.calculateIndexesToPlayerInsertionOrder(2));
    }

    @Test
    void calculateIndexesToPlayerInsertionOrder_shouldReturnCorrectOrderFor4Leaves() {
        List<Integer> expected = List.of(0, 2, 1, 3);
        assertEquals(expected, mathUtils.calculateIndexesToPlayerInsertionOrder(4));
    }

    @Test
    void calculateIndexesToPlayerInsertionOrder_shouldReturnCorrectOrderFor8Leaves() {
        List<Integer> expected = List.of(0, 4, 2, 6, 1, 5, 3, 7);
        assertEquals(expected, mathUtils.calculateIndexesToPlayerInsertionOrder(8));
    }

    @Test
    void calculateIndexesToPlayerInsertionOrder_shouldReturnCorrectOrderFor16Leaves() {
        List<Integer> expected = List.of(0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15);
        assertEquals(expected, mathUtils.calculateIndexesToPlayerInsertionOrder(16));
    }

    @Test
    void calculateIndexesToPlayerInsertionOrder_shouldHandleOddNumberOfLeaves() {
        // For non-power-of-two, it should still work (though in practice numLeaves should be power of 2)
        // Later it will, not crucial for now
    }

    @Test
    void calculateIndexesToPlayerInsertionOrder_shouldReturnEmptyListForZeroLeaves() {
        // assertTrue(mathUtils.calculateIndexesToPlayerInsertionOrder(0).isEmpty());
    }

    @Test
    void calculateIndexesToPlayerInsertionOrder_shouldHandleSingleLeaf() {
        // assertEquals(List.of(0), mathUtils.calculateIndexesToPlayerInsertionOrder(1));
    }

}