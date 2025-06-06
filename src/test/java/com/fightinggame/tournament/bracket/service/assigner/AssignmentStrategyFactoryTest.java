package com.fightinggame.tournament.bracket.service.assigner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AssignmentStrategyFactoryTest {

    @Mock
    private AssignmentStrategy randomAssignmentStrategy;

    @Mock
    private AssignmentStrategy rankedAssignmentStrategy;

    private AssignmentStrategyFactory factory;

    @BeforeEach
    void setUp() {
        // Initialize factory with mocked strategies
        factory = new AssignmentStrategyFactory(
                List.of(randomAssignmentStrategy, rankedAssignmentStrategy)
        );
    }

    @Test
    void shouldReturnRandomAssignmentStrategyForFullyRandomType() {
        // Arrange
        when(randomAssignmentStrategy.supports(AssignmentType.FULLY_RANDOM)).thenReturn(true);

        // Act
        AssignmentStrategy strategy = factory.getStrategy(AssignmentType.FULLY_RANDOM);

        // Assert
        assertThat(strategy).isSameAs(randomAssignmentStrategy);
        verify(randomAssignmentStrategy).supports(AssignmentType.FULLY_RANDOM);
    }

    @Test
    void shouldReturnRankedAssignmentStrategyForRankedRandomType() {
        // Arrange
        when(rankedAssignmentStrategy.supports(AssignmentType.RANKED_RANDOM)).thenReturn(true);

        // Act
        AssignmentStrategy strategy = factory.getStrategy(AssignmentType.RANKED_RANDOM);

        // Assert
        assertThat(strategy).isSameAs(rankedAssignmentStrategy);
        verify(rankedAssignmentStrategy).supports(AssignmentType.RANKED_RANDOM);
    }

    @Test
    void shouldThrowExceptionWhenNoStrategyFound() {
        // Arrange
        when(randomAssignmentStrategy.supports(any())).thenReturn(false);
        when(rankedAssignmentStrategy.supports(any())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> factory.getStrategy(AssignmentType.FULLY_RANDOM))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No strategy found");
    }
}