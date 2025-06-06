package com.fightinggame.tournament.bracket.dto;

import com.fightinggame.tournament.bracket.service.assigner.AssignmentType;
import com.fightinggame.tournament.player.dto.PlayerRequest;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BracketInitializationRequest(
        AssignmentType assignerType
) {
}
