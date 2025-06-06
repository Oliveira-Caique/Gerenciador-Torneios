package com.fightinggame.tournament.bracket.service.util;

import com.fightinggame.tournament.bracket.model.Bracket;

/**
 * Singleton class responsible for storing the current tournament bracket
 */
public class BracketHolder {

    private static BracketHolder instance;
    private Bracket currentBracket;

    private BracketHolder() {}  // Private constructor

    public static synchronized BracketHolder getInstance() {
        if (instance == null) {
            instance = new BracketHolder();
        }
        return instance;
    }

    public void storeBracket(Bracket bracket) {
        this.currentBracket = bracket;
    }

    public Bracket getCurrentBracket() {
        return currentBracket;
    }

    public void clearBracket() {
        currentBracket = null;
    }
}
