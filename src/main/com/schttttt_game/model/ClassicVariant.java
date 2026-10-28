package main.com.schttttt_game.model;

public class ClassicVariant implements GameVariant{
    @Override
    public String getName() {
        return "Classic Game";
    }

    @Override
    public Deck createDeck(){
        return new Deck();
    }

    @Override
    public int getHandSize() {
        return 6;
    }    

}
