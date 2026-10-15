package model;

public class Player {
    private String name;
    private Card_Combinaison hand;
    private boolean isAI;

    public Player(String name, boolean isAI) {
        this.name = name;
        this.hand = new Card_Combinaison();
        this.isAI = isAI;
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
        if (hand.getNbCards() >= 6) {
            throw new IllegalStateException("Hand is full (max 6 cards)");
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