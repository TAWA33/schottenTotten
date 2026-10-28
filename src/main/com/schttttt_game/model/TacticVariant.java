package main.com.schttttt_game.model;

public class TacticVariant implements GameVariant{
    @Override
    public String getName() {
        return "Tactic Game";
    }

    @Override
    public Deck createClanDeck() {
        return new Deck();
    }

    @Override
    public Deck createTacticDeck() {
        return new Deck(false);
    }

    @Override
    public int getHandSize() {
        return 7;
    }

}
