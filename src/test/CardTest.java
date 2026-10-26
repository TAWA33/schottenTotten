package test;

import static org.junit.Assert.*;

import org.junit.Test;

import main.com.schttttt_game.model.Card;

public class CardTest {

    @Test
    public void testGetValue() {
        Card test_card = new Card(3, "Blue");
        assertEquals("Incorrect Value", 3, test_card.getValue());
    }

    @Test
    public void testGetColor() {
        Card test_card = new Card(3, "Blue");
        assertEquals("Incorrect Color", "Blue", test_card.getColor());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCardInvalidValueTooHigh() {
        new Card(12, "Blue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCardInvalidValueTooLow() {
        new Card(0, "Blue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCardInvalidColor() {
        new Card(3, "Orange");
    }

    @Test
    public void testAllValidCombinations() {
        for (String color : Card.COLORS) {
            for (int v = 1; v <= 9; v++) {
                Card c = new Card(v, color);
                assertEquals(v, c.getValue());
                assertEquals(color, c.getColor());
            }
        }
    }

    @Test
    public void testToString() {
        assertEquals("Red 5", new Card(5, "Red").toString());
    }

    @Test
    public void testColorsList() {
        assertEquals(6, Card.COLORS.size());
        assertTrue(Card.COLORS.containsAll(
            java.util.List.of("Blue", "Green", "Red", "Purple", "Yellow", "Grey")));
    }
}