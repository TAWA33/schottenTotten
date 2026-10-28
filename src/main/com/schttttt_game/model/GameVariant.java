package main.com.schttttt_game.model;

public interface GameVariant {
    String getName(); // Classic or Tactic
    Deck createDeck(); // 54 or 54+10
    int getHandSize(); // 6 or 7

}
