package main.com.schttttt_game.model;

import java.util.ArrayList;
import java.util.List;

public class Game_State {
    private List<Player> players;
    private List<Limit> limits;
    private Deck clanDeck;
    private Deck tacticDeck;
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
    
    public List<Integer> getValidLimits(int playerIndex) {
        List<Integer> validLimits = new ArrayList<>();
        for (int i = 0; i < limits.size(); i++) {
            if (limits.get(i).getControl() == 0 && !limits.get(i).isFull(playerIndex)) {
                validLimits.add(i);
            }
        }
        return validLimits;
    }

    public void addLimit(Limit l) {
        limits.add(l);
    }
    public Deck getClanDeck() {
        return clanDeck;
    }
    public Deck getTacticDeck() {
        return tacticDeck;
    }
    public void setClanDeck(Deck deck){
        this.clanDeck = deck;
    }
    public void setTacticDeck(Deck deck){
        this.tacticDeck = deck;
    }
    public boolean hasTacticDeck() {
        return tacticDeck != null && !tacticDeck.isEmpty();
    }
    public void setDeck(Deck deck) {
        this.clanDeck = deck;
    }
    public Deck getDeck() { // For class test compatibility
        return clanDeck;
    }
    public void updateClaims() {
        for (int player = 1; player <= 2; player++) {
            List<Card> unknown = clanDeck.getCards(); // ClanDeck. We ommit now the Tactic cards that can change the claim (Joker, Shield, 7).
            unknown.addAll(players.get(2 - player).getHand().getCards()); // Opponent Hand
            unknown.addAll(players.get(currentPlayerIndex).getHand().getCards());
            for (Limit l : limits) {
                if (l.claim(player, unknown)) {
                    System.out.println("Limit " + (l.getIndex() + 1) + " claimed by " + players.get(player - 1).getName());
                }
            }
        }
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
    public int computeWinner(){
        // Victory if 3 adjacent limits or 5limits in total for a player
        int control1 = 0, control2 = 0;
        int consecutive1 = 0, consecutive2 = 0;
        int maxConsecutive1 = 0, maxConsecutive2 = 0;

        for (Limit l : getLimits()) {
            if (l.getControl() == 1) {
                control1++;
                consecutive1++;
                if (consecutive1 > maxConsecutive1){
                    maxConsecutive1 = consecutive1;
                }
            } else {
                consecutive1 = 0;
            }
            if (l.getControl() == 2) {
                control2++;
                consecutive2++;
                if (consecutive2 > maxConsecutive2){
                    maxConsecutive2 = consecutive2;
                }
            } else {
                consecutive2 = 0;
            }
        }

        if (control1 >= 5 || maxConsecutive1 >= 3) {
            return 1;
        } else if (control2 >= 5 || maxConsecutive2 >= 3) {
            return 2;
        }
        return 0;        
    }
    public Player getWinner() {
        if (winner == 0) {
            throw new IllegalStateException("No winner yet.");
        }
        return players.get(winner - 1);
    }
    public void setWinner(int winner) {
        this.winner = winner;
    }
}