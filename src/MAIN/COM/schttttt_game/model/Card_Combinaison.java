package MAIN.COM.schttttt_game.model;

import java.util.ArrayList;
import java.util.List;

public class Card_Combinaison {
    private List<Card> cards;

    public Card_Combinaison() {
        this.cards = new ArrayList<>();
    }

    public Card_Combinaison(List<Card> cards){
        this.cards = new ArrayList<>(cards);
    }

    public void addCard(Card card){
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }
        cards.add(card);
    }

    public void removeCard(Card card){
        cards.remove(card);
    }

    public Card getCard(int index){
        return cards.get(index);
    }

    public int getNbCards(){
        return cards.size();
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public void clear() {
        cards.clear();
    }
    
    public List<Card> getCombinaison() {
        return List.copyOf(cards);
    }


    @Override 
    public String toString() {
        StringBuilder sb = new StringBuilder("Cards : [");
        for (int i = 0; i < cards.size(); i++) {
            sb.append(cards.get(i));
            if (i < cards.size() - 1){
                sb.append("; ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
