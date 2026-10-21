package MAIN.COM.schttttt_game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
        for (String color : Card.COLORS) {
            for (int value = 1; value <= 9; value++) {
                cards.add(new Card(value, color));
            }
        }
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty.");
        }
        return cards.remove(0); // We draw the first card of the Deck
    }
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }
}