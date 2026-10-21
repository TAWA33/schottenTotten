package MAIN.COM.schttttt_game.model;

import java.util.List;

public class Card {
    private int value;
    private String color;
    public static List<String> COLORS = List.of("Blue", "Green", "Red", "Purple", "Yellow", "Grey");

    public Card(int value, String color){
        if (value < 1 || value > 9){
            throw new IllegalArgumentException("Value must be between 1 and 9. Received : " + value);
        }
        if (!COLORS.contains(color)) {
            throw new IllegalArgumentException("Invalid Color : " + color + ". Authorized colors : " + COLORS);
        }
        this.value = value;
        this.color = color;
    }

    @Override
    public String toString() {
        return color + " " + value;
    }

    public int getValue(){
        return value;
    }
    public String getColor(){
        return color;
    }
}
