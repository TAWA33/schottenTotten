package main.com.schttttt_game.model;

import java.util.ArrayList;
import java.util.List;

public class Limit {
    private int index;
    private Card_Combinaison[] zones; // zones[0] = player 1, zones[1] = player 2
    private int control; // 0 = nobody, 1 = player 1, 2 = player 2
    private int firstToComplete; // If equality

    private TacticCard combatModeCard;

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
        return getZone(playerIndex).getNbCards() >= getCapacity();
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

    public void setCombatModeCard(TacticCard card) {
        if (card.getType() != TacticType.MUD_FIGHT && card.getType() != TacticType.BLIND_MAN_BLUFF) {
            throw new IllegalArgumentException("Not a combat mode card");
        }
        this.combatModeCard = card;
    }
    public int getCapacity() {
        return hasMudFight() ? 4 : 3;
    }

    public boolean usesCombinations() {
        return !hasBlindManBluff();
    }

    public TacticCard getCombatModeCard() {
        return combatModeCard;
    }

    public boolean hasMudFight() {
        return combatModeCard != null && combatModeCard.getType() == TacticType.MUD_FIGHT;
    }

    public boolean hasBlindManBluff() {
        return combatModeCard != null && combatModeCard.getType() == TacticType.BLIND_MAN_BLUFF;
    }

    public void evaluate() {
        if (isFull(1) && isFull(2)) {
            int score1 = evaluateCombination(getZone(1), getCapacity(), usesCombinations());
            int score2 = evaluateCombination(getZone(2), getCapacity(), usesCombinations());
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
        return evaluateCombination(zone, 3, true);
    }

    public static int evaluateCombination(Card_Combinaison zone, int capacity, boolean useCombinations) {
        List<Card> cards = zone.getCards();
        if (cards.size() != capacity){
            return 0;
        }
        return bestScore(new ArrayList<>(cards), useCombinations);
    }

    private static boolean isEliteTroup(Card c) {
        if (!(c instanceof TacticCard)){
            return false;
        }
        TacticType t = ((TacticCard) c).getType();
        return t == TacticType.JOKER || t == TacticType.SPY || t == TacticType.SHIELD_BEARER;
    }

    private static int bestScore(List<Card> cards, boolean useCombinations) {
        int EliteIdx = -1;
        for (int i = 0; i < cards.size(); i++) {
            if (isEliteTroup(cards.get(i))) { EliteIdx = i; break; }
        }
        if (EliteIdx == -1){
            return evaluateStatic(cards, useCombinations);
        }

        TacticType type = ((TacticCard) cards.get(EliteIdx)).getType();
        int[] values;
        if (type == TacticType.JOKER){
            values = new int[]{1,2,3,4,5,6,7,8,9};
        } else if (type == TacticType.SHIELD_BEARER){
            values = new int[]{1,2,3};
        } else{
            values = new int[]{7}; // SPY
        }
        int best = 0;
        for (String color : Card.COLORS) {
            for (int v : values) {
                List<Card> trial = new ArrayList<>(cards);
                trial.set(EliteIdx, new ClanCard(v, color));
                int score = bestScore(trial, useCombinations);
                if (score > best){
                    best = score;
                }
            }
        }
        return best;
    }

    private static int evaluateStatic(List<Card> cards, boolean useCombinations) {
        // BL only SUM
        if (!useCombinations) {
            int sum = 0;
            for (Card c : cards) sum += c.getValue();
            return sum;
        }
        List<Card> sorted = new ArrayList<>(cards);
        sorted.sort((a, b) -> Integer.compare(a.getValue(), b.getValue()));

        int sum = 0;
        for (Card c : sorted) sum += c.getValue();

        for (Card c : sorted) { // Verification
            if (c.getColor() == null) return sum;
        }

        int size = sorted.size();
        int v1 = sorted.get(0).getValue();
        String c1 = sorted.get(0).getColor();

        boolean sameColor = true;
        for (Card c : sorted) {
            if (!c.getColor().equals(c1)) {
                sameColor = false;
                break;
            }
        }

        boolean consecutive = true; // consecutive : v[i] == v[i-1] + 1 for all i
        for (int i = 1; i < size; i++) {
            if (sorted.get(i).getValue() != sorted.get(i - 1).getValue() + 1) {
                consecutive = false;
                break;
            }
        }

        boolean allSameValue = true;
        for (Card c : sorted) {
            if (c.getValue() != v1) {
                allSameValue = false;
                break;
            }
        }

        if (sameColor && consecutive) return 5000 + sum;
        if (allSameValue) return 4000 + v1 * size;   // v1*3 or v1*4
        if (sameColor) return 3000 + sum;
        if (consecutive) return 2000 + sum;
        return 1000 + sum;
    }

    public boolean canClaim(int player, List<Card> unknown) {
        if (control != 0 || !isFull(player)){
            return false;
        }

        int opp = 3 - player;
        int myScore = evaluateCombination(getZone(player), getCapacity(), usesCombinations());
        List<Card> oppCards = getZone(opp).getCards();
        int missing = getCapacity() - oppCards.size();

        if (missing == 0) { // 2 zones are full !
            int oppScore = evaluateCombination(getZone(opp), getCapacity(), usesCombinations());
            if (myScore != oppScore){
                return myScore > oppScore;
            }
            return firstToComplete == player;
        }
        return !canBeat(oppCards, unknown, 0, missing, myScore, getCapacity(), usesCombinations());
    }

    public boolean claim(int player, List<Card> unknown) {
        if (canClaim(player, unknown)) {
            control = player;
            return true;
        }
        return false;
    }

    // Backtracking Algorithm : https://en.wikipedia.org/wiki/Backtracking
    private static boolean canBeat(List<Card> current, List<Card> pool, int start, int missing, int target, int capacity, boolean useCombinations) {
        // We try to add to "current" (cards in the limit zone), Unknown cards "pool" (from the deck or cards in the hand of the opponent) 
        
        if (missing == 0) { // zone is full we can compare the 2 zones
            return evaluateCombination(new Card_Combinaison(current), capacity, useCombinations) > target; // Return if the opponent can beat the target ( = score of the current player)
        }
        for (int i = start; i < pool.size(); i++) { // The opponent zone is not full so we have to add 1 or more cards 
            current.add(pool.get(i));
            boolean beat = canBeat(current, pool, i + 1, missing - 1, target, capacity, useCombinations); // Recursive call with one fewer card to add (missing - 1) and start = i + 1 to avoid reusing the same card
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