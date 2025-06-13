package com.fightinggame.tournament.bracket.controller;

import com.fightinggame.tournament.bracket.dto.BracketInitializationRequest;
import com.fightinggame.tournament.bracket.dto.BracketResponse;
import com.fightinggame.tournament.bracket.model.Bracket;
import com.fightinggame.tournament.bracket.service.BracketService;
import com.fightinggame.tournament.bracket.service.assigner.AssignmentType;
import com.fightinggame.tournament.match.model.Match;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BracketControllerTest {

    @Mock
    private BracketService bracketService;

    @InjectMocks
    private BracketController bracketController;

    @Test
    void createBracket_Success() {
        // Arrange
        BracketInitializationRequest request = new BracketInitializationRequest(AssignmentType.FULLY_RANDOM);

        // Act
        ResponseEntity<Void> response = bracketController.createBracket(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bracketService).initializeBracket(request);
    }


    @Test
    void getBracket_WithExistingBracket_ReturnsBracket() {
        // Arrange
        var left = new Match();
        var root = new Match();
        root.setLeftMatch(left);

        Bracket mockBracket = new Bracket();
        mockBracket.setRootMatch(root);

        BracketResponse expectedResponse = new BracketResponse(mockBracket);

        when(bracketService.getCurrentBracket()).thenReturn(expectedResponse);

        // Act
        ResponseEntity<BracketResponse> response = bracketController.getBracket();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(bracketService).getCurrentBracket();
    }

    @Test
    void getBracket_NoBracketExists_ReturnsNoContent() {
        // Given
        when(bracketService.getCurrentBracket()).thenReturn(null);

        // When
        ResponseEntity<BracketResponse> response = bracketController.getBracket();

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(bracketService).getCurrentBracket();
    }

    // @Valid on createBracket covers problems with initializationRequest null cases
}