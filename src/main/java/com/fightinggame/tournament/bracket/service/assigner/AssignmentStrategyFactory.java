package com.fightinggame.tournament.bracket.service.assigner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  Factory that provides the appropriate player assignment strategy based on game rules.
 *
 *  <p>The default implementation selects from:
 *  * - Fully Random
 *  * - Ranked Random
 */
@Component
public class AssignmentStrategyFactory {

    private final List<AssignmentStrategy> strategies;  // Spring injects ALL implementations of the strategy

    @Autowired
    public AssignmentStrategyFactory(List<AssignmentStrategy> strategies) {
        this.strategies = strategies;
    }

    public AssignmentStrategy getStrategy(AssignmentType type) {
        return strategies.stream()
                .filter(s -> s.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for type: " + type));
    }

}
