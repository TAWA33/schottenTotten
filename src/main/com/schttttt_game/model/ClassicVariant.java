package main.com.schttttt_game.model;

public class ClassicVariant implements GameVariant{
    @Override
    public String getName() {
        return "Classic Game";
    }

    @Override
    public Deck createClanDeck(){
        return new Deck();
    }

    @Override
    public Deck createTacticDeck() {
        return new Deck(false);
    }

    @Override
    public int getHandSize() {
        return 6;
    }    

}
