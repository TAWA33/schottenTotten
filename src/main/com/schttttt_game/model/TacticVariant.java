package main.com.schttttt_game.model;

public class TacticVariant implements GameVariant{
    @Override
    public String getName() {
        return "Tactic Game";
    }

    @Override
    public Deck createDeck() {
        // We have to create the tectic card deck. Return an array or list of the 2 decks
        return new Deck();
    }

    @Override
    public int getHandSize() {
        return 7;
    }

}
