package main.com.schttttt_game.model;

public class Player {
    private String name;
    private Card_Combinaison hand;
    private boolean isAI;
    private int maxHandSize;

    public Player(String name, boolean isAI, int maxHandSize) {
        this.name = name;
        this.hand = new Card_Combinaison();
        this.isAI = isAI;
        this.maxHandSize = maxHandSize;
    }

    public Player(String name, boolean isAI) { // Another constructor for GameTest 
        this(name, isAI, 6);
    }

    public String getName() {
        return name;
    }
    public Card_Combinaison getHand() {
        return hand;
    }
    public boolean isAI() {
        return isAI;
    }

    public void addCardToHand(Card card) {
        if (hand.getNbCards() >= maxHandSize) {
            throw new IllegalStateException("Hand is full (max " + maxHandSize + " cards)");
        }
        hand.addCard(card);
    }

    public Card removeCardFromHand(int index) {
        Card c = hand.getCard(index);
        hand.removeCard(c);
        return c;
    }

    @Override
    public String toString() {
        return name + " : " + hand;
    }
}