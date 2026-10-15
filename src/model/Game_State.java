package model;

import java.util.ArrayList;
import java.util.List;

public class Game_State {
    private List<Player> players;
    private List<Limit> limits;
    private Deck deck;
    private int currentPlayerIndex; // 0 or 1
    private boolean gameOver;
    private int winner; // 1 or 2, 0 if no winner at the moment

    public Game_State() {
        players = new ArrayList<>();
        limits = new ArrayList<>();
        gameOver = false;
        winner = 0;
        currentPlayerIndex = 0;
    }

    public List<Player> getPlayers() {
        return players;
    }
    public void addPlayer(Player p) {
        players.add(p);
    }
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    public void switchPlayer() {
        currentPlayerIndex = 1 - currentPlayerIndex;
    }
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    public List<Limit> getLimits() { 
        return limits;
    }
    public void addLimit(Limit l) {
        limits.add(l);
    }
    public Deck getDeck() {
        return deck;
    }
    public void setDeck(Deck deck) {
        this.deck = deck;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
    public int getWinner() {
        return winner;
    }
    public void setWinner(int winner) {
        this.winner = winner;
    }
}