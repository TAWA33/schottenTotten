package main.com.schttttt_game.model;

public class GameFactory {
    public static GameVariant createVariant(int choice) {
        return switch (choice) {
            case 1 -> new ClassicVariant();
            case 2 -> new TacticVariant();
            default -> throw new IllegalArgumentException("Unknown variant : " + choice + " (expected : 1 or 2)");
        };
    }

}
