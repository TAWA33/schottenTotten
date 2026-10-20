package model;

import java.util.ArrayList;
import java.util.List;

public class Limit {
    private int index;
    private Card_Combinaison[] zones; // zones[0] = player 1, zones[1] = player 2
    private int control; // 0 = nobody, 1 = player 1, 2 = player 2
    private int firstToComplete; // If equality

    public Limit(int index) {
        this.index = index;
        this.zones = new Card_Combinaison[2];
        zones[0] = new Card_Combinaison();
        zones[1] = new Card_Combinaison();
        this.control = 0;
        this.firstToComplete = 0;
    }

    public int getIndex() {
        return index;
    }
    public int getControl() {
        return control;
    }
    public void setControl(int control) {
        this.control = control;
    }

    public Card_Combinaison getZone(int playerIndex) {
        return zones[playerIndex - 1]; // playerIndex 1 or 2
    }

    public int getFirstToComplete(){
        return firstToComplete;
    }

    public boolean isFull(int playerIndex) {
        return getZone(playerIndex).getNbCards() >= 3;
    }

    public void addCard(int playerIndex, Card card) {
        if (isFull(playerIndex)) {
            throw new IllegalStateException("Zone is full for player " + playerIndex);
        }
        getZone(playerIndex).addCard(card);
        if (isFull(playerIndex) && firstToComplete == 0) {
            firstToComplete = playerIndex;
        }
    }

    public void evaluate() {
        if (isFull(1) && isFull(2)) {
            int score1 = evaluateCombination(getZone(1));
            int score2 = evaluateCombination(getZone(2));
            if (score1 > score2) {
                control = 1;
            } else if (score2 > score1) {
                control = 2;
            } else {
                control = firstToComplete;
            }
        } else {
            //control = 0; // Cannot make the choice
        }
    }

    public static int evaluateCombination(Card_Combinaison zone) {
        List<Card> cards = zone.getCards();
        if (cards.size() != 3){
            return 0;
        }
        cards.sort((c1, c2) -> Integer.compare(c1.getValue(), c2.getValue())); // We use Lambda Comparator interface
        int v1 = cards.get(0).getValue();
        int v2 = cards.get(1).getValue();
        int v3 = cards.get(2).getValue();
        String c1 = cards.get(0).getColor();
        String c2 = cards.get(1).getColor();
        String c3 = cards.get(2).getColor();

        boolean sameColor = c1.equals(c2) && c2.equals(c3);
        boolean consecutive = (v2 == v1 + 1) && (v3 == v2 + 1);
        boolean threeOfAKind = (v1 == v2) && (v2 == v3);

        if (sameColor && consecutive) {
            return 5000 + v1 + v2 + v3; // Quinte flush
        } else if (threeOfAKind) {
            return 4000 + v1 * 3; // Brelan
        } else if (sameColor) {
            return 3000 + v1 + v2 + v3; // Couleur
        } else if (consecutive) {
            return 2000 + v1 + v2 + v3; // Suite
        } else {
            return 1000 + v1 + v2 + v3; // Somme
        }
    }

    public boolean canClaim(int player, List<Card> unknown) {
        if (control != 0 || !isFull(player)){
            return false;
        }

        int opp = 3 - player;
        int myScore = evaluateCombination(getZone(player));
        List<Card> oppCards = getZone(opp).getCards();
        int missing = 3 - oppCards.size();

        if (missing == 0) { // 2 zones are full !
            int oppScore = evaluateCombination(getZone(opp));
            if (myScore != oppScore){
                return myScore > oppScore;
            }
            return firstToComplete == player;
        }
        return !canBeat(oppCards, unknown, 0, missing, myScore);
    }

    public boolean claim(int player, List<Card> unknown) {
        if (canClaim(player, unknown)) {
            control = player;
            return true;
        }
        return false;
    }

    // Backtracking Algorithm : https://en.wikipedia.org/wiki/Backtracking
    private static boolean canBeat(List<Card> current, List<Card> pool, int start, int missing, int target) {
        // We try to add to "current" (cards in the limit zone), Unknown cards "pool" (from the deck or cards in the hand of the opponent) 
        
        if (missing == 0) { // zone is full we can compare the 2 zones
            return evaluateCombination(new Card_Combinaison(current)) > target; // Return if the opponent can beat the target ( = score of the current player)
        }
        for (int i = start; i < pool.size(); i++) { // The opponent zone is not full so we have to add 1 or more cards 
            current.add(pool.get(i));
            boolean beat = canBeat(current, pool, i + 1, missing - 1, target); // Recursive call with one fewer card to add (missing - 1) and start = i + 1 to avoid reusing the same card
            current.remove(current.size() - 1); // We remove the card ( = back track). Come back to the initial version
            if (beat){
                return true; // We found a better possible combination => The player cannot claim the limit
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Limit " + (index + 1) + " [P1: " + zones[0] + " | P2: " + zones[1] + "] Control: " + control;
    }
}