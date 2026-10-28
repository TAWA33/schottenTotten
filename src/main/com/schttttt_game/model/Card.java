package main.com.schttttt_game.model;

import java.util.List;

public interface Card {
    List<String> COLORS = List.of("Blue", "Green", "Red", "Purple", "Yellow", "Grey");

    int getValue();
    String getColor();
}
