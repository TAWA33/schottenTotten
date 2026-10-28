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
        Deck d = new Deck(false);
        d.addCard(new TacticCard(TacticType.JOKER));
        d.addCard(new TacticCard(TacticType.JOKER));
        d.addCard(new TacticCard(TacticType.SPY));
        d.addCard(new TacticCard(TacticType.SHIELD_BEARER));
        d.addCard(new TacticCard(TacticType.BLIND_MAN_BLUFF));
        d.addCard(new TacticCard(TacticType.MUD_FIGHT));
        d.addCard(new TacticCard(TacticType.RECRUITER));
        d.addCard(new TacticCard(TacticType.STRATEGIST));
        d.addCard(new TacticCard(TacticType.BANSHEE));
        d.addCard(new TacticCard(TacticType.TRAITOR));
        return d;
    }

    @Override
    public int getHandSize() {
        return 7;
    }

}
