package main.com.schttttt_game.model;

public interface GameVariant {
    String getName(); // Classic or Tactic
    Deck createClanDeck(); // 54
    Deck createTacticDeck(); // 0 or 10
    int getHandSize(); // 6 or 7

}
